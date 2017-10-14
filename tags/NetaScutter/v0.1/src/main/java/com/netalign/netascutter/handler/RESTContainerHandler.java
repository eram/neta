/**
 * 
 */
package com.netalign.netascutter.handler;

import java.util.*;

import org.apache.log4j.*;

import com.netalign.netascutter.Constants;
import com.netalign.netascutter.interfaces.*;
import com.netalign.sioc.*;

/**
 * @author yoavram
 * 
 */
public class RESTContainerHandler extends RESTAbstractHandler<ISiocContainer> {

	private static final Logger logger = Logger
			.getLogger(RESTContainerHandler.class);
	private final ContainerHandler containerHandler;

	public RESTContainerHandler() {
		super();
		containerHandler = new ContainerHandler();
	}

	public void setListener(IThreadedUrlListener listener) {
		super.setListener(listener);
		containerHandler.setListener(listener);
	}

	@Override
	public synchronized void handle(ISiocContainer container, String containerUrl) {
		logger.debug("Handling container with URI " + container.getURI() + " from URL " + containerUrl);
		//TODO do not create empty containers (containers that only have uri and nothing else 				
		int parentId = 0;
		if (container.getHasParent() != null) {						
			// the container has a parent container
			String parentUri = container.getHasParent().getURI();
			if (parentUri != null && !parentUri.isEmpty()) {
				// we have a uri of the parent container
				if ((parentId = getContainer(parentUri)) != Constants.ILLEGAL_ID) {
					// parent in REST
					logger.debug("Found parent container with " + Constants.TID
							+ " " + parentId);
				} else {
					// parent not in REST
					parentId = createContainer(container.getHasParent(),container.getHasParent().getSeeAlso(), 0);					
				}
			} else {
				logger.info("Parent container without URI in " + containerUrl);
			}
			
		}
		// check if container exists
		int tid= getContainer(container.getURI());
		if ( tid == Constants.ILLEGAL_ID) {
			// create the container
			tid = createContainer(container, containerUrl, parentId);
		} else {
			// update the container 
			tid = updateContainer(container, tid, containerUrl, parentId);
		}
		
		// iterate over child containers
		if (tid != Constants.ILLEGAL_ID) {
			for (ISiocContainer child : container.getParentOf()) {
				int childId = Constants.ILLEGAL_ID;
				String childUri = child.getURI();
				if (childUri != null && !childUri.isEmpty()) {
					// we have a uri of the parent container
					if ((childId = getContainer(childUri)) != Constants.ILLEGAL_ID) {
						// child in REST
						logger.debug("Found child container with "
								+ Constants.TID + " " + childId);
						childId = updateContainer(child, childId, child.getSeeAlso(), tid);
					} else {
						// child not in REST
						childId = createContainer(child, child.getSeeAlso(), tid);						
					}
				} else {
					logger.info("Child container without URI in " + containerUrl);
				}
			}

			// iterate over child posts
			for (ISiocPost post : container.getContainerOf()) {
				if (post.getReplyOf() == null) {
					// only first post in thread can be a forum topic
					// check if post is in REST as a node or a comment
					int nid = restCon.getNodeId(Constants.NODE_URI_FIELD,
							encryptor.encrypt(post.getURI()));
					int cid = restCon.getCommentId(Constants.COMMENT_URI_FIELD,
							encryptor.encrypt(post.getURI()));
					if (nid == Constants.ILLEGAL_ID && cid == Constants.ILLEGAL_ID && post.getReplyOf() == null) {
						// post not in REST (as comment or node) and is not a reply of another post
						nid = createPost(post, post.getSeeAlso(), tid);						
					} else if (nid == Constants.ILLEGAL_ID && cid != Constants.ILLEGAL_ID ) {
						// post in REST as comment and not as node
						nid = getParentNidByCid(cid);
					}
					if (nid != Constants.ILLEGAL_ID) {
						// set node - term relation
						relateNodeToContainer(nid, tid);
					}
				}
			}
		} // end of if tid != ILLEGAL_ID

		// send the object to the container handler to import seeAlso links
		containerHandler.handle(container, containerUrl);
		logger.debug("Done handling container with URI " + container.getURI() + " from URL " + containerUrl);
	}

	private int getContainer(String uri) {
		return restCon.getTermId(Constants.TERM_URI_FIELD, encryptor.encrypt(uri));
	}

	private void relateNodeToContainer(int nid, int tid) {
		if (restCon.addTermNodeRelation(nid, tid) ) {
			logger.info("Added term " + tid + " to node " + nid);
		} else {
			logger.warn("Failed adding term " + tid + " to node " + nid);
		}
	}

	private int updateContainer(ISiocContainer container, int tid, String url, int parentTid) {
		Map<String, String> map = converter.convertContainer(container, url);
		if (parentTid > Constants.DRUPAL_MIN_ID) {
			map.put("parent", Integer.toString(parentTid));
		}
		if (map.get("name").startsWith("http://")) {
			map.remove("name"); // so as not to update a good name with a stub name made from the URI of the container
		}
		tid = restCon.updateTerm(map, Constants.TID, Integer.toString(tid));
		
		if (tid != Constants.ILLEGAL_ID) {
			logger.debug("Updated container with "
					+ Constants.TID + " " + tid + " with name " + map.get("name"));
		} else {
			logger.warn("Failed updating container with "
					+ Constants.TID + " " + tid + " with name " + map.get("name"));
		}
		return tid;
	}

	private int createContainer(ISiocContainer container, String url, int parentTid) {
		Map<String, String> map = converter.convertContainer(container, url);
		map.put("parent", Integer.toString(parentTid));
		
		int tid = restCon.createTerm(map);
		if (tid != Constants.ILLEGAL_ID) {
			logger.debug("Created container with "
					+ Constants.TID + " " + tid + " with name " + map.get("name"));
		} else {
			logger.warn("Failed creating container with name " + map.get("name"));
		}
		return tid;
	}

	private int createPost(ISiocPost post, String url, int tid) {
		Map<String, String> map = converter.convertPost(post, url);
		
		int nid = restCon.createNode(map);
		if (nid == Constants.ILLEGAL_ID) { // failed creating post
			logger.warn("Failed creating child node");
		} else {
			logger.info("Created child node with " + Constants.NID + " " + nid);
		}
		return nid;
	}
	
	/**
	 * get the node id of the parent of a comment specified by a comment id.
	 * returns Constatns.ILLEGAL_ID if failed.
	 * 
	 * @param cid
	 * @return
	 */
	protected int getParentNidByCid(int cid) {
		int result = Constants.ILLEGAL_ID;
		String reply = restCon.getCommentFieldByKeyValue(Constants.CID, Integer
				.toString(cid), Constants.NID);
		if (reply != null && !reply.isEmpty()) {
			try {
				result = Integer.parseInt(reply);
			} catch (NumberFormatException e) {
				;
			}
		}
		return result;
	}
}
