package com.netalign.netascutter.utils;

import java.io.*;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.netalign.netascutter.Constants;
import com.netalign.netascutter.interfaces.IThreadedUrlListener;

/**
 * The <code>VBulletinImporter</code> class creates URLs of RDF SIOC and FOAF resources of a <i>vBulletin</i>
 * forums/boards site that has a working SIOC exporter.
 * <p>
 * The class extends the {@link Thread} class, implementing a <code>run</code> method that iterates over 
 * the number of users, forums, threads and posts in the site, creates the URL string for each resource and
 * adds the URL to the a listener that implements the {@link IThreadedUrlListener} interface.
 * <p>
 * The class is initialized by giving it a listener object, the site URL and the last index of the users, forums, threads and
 * posts in the site. Then it is started using the <code>start</code> method. The condiguration parameters may be givven
 * by setters or in a properties file. that is read in the <code>init</code> method.
 * 
 * @author yoavram 
 * @see IThreadedUrlListener
 * @see <a href="http://www.vbulletin.com/">vBulletin.com</a>
 */
public class VBulletinImporter extends Thread {

	private static final long COUNTER_MODULO = 25;
	private static final long SLEEP_TIME = 100;
	//TODO do this dynamically
	private static final String PROPERTIES_FILE = "C:\\neta\\src\\java\\NetaScutterWorkspace\\NetaScutter\\src\\main\\resources\\scutter.properties";
	private static final String PROPERTIES_PREFIX = "vbulletinimporter";

	private static Logger logger = Logger.getLogger(VBulletinImporter.class);

	private IThreadedUrlListener listener;
	private String baseUrl = Constants.EMPTY_STRING;
	private int posts = 0;
	private int users = 0;
	private int threads = 0;
	private int forums = 0;

	/**
	 * returns the base URL of the importer, such as "http://boards.us"
	 * @return
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * sets the base URL string of the importer, such as "http://boards.us"
	 * @param baseUrl
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * returns the last index to run on in the forums loop
	 * @return
	 */
	public int getForums() {
		return forums;
	}

	/**
	 * sets the last index to run on in the forums loop
	 * @param forums
	 */
	public void setForums(int forums) {
		this.forums = forums;
	}

	public VBulletinImporter() {
	}

	public VBulletinImporter(IThreadedUrlListener listener) {
		setListener(listener);
	}

	/**
	 * Sets the listener of the importer
	 * 
	 * @param listener
	 */
	public void setListener(IThreadedUrlListener listener) {
		this.listener = listener;
	}
	/**
	 * returns the last index to run on in the posts loop
	 * @param forums
	 */
	public int getPosts() {
		return posts;
	}
	/**
	 * sets the last index to run on in the posts loop
	 * @param forums
	 */
	public void setPosts(int posts) {
		this.posts = posts;
	}
	/**
	 * returns the last index to run on in the users loop
	 * @param forums
	 */
	public int getUsers() {
		return users;
	}
	/**
	 * sets the last index to run on in the users loop
	 * @param forums
	 */
	public void setUsers(int users) {
		this.users = users;
	}
	/**
	 * returns the last index to run on in the threads loop
	 * @param forums
	 */
	public int getThreads() {
		return threads;
	}
	/**
	 * sets the last index to run on in the threads loop
	 * @param forums
	 */
	public void setThreads(int threads) {
		this.threads = threads;
	}

	/**
	 * reads configuration from a properties file: vbulletinimporter.posts, .threads, .forums, .users, .baseUrl
	 * TODO change to public and remove from the run method
	 * @return
	 */
	private boolean init() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(PROPERTIES_FILE));
		} catch (Exception e) {
			logger.fatal("Can't open file " + PROPERTIES_FILE + ": "
					+ e.getMessage());
			return false;
		}
		if (posts == 0 && prop.get(PROPERTIES_PREFIX + ".posts") != null)
			posts = Integer.valueOf(prop.get(PROPERTIES_PREFIX + ".posts")
					.toString().trim());
		if (threads == 0 && prop.get(PROPERTIES_PREFIX + ".threads") != null)
			threads = Integer.valueOf(prop.get(PROPERTIES_PREFIX + ".threads")
					.toString().trim());
		if (forums == 0 && prop.get(PROPERTIES_PREFIX + ".forums") != null)
			forums = Integer.valueOf(prop.get(PROPERTIES_PREFIX + ".forums")
					.toString().trim());
		if (users == 0 && prop.get(PROPERTIES_PREFIX + ".users") != null)
			users = Integer.valueOf(prop.get(PROPERTIES_PREFIX + ".users")
					.toString().trim());
		if ((baseUrl == null || baseUrl.isEmpty())
				&& prop.get(PROPERTIES_PREFIX + ".baseUrl") != null)
			baseUrl = prop.get(PROPERTIES_PREFIX + ".baseUrl").toString()
					.trim();

		if (baseUrl.isEmpty() || baseUrl == null) {
			logger.fatal("No base URL");
			return false;
		}

		if (posts == 0 && threads == 0 && users == 0) {
			logger.fatal("No number of posts or users or threads");
			return false;
		}

		return true;
	}

	/**
	 * runs the iterations. 
	 * iteration cycle:
	 * <ul>
	 * <li> the importer waits for listener inactivity using a <code>listener.getActiveCount() > 0</code> statement</li>
	 * <li> the importer waits for listener queue to empty using a <code>listener.getNumOfUrls() > 0</code> statement</li>
	 * <li> the pattern of the listener is changed to match the type of resource of the following iteration.</li> 
	 * <li> iterate over the indexes of the resources, from zero to the number specified. before each iteration the 
	 * importer waits for the listener's queue to empty, as in step 2.</li>
	 * </ul>
	 * <p>
	 * TODO change patterns to generic
	 * TODO logs should have "percentage done"
	 */
	@Override
	public void run() {
		if (listener == null) {
			logger.error("No listener!");
			return;
		}
		if (!init())
			return;

		// must wait for the queue to empty before changing the pattern
		waitForInactive();
		waitForEmptyQueue();
		
		logger.debug("Started with users");
	    listener.setPattern("^http://.*boards\\.us/forums/foaf\\.php\\?u=.*$");	
		int usersSuccess = 0;
		for (int i = 1; i < users; i++) {
			waitForEmptyQueue();
			String url = baseUrl + "/forums/foaf.php?u="+ i;
			if (listener.addURL(url)) {
				if (++usersSuccess % COUNTER_MODULO == 0) {
					logger.debug("Successfully added " + usersSuccess
							+ " user URLs");
				}
			}
		}
		
		waitForInactive();
		waitForEmptyQueue();
				
		logger.debug("Started with forums");
		listener.setPattern("^http://.*boards\\.us/forums/sioc\\.php\\?sioc_type=forum&sioc_id=.*$");
		int forumsSuccess = 0;
		for (int i = 1; i < forums; i++) {
			waitForEmptyQueue();
			String url = baseUrl + "/forums/sioc.php?sioc_type=forum&sioc_id="
					+ i;
			if (listener.addURL(url)) {
				if (++forumsSuccess % COUNTER_MODULO == 0) {
					logger.debug("Successfully added " + forumsSuccess
							+ " forum URLs");
				}
			}
		}
		
		waitForInactive();
		waitForEmptyQueue();
				
		logger.debug("Started with threads");
		listener.setPattern("^http://.*boards\\.us/forums/sioc\\.php\\?sioc_type=thread&sioc_id=.*$");
		int threadsSuccess = 0;
		for (int i = 410; i < threads; i++) {
			waitForEmptyQueue();
			String url = baseUrl + "/forums/sioc.php?sioc_type=thread&sioc_id="
					+ i;
			if (listener.addURL(url)) {
				if (++threadsSuccess % COUNTER_MODULO == 0) {
					logger.debug("Successfully added " + threadsSuccess
							+ " thread URLs");
				}
			}
		}
		
		waitForInactive();
		waitForEmptyQueue();
				
		logger.debug("Started with posts");
		listener.setPattern("^http://.*boards\\.us/forums/sioc\\.php\\?sioc_type=post&sioc_id=.*$");
		int postsSuccess = 0;
		for (int i = 14007; i < posts; i++) {
			waitForEmptyQueue();
			String url = baseUrl + "/forums/sioc.php?sioc_type=post&sioc_id="
					+ i;
			if (listener.addURL(url)) {
				if (++postsSuccess % COUNTER_MODULO == 0) {
					logger.debug("Successfully added " + postsSuccess
							+ " post URLs");
				}
			}
		}

		logger.debug("Finished with "
				+ usersSuccess + " users " + forums + " forums and " + threads + " threads and "
				+ postsSuccess + " posts");
	}
	
	/**
	 * waits until the queue of the listener is empty (size == 0)
	 */
	private void waitForEmptyQueue() {
		while (listener.getNumOfUrls() > 0) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				return;
			}
		}
		return;
	}
	
	/**
	 * waits until all the listener's threads (or other signs of activity) are inactive
	 */
	private void waitForInactive() {
		while (listener.getActiveCount() > 0) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				return;
			}
		}
		return;
	}
}
