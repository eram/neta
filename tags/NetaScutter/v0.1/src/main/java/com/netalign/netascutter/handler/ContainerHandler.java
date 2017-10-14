/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.handler;

import org.apache.log4j.Logger;

import com.netalign.netascutter.interfaces.IHandler;
import com.netalign.netascutter.interfaces.IUrlListener;
import com.netalign.sioc.*;

/**
 * The <code>ContainerHandler</code> class implements the {@link IHandler}
 * interface.
 * <p>
 * The class extracts see also URLs of posts, users and containers in an {@link ISiocContainer} 
 * object and adds them to the {@link IUrlListener}.
 * <p>
 * This handler handles both containers, forums, threads and all other containers.
 * <p>
 * 
 * @author yoavram
 * @see IHandler
 * @see ISiocContainer
 * @see ISiocForum
 */
public class ContainerHandler extends AbstractHandler<ISiocContainer> {
	protected static Logger logger = Logger.getLogger(ContainerHandler.class);

	@Override
	public void handle(ISiocContainer container, String containerUrl) {
		int counter = 0;
				
		// get all the links possible, first of users and containers, then of posts
		// specific type of containers use specific blocks with 
		// "if (container instanceof X)" statement
		
		if (container.getSeeAlso() != null && !container.getSeeAlso().isEmpty()) {
			listener.addURL(container.getSeeAlso());
			counter++;
		}
		
		if (container.getHasOwner() != null) {
			listener.addURL(container.getHasOwner().getSeeAlso());
			counter++;
		}
		if (container.getHasParent() != null) {
			listener.addURL(container.getHasParent().getSeeAlso());
			counter++;
		}
		for (ISiocUser user : container.getHasSubscriber()) {
			if (user != null) {
				listener.addURL(user.getSeeAlso());
				counter++;
			}
		}
		for (ISiocContainer child : container.getParentOf()) {
			if (child != null) {
				listener.addURL(child.getSeeAlso());
				counter++;
			}
		}
				
		// Forum specific getters
		if (container instanceof ISiocForum) {
			ISiocForum forum = (ISiocForum)container;
			if (forum.getHasHost() != null) {
				listener.addURL(forum.getHasHost().getSeeAlso());
				counter++;
			}
			for (ISiocUser user : forum.getHasModerator()) {
				if (user != null) {
					listener.addURL(user.getSeeAlso());
					counter++;
				}
			}
		}
		
		// General container again
		for (ISiocPost post : container.getContainerOf()) {
			if (post != null) {
				listener.addURL(post.getSeeAlso());
				counter++;
			} 
			if( post.getNextByDate() != null && post.getNextByDate().getSeeAlso() != null && 
					!post.getNextByDate().getSeeAlso().isEmpty()) {
				listener.addURL(post.getNextByDate().getSeeAlso());
				counter++;
			}
			if( post.getPreviousByDate() != null && post.getPreviousByDate().getSeeAlso() != null && 
					!post.getPreviousByDate().getSeeAlso().isEmpty()) {
				listener.addURL(post.getPreviousByDate().getSeeAlso());
				counter++;
			}
			if( post.getNextVersion() != null && post.getNextVersion().getSeeAlso() != null && 
					!post.getNextVersion().getSeeAlso().isEmpty()) {
				listener.addURL(post.getNextVersion().getSeeAlso());
				counter++;
			}
			if( post.getPreviousVersion() != null && post.getPreviousVersion().getSeeAlso() != null && 
					!post.getPreviousVersion().getSeeAlso().isEmpty()) {
				listener.addURL(post.getPreviousVersion().getSeeAlso());
				counter++;
			}
			for (ISiocPost sib : post.getSiblings()) {
				if (sib!=null && sib.getSeeAlso() != null && !sib.getSeeAlso().isEmpty()) {
					listener.addURL(sib.getSeeAlso());
					counter++;
				}
			}			
		}	

		logger.debug("Extracted " + Integer.toString(counter)
				+ " 'seeAlso' links from forum at " + containerUrl);
	}
}
