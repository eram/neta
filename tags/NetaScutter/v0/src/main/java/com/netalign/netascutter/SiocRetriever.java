package com.netalign.netascutter;

import com.netalign.netascutter.interfaces.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.arp.*;
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.netalign.sioc.*;
import org.apache.log4j.*;

/**
 * The <code>SiocRetriever</code> class implements the {@link IRetriever} interface.
 * <br>It reads an RDF file using {@link JenaReader} which parses the RDF file into a {@link Model}.
 * <br>Then it looks for SIOC:Post, SIOC:Forum and FOAF:Person elements in the RDF file.
 * <br>If found, the retriever builds objects that represent the RDF elements in java and sends them to 
 * it's aggregators.
 *   
 * @author yoavram
 * 
 * @see IRetriever
 * @see IAggregator
 * @see ISiocPost
 * @see ISiocForum
 * @see IFoafPerson
 * @see PostBuilder
 * @see ForumBuilder
 * @see PersonBuilder
 */
public class SiocRetriever implements IRetriever {

    protected URL url;
    protected Collection<IAggregator> aggregators;
    protected boolean _ok = true;
    static Logger logger = Logger.getLogger(SiocRetriever.class);

    public SiocRetriever() {
    }

    /**
     * Constructs a SiocRetriever
     * @param url	a URL to work with.
     * @param agg	a {@link Collection} of {@link IAggregator} to send processed data to 
     */
    public SiocRetriever(URL url, Collection<IAggregator> agg) {
        this.url = url;
        aggregators = agg;
    }

    @Override
    public void run() {
    	String urlStr = url.toString();
        try {            
            logger.info("Retrieving " + urlStr);
            JenaReader reader = new JenaReader();
            reader.setErrorHandler(this);
            Model model = ModelFactory.createMemModelMaker().createDefaultModel();
            reader.read(model, urlStr);
            logger.info("Finished retrieving " + urlStr);
            if (_ok) { //may be set to false in fatalError() and error()
                List<Object> objects = extractObjects(model, urlStr);                                
                sendToAggregators(objects, urlStr);                
            } else {
                logger.warn("Unexpected errors, NOT sending to aggregators: " + urlStr);
            }
        } catch (DoesNotExistException e) {
        	logger.error("Can't find URL " + urlStr +": " + e.getMessage());
        } catch (NullPointerException e) {
        	logger.error("Null Pointer Exception at " + urlStr);
        }catch (Throwable t) {
        	if (t.getCause() instanceof NoRouteToHostException) {
        		logger.error(t.getCause().getMessage() + " : " + urlStr);
        	} else if (t.getCause() instanceof ConnectException) {
        		logger.error(t.getCause().getMessage() + " : " + urlStr);
        	} else if (t.getCause() instanceof UnknownHostException) {
        		logger.error("Can't find host: " + t.getCause().getMessage());
        	} else if (t.getCause() instanceof SocketException) { // SocketException extends IOException
        		logger.error(t.getCause().getMessage()+ ": " + urlStr);
        	} else if (t.getCause() instanceof IOException) {
        		logger.error(t.getCause().getMessage());
        	}  else {
	            StringWriter stack = new StringWriter();
	            t.printStackTrace(new PrintWriter(stack));
	            logger.error(stack.toString(), t);
        	}
        } finally {
            logger.debug(Thread.currentThread().getName() + " finished with " + urlStr);
        }
    }

    private List<Object> extractObjects(Model model, String urlStr) {
        // extract users posts and forums
        List<Object> list = new ArrayList<Object>();
        list.add(extractPrimaryPerson(model, urlStr));
        list.add(extractPrimaryPost(model, urlStr));
        list.addAll(extractForums(model, urlStr)); 
        return list;
    }
       
    private IFoafPerson extractPrimaryPerson(Model model, String urlStr) {
        // check for foaf:person as a primary topic of the RDF model
        FOAFGraphImpl g = new FOAFGraphImpl(model);      // this will be used to extract a post object form the rdf model    
        IFoafPerson person = g.findPrimaryPerson(urlStr);   // finds the primary foaf:psot and parses it to an object
        // TODO this doesnt find all persons... for example, http://jibbering.com/foaf.rdf
        if (person == null) {
            logger.debug("No person extracted from "+ urlStr);
        } else {
            logger.debug("Found primary person in " + urlStr);
        }        
        return person;
    }
    
    private ISiocPost extractPrimaryPost(Model model, String urlStr) {
    	// check for sioc:post as a primary topic of the RDF model
        FOAFGraphImpl g = new FOAFGraphImpl(model);      // this will be used to extract a psot object form the rdf model    
        ISiocPost post = g.findPrimaryPost(urlStr);   // finds the primary sioc:post and parses it to an object        
        if (post == null) {
            logger.debug("No post extracted from "+ urlStr);
        } else {
            logger.debug("Found primary post in " + urlStr);
        }
        
        return post;
    }
    
    private List<ISiocForum> extractForums(Model model, String urlStr) {
        // check for all available sioc:forum elements in Model
        FOAFGraphImpl g = new FOAFGraphImpl(model);      // this will be used to extract forum objects form the rdf model    
        List<ISiocForum> forums = g.findAllForums();   // finds all sioc:forum and parses it to an object        
        if (forums == null || forums.isEmpty()) {
            logger.debug("No forum extracted from "+ urlStr);
        } else {
            logger.debug("Found forums in " + urlStr);
        }
        
        return forums;
    }

    private void sendToAggregators(List<Object> objects, String urlStr) {
        // iterate over the aggregators and the objects - send each of the objects to each of the aggregators
        // TODO - which should be the first loop? aggs or objects?
        for (Object something : objects) {
        	if (something != null) {
	            for (IAggregator aggregator : aggregators) {
	                aggregator.add(something, urlStr);
	            }
        	}
        }
    }
    
    @Override
    public void warning(Exception e) {
        NDC.push(url.toString());
        logger.warn(e);
        NDC.pop();
    }
  
    @Override
    public void error(Exception e) {
        NDC.push(url.toString());
        logger.error(e);
        NDC.pop();
        _ok = false;
    }
    
    @Override
    public void fatalError(Exception e) {
        NDC.push(url.toString());
        logger.error(e);
        NDC.pop();
        _ok = false;
    }

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public void setAggregators(Collection<IAggregator> aggregators) {
        this.aggregators = aggregators;
    }    
}
