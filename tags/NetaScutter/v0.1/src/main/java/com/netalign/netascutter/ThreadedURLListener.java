package com.netalign.netascutter;

import java.util.*;
import org.apache.log4j.Logger;
import com.netalign.netascutter.interfaces.IThreadedUrlListener;

/**
 * The <code>ThreadedURLListener</code> class implements the {@link IThreadedUrlListener} interface. 
 * <p>
 * 
 * <p>
 * @author yoavram
 * 
 */
public abstract class ThreadedURLListener implements IThreadedUrlListener {

	protected static Logger logger = Logger.getLogger(ThreadedURLListener.class);
	
	public class StringStackThreadLocal extends ThreadLocal<Stack<String>> {
		@Override
		protected Stack<String> initialValue() {
			return new Stack<String>();
		}
	}
	public class IntegerHashSetThreadLocal extends ThreadLocal<HashSet<Integer>> {
		@Override
		protected HashSet<Integer> initialValue() {
			return new HashSet<Integer>();
		}
	}
	// LIFO
	private static StringStackThreadLocal threadFuture ;
	private static IntegerHashSetThreadLocal threadPast ;

	private List<String> future;
	private Set<Integer> past;
	private Map<Integer, Object> present;

	public ThreadedURLListener() {
		future = new ArrayList<String>();
		past = new HashSet<Integer>();
		present = new HashMap<Integer, Object>();
		if (threadFuture == null)
			threadFuture = this.new StringStackThreadLocal();
		if (threadPast == null)
			threadPast = this.new IntegerHashSetThreadLocal();
		logger.debug("Init completed");
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#addhGlobalURL(java.lang.String)
	 */
	public synchronized boolean addGlobalURL(String url) {
		// check argument
		if (url == null || url.isEmpty()) {
			logger.debug("URL is null or empty");
			return false;
		}
		if (isNotMatch(url) || isMaxNumOfURLsExceeded()) {
			// log messages should be implemented inside these methods
			return false;
		}
		// get hash code
		int hash = url.hashCode();

		// check if url is in past urls
		if (past.contains(hash)) {
			logger.debug("Global queue already seen " + url);
			return false;
		} else {
			// add url to future and past urls
			future.add(url);
			past.add(hash);
			logger.debug("Global queue size " + sizeFutureGlobalURLs() + ", added " + url);
			return true;
		}		
	}
	
	@Override
	public synchronized boolean addGlobalURLUnchecked(String url) {
		// check argument
		if (url == null || url.isEmpty()) {
			logger.debug("URL is null or empty");
			return false;
		}
		// get hash code
		int hash = url.hashCode();
		// add url to future and past urls
		future.add(url);
		past.add(hash);
		logger.debug("Global queue size " + sizeFutureGlobalURLs() + ", added " + url);		
		return true;
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#pushThreadURL(java.lang.String)
	 */
	public synchronized boolean pushThreadURL(String url) {
		// check argument
		if (url == null || url.isEmpty()) {
			logger.debug("URL is null or empty");
			return false;
		}
		// get hash code
		int hash = url.hashCode();
		// check if url is in past urls of thread
		if (threadPast.get().contains(hash)) {
			logger.debug("Thread queue already seen " + url);
			return false;
		} else {
			// push url to future and past urls of thread
			threadFuture.get().push(url);
			threadPast.get().add(hash);
			// add to global past and remove (if exists) from global future 
			past.add(hash);
			future.remove(url);
			logger.debug("Thread queue size " + sizeFutureThreadURLs() + ", added " + url);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#pushThreadURLUnchecked(java.lang.String)
	 */
	public synchronized boolean pushThreadURLUnchecked(String url) {
		// check argument
		if (url == null || url.isEmpty()) {
			logger.debug("URL is null or empty");
			return false;
		}
		// get hash code
		int hash = url.hashCode();
		// push url to future and past urls of thread
		threadFuture.get().push(url);
		threadPast.get().add(hash);
		// add to global past and remove (if exists) from global future 
		past.add(hash);
		future.remove(url);
		logger.debug("Thread queue size " + sizeFutureThreadURLs() + ", added " + url);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#popURL()
	 */
	public String popURL() {
		String url = null;
		// pop from thread future first
		try {
			synchronized (this) {
				url = threadFuture.get().pop();
			}
		} catch (EmptyStackException e) {
			// go on then, try to get from the global future
		}
		// check if url from thread future is valid
		if (url == null || url.isEmpty()) {
			// get url from global future
			synchronized (this) {
				url = future.remove(0);
			}
		}
		// check if url from global future is valid
		if (url == null || url.isEmpty()) {
			url = null;
		} else {
			// url is valid
			Object lock = null;
			// get hash code
			int hash = url.hashCode();
			// check if the url is in present - meaning some other thread has it
			// already
			synchronized (present) {
				lock = present.get(hash);
				if (lock == null) {
					// no other thread has the url, put it in present
					Object newLock = new Object();
					logger.debug("Putting new lock " + newLock.toString() + " for URL " + url);
					present.put(hash, newLock);
				}
			}
			if (lock != null) { 
				// some other thread has it, but this thread needs it done.
				// wait till he is done and then dump the url
				logger.debug("Waiting on " + lock.toString());
				try {
					synchronized (lock) {
						lock.wait();
					}
					// notified before wait
				} catch (InterruptedException e) {
					; // nothing to do, notified
				}
				logger.debug("Notified on " + lock.toString());
				// some other thread got it, but this thread doesn't need it.
				// go on to next url
				return popURL();
			} 			
		}
		if (url != null) {
			logger.debug("Popped from global queue " + url);
		}
		return url;
	}
	
	@Override
	public String popThreadURL() {
		String url = null;
		// pop from thread future first
		try {
			synchronized (this) {
				url = threadFuture.get().pop();
			}
		} catch (EmptyStackException e) {
			// go on then, try to get from the global future
		}		
		// check if url is valid
		if (url == null || url.isEmpty()) {
			url = null;
		} else {
			// url is valid
			Object lock = null;
			// get hash code
			int hash = url.hashCode();
			// check if the url is in present - meaning some other thread has it
			// already
			synchronized (present) {
				lock = present.get(hash);
				if (lock == null) {
					// no other thread has the url, put it in present
					Object newLock = new Object();
					logger.debug("Putting new lock " + newLock.toString() + " for URL " + url);
					present.put(hash, newLock);
				}
			}
			if (lock != null) {
				// some other thread has it, but this thread needs it done.
				// wait till he is done and then dump the url	
				logger.debug("Waiting on " + lock.toString());
				try {	
					synchronized (lock) {
						lock.wait();
					}
					// notified before wait
				} catch (InterruptedException e) {
					; // nothing to do, notified
				}
				logger.debug("Notified on " + lock.toString());
				// some other thread got it, but this thread doesn't need it.
				// go on to next url
				return popThreadURL();
			} 
		}
		if (url != null) {
			logger.debug("Popped from thread queue " + url);
		}
		return url;
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#sizeFutureGlobalURLs()
	 */
	public synchronized int sizeFutureGlobalURLs() {
		return future.size();
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#sizePastGlobalURL()
	 */
	public synchronized int sizePastGlobalURL() {
		return past.size();
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#sizePresentURLs()
	 */
	public synchronized int sizePresentURLs() {
		return present.size();
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#sizeFutureThreadURLs()
	 */
	public int sizeFutureThreadURLs() {
		return threadFuture.get().size();
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#sizePastThreadURLs()
	 */
	public int sizePastThreadURLs() {
		return threadPast.get().size();
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IThreadedUrlListener#finishURL(java.lang.String)
	 */
	public void finishURL(String url) {
		Object lock = null;
		// get hash code
		int hash = url.hashCode();
		// get the lock of this url
		synchronized (present) {
			// sync block is large so that no one can add the lock back before i notify
			lock = present.remove(hash);

			// check that it is not null
			if (lock != null) {
				// release the lock
				logger.debug("Notifying all on " + lock.toString() + " for " + url);
				synchronized (lock) {
					lock.notifyAll();
				}
			} else {
				logger.warn("Failed notifying on " + url);
			}
		}
	}
	

	@Override
	public synchronized boolean inPastGlobalURLs(String url) {
		// check argument
		if (url == null || url.isEmpty()) {
			return false;
		}
		return past.contains(url);
	}

	@Override
	public synchronized boolean inPastThreadURLs(String url) {
		// check argument
		if (url == null || url.isEmpty()) {
			return false;
		}
		// get hash code
		int hash = url.hashCode();
		return threadPast.get().contains(hash);
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IUrlListener#addURL(java.lang.String)
	 */
	@Override
	public boolean addURL(String url) {
		return addGlobalURL(url);
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IUrlListener#addURLUnchecked(java.lang.String)
	 */
	@Override
	public boolean addURLUnchecked(String url) {
		return addGlobalURLUnchecked(url);
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IUrlListener#getNumOfUrls()
	 */
	@Override
	public int getNumOfUrls() {
		return sizeFutureGlobalURLs();
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IUrlListener#getNumOfUrlsSeen()
	 */
	@Override
	public int getNumOfUrlsSeen() {
		return sizePastGlobalURL();
	}

	/* (non-Javadoc)
	 * @see com.netalign.netascutter.IUrlListener#removeURL()
	 */
	@Override
	public String removeURL() {
		return popURL();
	}
	/**
	 * This method returns false if maximum number of URLs was exceeded
	 * Must be override in extensions.
	 * Override methods should implement logging
	 * @return true if url is NOT vaild or false if it is
	 */
	protected abstract boolean isMaxNumOfURLsExceeded();
	
	/**
	 * This method returns false if the url doesn't match a predefined regexp expression.
	 * Must be override in extensions.
	 * Override methods should implement logging
	 * @return true if url is NOT vaild or false if it is
	 */
	protected abstract boolean isNotMatch(String url);
}
