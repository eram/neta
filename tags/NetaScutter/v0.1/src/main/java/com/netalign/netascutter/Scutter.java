/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter;

import com.netalign.netascutter.interfaces.*;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.*;

/**
 * The <code>Scutter</code> class is the main running component of theNetaScutter system.
 * The Scutter implements IThreadedUrlListener (ao also IUrlListener) and Runnable, thus accepting new URLs and
 * also running tasks to process the URL with the <code>run()</code> method.
 * <p>
 * 
 * @author yoavram
 * @see Runnable
 * @see IUrlListener
 * @see IThreadedUrlListener
 * @see ThreadedURLListener
 * 
 */
public class Scutter extends ThreadedURLListener implements IThreadedUrlListener, Runnable {
	public static final int SEEN_SIZE_THRESHOLD = 100;

	/**
	 * Number of bytes that can be read and then reset on the input stream
	 * buffer
	 * 
	 * @see BufferedInputStream#mark(int)
	 */
	private static final int BUFFERED_INPUT_STREAM_MARK_LIMIT = 10000;
	private static Logger logger = Logger.getLogger(Scutter.class);

	ThreadPoolExecutor executer;
	// init via setters
	private int maxNumOfUrls;
	private int corePoolSize;
	private int maxPoolSize;
	
	private Pattern pattern;

	// internal counter
	private AtomicInteger numOfElements;
	private AtomicInteger numOfFetches;

	private List<IFetcher> fetchers;

	private List<IParser<?>> parsers;

	private Map<Class<?>, List<IHandler<?>>> handlers;

	// Constructors
	public Scutter() {
		numOfElements = new AtomicInteger(0);
		numOfFetches = new AtomicInteger(0);		
	}
	
	/**
	 * this method is called to init the thread pool
	 */
	private void init() {
		// these two variables disable idle threads from ever terminating prior
		// to shut down.
		long excessThreadKeepAliveTime = Long.MAX_VALUE;
		TimeUnit timeUnit = TimeUnit.NANOSECONDS; // 		
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		executer = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
				excessThreadKeepAliveTime, timeUnit, queue,
				new RetryOrDiscardOnShutdownPolicy(logger));
		// to make sure that all threads are ready to run
		executer.prestartAllCoreThreads();
		// using Executors.defaultThreadFactory() for thread creation

		logger.debug("Initialized a scutter with the following parameters:"
				+ "\n\t\t\t corePoolSize:\t\t\t"
				+ Integer.toString(corePoolSize)
				+ "\n\t\t\t maxPoolSize:\t\t\t" + Integer.toString(maxPoolSize)
				+ "\n\t\t\t excessThreadKeepAliveTime:\t"
				+ Long.toString(excessThreadKeepAliveTime)
				+ "\n\t\t\t timeUnit:\t\t\t" + timeUnit.toString()
				+ "\n\t\t\t queue:\t\t\t\t" + queue.getClass().toString()
				+ "\n\t\t\t executer:\t\t\t" + executer.getClass().toString()
				+ "\n\t\t\t RejectedExecutionHandler:\t"
				+ executer.getRejectedExecutionHandler().getClass().toString()
				+ "\n\t\t\t SEEN_SIZE_THRESHOLD:\t\t"
				+ Integer.toString(SEEN_SIZE_THRESHOLD)
				+ "\n\t\t\t maxNumOfUrls:\t\t\t"
				+ Integer.toString(maxNumOfUrls) + "\n\t\t\t pattern:\t\t\t"
				+ pattern.toString());
	}

	// Setters
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	// Runnable Methods
	@Override
	public void run() {
		if (sizeFutureGlobalURLs() == 0) { //TODO wait until it is not empty?
			logger.debug("URL queue is empty");
			return;
		}
		String url = popURL();
		if (url == null || url.isEmpty()) {
			logger.warn("Started and failed getting a valid URL");
			return;
		}
		
		do {					
			logger.info("Started with " + url);
			// fetch the URL - URL is consumed by successful fetcher
			IFetcher fetcher = null; // out of loop because we need to close it
			// after parsing
			BufferedInputStream inputStream = null;
			for (Iterator<IFetcher> it = getFetchers().iterator(); it.hasNext()
					&& inputStream == null;) {
				if ((fetcher = it.next()) == null) {
					finishURL(url);
					continue;
				}
				inputStream = fetcher.fetch(url);
			} // end fetch loop
			// check if any fetches succeeded
			if (fetcher == null || inputStream == null) {
				logger.warn("Failed fetching " + url);
				finishURL(url);			
				continue;			
			} else {
				logger.info("Succeeded fetching " + url);
				inputStream.mark(BUFFERED_INPUT_STREAM_MARK_LIMIT); // mark the
				// start of the
				// input stream
				numOfFetches.incrementAndGet();
			}
			// parse the input stream - it is consumed by a successful parser
			List<?> parsedElements = Collections.emptyList();
			for (Iterator<IParser<?>> it = getParsers().iterator(); it
					.hasNext()
					&& parsedElements.isEmpty();) {
				IParser<?> parser = it.next();
				if (parser == null){
					finishURL(url);			
					continue;
				}
				try {
					// go back to the mark - should be the start of the input
					// stream
					inputStream.reset();
					// if succeeded going back to start of stream - parse!
					parsedElements = parser.parse(inputStream, url);
				} catch (IOException e) {
					logger.warn("Can't reset input stream: " + e);
				}
			} // end of parse loop
			// close the input stream
			fetcher.close(inputStream);
			// check if any parsing succeeded
			if (parsedElements.isEmpty()) {
				logger.info("No elements parsed from " + url);
				finishURL(url);
				continue;
			} else {
				int size = parsedElements.size();
				numOfElements.addAndGet(size);
				logger.info("Succeeded parsing " + Integer.toString(size)
						+ " elements from " + url);
			}
			// handle the parsed items - items are not consumed by successful
			// handlers
			Map<Class<?>, List<IHandler<?>>> handlerMap = getHandlerMap();
			for (Object element : parsedElements) {
				if (element == null) {
					finishURL(url);
					continue; // leave this line here to avoid exception, but
				}
				// make
				// sure parsers don't put nulls in the list
				List<Class<?>> interfaceList = new ArrayList<Class<?>>(Arrays
						.asList(element.getClass().getInterfaces()));
				interfaceList.add(element.getClass());
				for (Class<?> interfaze : interfaceList) {
					if (handlerMap.containsKey(interfaze)) {
						for (IHandler handler : handlerMap.get(interfaze)) {
							if (handler == null)
								continue; // should not happen as hashtable
							// doesn't
							// allow null values
							handler.handle(element, url);
							logger.info("Handled element of type "
									+ getClassName(element) + " from " + url);
						}
					}
				}
				
			}
			finishURL(url);
			logger.info("Finished with " + url);
		} while ( (url = popThreadURL()) != null);
		logger.debug("Thread finished");
	}

	// Scutter Methods
	public void shutdown() {
		logger.info("Shutting down scutter: seen " + getNumOfUrlsSeen()
				+ " URLs, fetched " + getNumOfFetches() + " URLs and parsed "
				+ getNumOfElements() + " elements");
		executer.shutdown();
	}

	// Scutter Getters Setters
	private List<IParser<?>> getParsers() {
		return parsers;
	}

	private List<IFetcher> getFetchers() {
		return fetchers;
	}

	private Map<Class<?>, List<IHandler<?>>> getHandlerMap() {
		return handlers;
	}

	public void setFetchers(List<IFetcher> fetcherList) {
		this.fetchers = fetcherList;
	}

	public void setParsers(List<IParser<?>> parserList) {
		this.parsers = parserList;
	}

	public void setHandlers(List<IHandler<?>> handlerList) {
		// associate in the map each handler with the parameter it likes
		handlers = new Hashtable<Class<?>, List<IHandler<?>>>();
		for (IHandler<?> handler : handlerList) {
			for (Method m : handler.getClass().getMethods()) {
				if (m.getName().equals("handle")) {
					// the first parameter of handle is the one we want
					Class<?> cls = m.getParameterTypes()[0];
					if (cls.equals(Object.class) ) {
						continue; // XXX handle method must be defined on a Class not just any Object
					}
					List<IHandler<?>> list = handlers.get(cls);
					if (list == null) {
						list = new ArrayList<IHandler<?>>();
					}
					list.add(handler);
					handlers.put(cls, list);
					break; // don't go over any more methods
				}

			}
		}
	}

	public int getNumOfFetches() {
		return numOfFetches.get();
	}

	public int getNumOfElements() {
		return numOfElements.get();
	}

	@Override
	public int getActiveCount() {
		if (executer == null) return 0;
		return executer.getActiveCount();
	}

	// IUrlListener Methods
	@Override
	public synchronized boolean addGlobalURL(String url) {
		if (executer==null) init();
		if ( super.addGlobalURL(url) ) {
			executer.execute(this);
			int futSize = sizeFutureGlobalURLs();
			int exeSize = executer.getQueue().size();
			logger.debug("Added URL and executed task, queue size " + sizeFutureGlobalURLs() + ", URL: " + url);
			assert (futSize != exeSize);
			return true;
		} else {
			logger.debug("Didn't add URL, queue size " + sizeFutureGlobalURLs() + ", URL: " + url);
			return false;
		}
	}

	@Override
	public synchronized boolean addGlobalURLUnchecked(String url) {
		if (executer==null) init();
		if ( super.addGlobalURLUnchecked(url) ) {
			executer.execute(this);
			int futSize = sizeFutureGlobalURLs();
			int exeSize = executer.getQueue().size();
			logger.debug("Added unchecked URL and executed task, queue size " + sizeFutureGlobalURLs() + ", URL: " + url);
			assert (futSize != exeSize);
			return true;
		} else {
			logger.debug("Didn't add URL, queue size " + sizeFutureGlobalURLs() + ", URL: " + url);
			return false;
		}
	}

	@Override
	public boolean addURL(String url) {
		return addGlobalURL(url);
	}

	@Override
	public boolean addURLUnchecked(String url) {
		return addGlobalURLUnchecked(url);
	}
	
	@Override
	public void setPattern(String pattern) {
		this.pattern = Pattern.compile(pattern);
	}

	@Override
	public void setMaxNumOfUrls(int maxNumOfUrls) {
		this.maxNumOfUrls = maxNumOfUrls;
	}
	
	// Private methods
	@Override
	protected boolean isMaxNumOfURLsExceeded() {
		int size = sizePastGlobalURL();
		if (maxNumOfUrls > 0 && size > maxNumOfUrls) {
			logger.debug("Maximum number of URLs exceeded");
			return true;
		}
		return false;
	}

	@Override
	protected boolean isNotMatch(String url) {		
		if (pattern == null || pattern.toString().isEmpty()) {
			return false;
		} else {
			Matcher matcher = pattern.matcher(url);
			if (matcher.matches()) {
				logger.debug("URL matched: " + url + " with pattern " + pattern.toString());
				return false;
			} else {
				logger.debug("URL did not match: " + url + " with pattern " + pattern.toString());
				return true;
			}
		}
	}

	private String getClassName(Object obj) {
		String fullname = obj.getClass().toString();
		int lastdot = fullname.lastIndexOf(46); // 46 is the ascii code for .
		// (dot)
		return fullname.substring(lastdot + 1);
	}

	/**
	 * A handler for rejected tasks that retries <tt>execute</tt> once and the
	 * next time it gets the same task it is discarded , unless the executor is
	 * shut down, in which case the task is discarded.
	 * <p>
	 * The handler logs it's action on a given logger.
	 * 
	 * @author yoavram
	 * @see RejectedExecutionHandler
	 */
	private class RetryOrDiscardOnShutdownPolicy implements
			RejectedExecutionHandler {
		private Logger logger;
		private Set<Integer> seen;

		public RetryOrDiscardOnShutdownPolicy(Logger logger) {
			this.logger = logger;
			seen = new HashSet<Integer>();
		}

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			if (!executor.isShutdown()) {
				if (!seen.contains(r)) {
					seen.add(r.hashCode());
					logger.info("Retrying task '" + r.toString() + "'");
					executor.execute(r);
				} else {
					logger.info("Task '" + r.toString()
							+ "' has already been retried, discarding");
				}
			} else {
				logger.info("Executer was shutdown, task '" + r.toString()
						+ "' discarded");
			}
		}

	}
}
