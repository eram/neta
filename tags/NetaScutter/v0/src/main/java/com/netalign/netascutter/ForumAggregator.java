package com.netalign.netascutter;

import org.apache.log4j.Logger;

import com.netalign.netascutter.interfaces.*;
import com.netalign.sioc.*;

/**
* Parses ISiocForum objects - extracts "seeAlso" addresses and sends them to an IUrlListener
* @author yoavram
*/
public class ForumAggregator implements IAggregator {
	protected static Logger logger = Logger.getLogger(ForumAggregator.class);
	protected IUrlListener listener;
		    
	@Override
    public void setListener(IUrlListener listener) {
        this.listener = listener;
    }
	@Override
	public void add(Object incoming, String urlStr) {
		synchronized (this) {
            try {
                logger.debug(Thread.currentThread().getName() + " has the aggregator with " + urlStr);
                if (incoming instanceof ISiocForum) {
                    aggregateForum((ISiocForum) incoming, urlStr);
                }
            } catch (Exception e) {
                logger.error(e);
            } finally {
                logger.debug(Thread.currentThread().getName() + " releases the aggregator");
            }
        }
	}

	/**
	 * the logic here is to get all the 'seeAlso's we can and send them to the url listener.
	 * @param forum
	 * @param forumUrl
	 */
	protected void aggregateForum(ISiocForum forum, String forumUrl) {		
		int counter = 0;
		for (ISiocPost post : forum.getContainerOf()) {
			if (post != null) {
				listener.addURL(post.getSeeAlso());
				counter++;
			}
		}
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
		if (forum.getHasOwner() != null) {			
			listener.addURL(forum.getHasOwner().getSeeAlso());
			counter++;
		}		
		if (forum.getHasParent() != null) {			
			listener.addURL(forum.getHasParent().getSeeAlso());
			counter++;
		}
		for (ISiocUser user : forum.getHasSubscriber()) {
			if (user != null) {
				listener.addURL(user.getSeeAlso());
				counter++;
			}
		}
		for (ISiocContainer container : forum.getParentOf()) {
			if (container != null) {
				listener.addURL(container.getSeeAlso());
				counter++;
			}
		}
		
		logger.debug("Extracted " + Integer.toString(counter) + " 'seeAlso' links from forum at " + forumUrl);		
	}
	

}
