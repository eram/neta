package com.netalign.netascutter;

import com.netalign.netascutter.interfaces.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import java.net.*;
import java.util.*;
/***  see http://g.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html */
import EDU.oswego.cs.dl.util.concurrent.*; //PooledExecutor, LinkedQueue

import org.apache.log4j.*;

public class Scutter implements IUrlListener {
    private static final int SLEEP_TIME = 50;
    private static final int MAX_SLEEPS_COUNTER = Integer.MAX_VALUE;//60*100/SLEEP_TIME;
	private static final int NUM_OF_THREADS = 6;
	private static final int SEEN_SIZE_THRESHOLD = 100;
	
    static Logger logger = Logger.getLogger(Scutter.class);
    List<URL> urls = new ArrayList<URL>();
    HashSet<URL> seen = new HashSet<URL>();
    Collection<IAggregator> aggregators;
	private int maxNumOfUrls;
	private String domain;

    /**
     * Spring AOP lookup method
     * @return
     */
    public IRetriever getRetriever() {
        return null;
    }
    
    public Scutter() {
    	setMaxNumOfUrls(0);
    	setDomain(Constants.EMPTY_STRING);
    }        
    
    public int getMaxNumOfUrls() {
		return maxNumOfUrls;
	}

	public void setMaxNumOfUrls(int maxNumOfUrls) {
		this.maxNumOfUrls = maxNumOfUrls;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public void start() throws Exception {
		go(Constants.EMPTY_STRING_ARRAY);
	}

	public void go(String[] seed) throws Exception {
    	logger.debug("Starting the scutter");
    	int sleepsCounter = 0;
        PooledExecutor exec = new PooledExecutor(new LinkedQueue());
        exec.setMaximumPoolSize(NUM_OF_THREADS);
        exec.createThreads(NUM_OF_THREADS);            
        initUrlsFromArgs(seed);        
        while (true) {         	
            try {
                Thread.sleep(SLEEP_TIME);
                sleepsCounter++;
            } catch (InterruptedException e) {}
            while (urls.size() > 0) { 
            	sleepsCounter = 0;
                try {
                    IRetriever r;
                    URL url;
                    synchronized (urls) {
                        url = urls.remove(0);
                    }
                    logger.debug("Putting " + url.toString() + " into the execute queue");
                    r = getRetriever();
                    r.setUrl(url);
                    exec.execute(r);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
            if (sleepsCounter > MAX_SLEEPS_COUNTER) {
            	break;
            }
        }
        exec.shutdownNow(); // shutdown all the threads
        logger.debug("Closing the scutter");
    }

    protected void initUrlsFromArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            addURL(args[i]);
        }
    }
    
    @Override
    public boolean addSeeAlsos(Model incoming) {
    	boolean added = false;
        for ( NodeIterator iter = incoming.listObjectsOfProperty(RDFS.seeAlso); iter.hasNext() ;) {
        	Resource also = (Resource) iter.nextNode();
            if ( addURL(also.toString()) ) {
            	added = true;
            }
        }        
        return added;
    }

    @Override
    public boolean addURLUnchecked(URL url) {
    	if (maxNumOfUrlsExceeded()) {
    		logger.debug("Max number of URLs exceeded, will not add " + url.toString()); 
    	} else if (outOfDomain(url)) {
    		logger.debug("URL out of domain, will not add " + url.toString()); 
    	} else { // add
	        synchronized (urls) {
	            urls.add(url);
	            seen.add(url);
	            logger.debug("Added url to the queue: " + url.toString());
	            logger.debug("Queue size " + Integer.toString(urls.size())); 
	            if (seen.size()%SEEN_SIZE_THRESHOLD == 0) {
	            	logger.debug("Seen " + Integer.toString(seen.size()) + " URLs" ); 
	            }
	            return true;
	        }
    	} return false;
    }

	@Override
    public boolean addURLUnchecked(String url) {
        try {
            return addURLUnchecked(new URL(url));
        } catch (MalformedURLException e) {
            logger.error(e);
        } return false;
    }

    @Override
    public boolean addURL(URL url) {
        if (!seen.contains(url)) {
        	return addURLUnchecked(url);
        } else {
            logger.debug("Already seen " + url);
        } return false;
    }

    @Override
    public boolean addURL(String url) {
        if (url != null && !url.isEmpty()) {
	    	try {
	    		return addURL(new URL(url));
	        } catch (MalformedURLException e) {
	            logger.error(e);
	        }
        } return false;
    }
    
    @Override
    public int getNumOfUrls() {
    	return urls.size();
    }
    
    private boolean maxNumOfUrlsExceeded() {
		if (maxNumOfUrls > 0 && seen.size() > maxNumOfUrls) {
			return true;
		}
		return false;
	}
    
    private boolean outOfDomain(URL url) {
		if (domain.isEmpty()) {
			return false;
		} else if (url.getHost().equals(domain)) {
			return false;
		}
		return true;
	}
}
