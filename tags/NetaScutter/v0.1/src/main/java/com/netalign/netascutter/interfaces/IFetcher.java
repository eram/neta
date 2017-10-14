/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.interfaces;

import java.io.BufferedInputStream;
import com.netalign.netascutter.fetcher.HttpFetcher;

/**
 * The <code>IFetcher</code> interface allows the declaration of fetchers
 * specific types of input, while maintaining that all fetchers will get their
 * fetch order via a URL string and return their fetch results using a
 * {@link BufferedInputStream} - an input stream that allows marking and
 * reseting.
 * <p>
 * <code>IFetcher</code> implementations are state-less, thus they may be
 * instantiated once and used by many threads.
 * <p>
 * Any non-null returned streams and fetchers should be closed AFTER the streams
 * were read and no more work \ on the stream is expected.
 * 
 * @author yoavram
 * @see HttpFetcher
 */
public interface IFetcher {
	/**
	 * Attempts fetching a URL.
	 * 
	 * @return an {@link BufferedInputStream} of the URL resource or null if
	 *         failed fetching. BufferedInputStream is an input stream that
	 *         implements mark/reset methods.
	 */
	BufferedInputStream fetch(String url);

	/**
	 * Close all resources used in fetching. Should be called after finished
	 * using the input stream.
	 */
	void close(BufferedInputStream inputStream);
}
