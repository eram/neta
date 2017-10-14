/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.interfaces;

import com.netalign.netascutter.handler.ContainerHandler;
import com.netalign.netascutter.handler.RESTAbstractHandler;
import com.netalign.netascutter.handler.RESTPersonHandler;
import com.netalign.netascutter.handler.RESTPostHandler;
import com.netalign.netascutter.handler.UrlHandler;

/**
 * The <code>IHandler</code> interface allows the declaration of handlers for objects of specific type,
 * and allows registration of a {@link IUrlListener} that will listen for new URLs that are encountered 
 * while handling objects.
 * <p>
 * <code>IHandler</code> implementations are state-less, thus they may be instantiated once and used 
 * by many threads.
 * <p>
 * 
 * @author yoavram
 * @see IThreadedUrlListener
 * @see ContainerHandler
 * @see RESTAbstractHandler
 * @see RESTPersonHandler
 * @see RESTPostHandler
 * @see UrlHandler
 * @see IScutter
 */
public interface IHandler<E extends Object> {
	/**
	 * Sets the {@link IUrlListener} that will listen for new URLs from this
	 * handler.
	 * 
	 * @param listener
	 */
	void setListener(IThreadedUrlListener listener);

	/**
	 * Handle an element from the URL specified
	 * 
	 * @param element
	 *            an element to handle
	 * @param url
	 *            the URL from which the element was fetched and parsed
	 */
	void handle(E element, String url);
}
