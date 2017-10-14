/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.interfaces;

/**
 * The <code>IThreadedUrlListener</code> interface receives URLs from and for a
 * specific thread. The user of this interface sends its URLs as a
 * {@link String}, which are put into a stack specific for this thread. TODO
 * comments
 * 
 * @author yoavram
 * @see com.netalign.netascutter.Scutter
 * @see com.netalign.netascutter.DummyListener
 */
public interface IThreadedUrlListener extends IUrlListener {

	boolean addGlobalURL(String url);
	
	boolean addGlobalURLUnchecked(String url);

	boolean pushThreadURL(String url);

	boolean pushThreadURLUnchecked(String url);

	/**
	 * may return null
	 * 
	 * @return
	 */
	String popURL();
	
	/**
	 * may return null
	 * 
	 * @return
	 */
	String popThreadURL();

	int sizeFutureGlobalURLs();

	int sizePastGlobalURL();

	int sizePresentURLs();

	int sizeFutureThreadURLs();

	int sizePastThreadURLs();

	void finishURL(String url);
	
	boolean inPastGlobalURLs(String url);
	boolean inPastThreadURLs(String url);

	int getActiveCount();

}