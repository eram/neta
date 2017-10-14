/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.interfaces;

/**
 *	The <code>IUrlListener</code> interface receives URLs.
 *  The user of this interface sends its URLs as a {@link String}.
 *  and it may ask for the current number of URLs. 
 * TODO comments
 * @author yoavram
 * @see com.netalign.netascutter.Scutter
 * @see com.netalign.netascutter.DummyListener
 */
public interface IUrlListener {

    /**
     * Add a URL to the listener. The listener WILL check the URL prior to addition.
     * @param url a {@link String} of a URL
     * @return returns true if the URL was added or false otherwise
     */
    boolean addURL(String url);
    /**
     * Add a URL to the listener. The listener will NOT check the URL prior to addition.
     * @param url a {@link String} of a URL
     * @return returns true if the URL was added or false otherwise
     */
    boolean addURLUnchecked(String url);
    /**
     * Remove a URL from the top of the listener's list and return it to the user.
     * @return returns the URL at the top of the listener's list, or an empty string 
     * if the listener has no URLs
     */
    String removeURL();
	/**
	 * The listener will return the number of URLs in it's queue.
	 * @return Number of URLs in the listener's URL queue.
	 */
	int getNumOfUrls();
	/**
	 * The listener will return the number of URLs that already have been in it's queue.
	 * @return Number of URLs ever been in the listener's URL queue.
	 */
	int getNumOfUrlsSeen();
	/**
	 * Set the maximum number of URLs allowed to be added to the URL queue
	 * 
	 * @param maxNumOfUrls
	 */
	void setMaxNumOfUrls(int maxNumOfUrls);

	/**
	 * Set the domain in which all valid URLs must reside
	 * 
	 * @param domain
	 */
	void setPattern(String domain);

}
