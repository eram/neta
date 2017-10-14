/**
 * NetaScutter v.0.1
 */
package com.netalign.netascutter.handler;

import java.net.URL;

import org.apache.log4j.Logger;

import com.netalign.netascutter.interfaces.IHandler;
import com.netalign.netascutter.interfaces.IUrlListener;

/**
 * The <code>UrlHandler</code> class implements the {@link IHandler} interface
 * by receiving URL objects and adding the URL to a {@link IUrlListener}.
 * <p>
 * 
 * @author yoavram
 * @see IHandler
 * @see URL
 * 
 */
public class UrlHandler extends AbstractHandler<URL> implements IHandler<URL> {

	protected static Logger logger = Logger.getLogger(UrlHandler.class);

	@Override
	public void handle(URL element, String url) {
		if (listener == null || element == null) {
			return;
		}
		listener.addURL(element.toString());
	}
}
