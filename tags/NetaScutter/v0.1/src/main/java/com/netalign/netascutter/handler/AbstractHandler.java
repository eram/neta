/**
 * 
 */
package com.netalign.netascutter.handler;

import com.netalign.netascutter.interfaces.IHandler;
import com.netalign.netascutter.interfaces.IThreadedUrlListener;

/**
 * The <code>AbstractHandler</code> abstract class implements just the memebr field listener and the 
 * setter for that memeber, since these are the common procedures for all handlers.
 * @author yoavram
 * @see IHandler
 */
public abstract class AbstractHandler<E> implements IHandler<E> {
	protected IThreadedUrlListener listener;

	public AbstractHandler() {
	}
	
	@Override
	public void setListener(IThreadedUrlListener listener) {
		this.listener = listener;
	}
}
