/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.handler;

import com.netalign.netascutter.interfaces.*;
import com.netalign.netascutter.utils.SHA1Encryptor;

/**
 * The <code>RESTAbstractHandler</code> abstract class implements a constructor and setters common to 
 * all REST handlers.
 * 
 * @author yoavram
 * @see IHandler
 * @see AbstractHandler
 * @see RESTPersonHandler
 * @see RESTPostHandler
 */
public abstract class RESTAbstractHandler<E> extends AbstractHandler<E> {
		
	protected IRESTConnection restCon; 
	protected IConverter converter;
	protected IEncryptor encryptor;
    
    /**
     * Empty constructor, must set got or connection, listener and converter for proper work
     */
    public RESTAbstractHandler() {
    	encryptor = new SHA1Encryptor();
    }
    
    public void setRestCon(IRESTConnection restCon) {
        this.restCon = restCon;
    }
   
    public void setConverter(IConverter converter) {
        this.converter = converter;
    }
}
