/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.handler;

import java.util.*;
import org.apache.log4j.Logger;

import com.netalign.netascutter.Constants;
import com.netalign.netascutter.interfaces.IHandler;
import com.netalign.netascutter.interfaces.IRESTConnection;
import com.netalign.sioc.*;

/**
 * The <code>RESTPostHandler</code> class implements the {@link IHandler}
 * interface.
 * <p>
 * The class handles {@link ISiocPost} objects, communicating them to a
 * {@link Drupal}-like REST server using an {@link IRESTConnection}
 * implementation.
 * 
 * TODO comments on private methods
 * 
 * @author yoavram
 * @see IHandler
 * @see RESTAbstractHandler
 * @see ISiocPost
 * @see IRESTConnection
 * 
 */
public class RESTPostHandler extends RESTAbstractHandler<ISiocPost> {

	protected static Logger logger = Logger.getLogger(RESTPostHandler.class);

	private class State {
		public boolean postInREST;
		public boolean parentInPost;
		public boolean parentInREST;
		public boolean userInPost;
		public boolean userInREST;

		public State() {
			postInREST = parentInPost = parentInREST = userInPost = userInREST = false;
		}

		public State(State state) {
			this.copyFrom(state);
		}

		public int getState() {
			int a = postInREST ? 1 : 0;
			a <<= 1;
			a += parentInPost ? 1 : 0;
			a <<= 1;
			a += parentInREST ? 1 : 0;
			a <<= 1;
			a += userInPost ? 1 : 0;
			a <<= 1;
			a += userInREST ? 1 : 0;
			return a;
		}

		public void copyFrom(State state) {
			this.postInREST = state.postInREST;
			this.parentInPost = state.parentInPost;
			this.parentInREST = state.parentInREST;
			this.userInPost = state.userInPost;
			this.userInREST = state.userInREST;
		}

		@Override
		public String toString() {
			Formatter formatter = new Formatter();
			formatter
					.format(
							"State 0x%x: postInREST=%b parentInPost=%b parentInREST=%b userInPost=%b userInREST=%b",
							getState(), postInREST, parentInPost, parentInREST,
							userInPost, userInREST);
			return formatter.toString();
		}
	}

	@Override
	public synchronized void handle(ISiocPost post, String postUrl) {
		logger.debug("Handling post with URI " + post.getURI() + " from URL " + postUrl);
		// get soem see alsos
		getSeeAlso(post);
		// find state
		State state = new State();
		int nid = Constants.ILLEGAL_ID;
		int cid = Constants.ILLEGAL_ID;
		int uid = Constants.ILLEGAL_ID;
		int parentId = Constants.ILLEGAL_ID;
		// check if post in drupal
		if ((nid = getNodeId(post.getURI())) != Constants.ILLEGAL_ID) {
			// post is aleady node id drupal
			state.postInREST = true;
		} else if ((cid = getCommentId(post.getURI())) != Constants.ILLEGAL_ID) {
			// post is comment and in drupal
			state.postInREST = true;
		}
		// check if user in post and drupal
		if (post.getMaker() != null && post.getMaker().getSeeAlso() != null
				&& !post.getMaker().getSeeAlso().isEmpty()) {
			// post have user
			state.userInPost = true;
		}
		if (post.getMaker() != null
				&& (uid = getMakerId(post)) != Constants.ILLEGAL_ID) {
			state.userInREST = true;
		} else if (state.postInREST) {
			// try to get user id by cid or nid FIXME this does nothing : HTTP://192.168.123.4/=/user/nid/268/uid will always return -1
			if (cid != Constants.ILLEGAL_ID) {
				uid = getUserIdBy(Constants.CID, Integer.toString(cid));
			} else if (nid != Constants.ILLEGAL_ID) {
				uid = getUserIdBy(Constants.NID, Integer.toString(nid));
			}
			if (uid != Constants.ILLEGAL_ID) {
				state.userInREST = true;
			}
		}
		// check if parent in post and drupal
		if (post.getReplyOf() != null && post.getReplyOf().getSeeAlso() != null
				&& !post.getReplyOf().getSeeAlso().isEmpty()) {
			state.parentInPost = true;
		}
		if (cid != Constants.ILLEGAL_ID
				&& (parentId = getParentNidByCid(cid)) != Constants.ILLEGAL_ID) {
			state.parentInREST = true;
		} else if (post.getReplyOf() != null
				&& (parentId = getParentNidByURI(post.getReplyOf().getURI())) != Constants.ILLEGAL_ID) {
			state.parentInREST = true;
		}

		// do job -
		// postInREST-parentInPost-parentInREST-userInPost-userInREST
		logger.debug(state.toString());
		switch (state.getState()) {
		// postInREST-parentInPost-parentInREST-userInPost-userInREST

		case (0): // 00000
		 // create node with anonymous user
			nid = createNode(post, postUrl, 0);
			break;

		case (3): // 00011
			// i don't care if the user is in the post, more important i
			// have a uid i can use
		case (1): // 00001
			// create a node with a user id that is already found
			nid = createNode(post, postUrl, uid);
			break;

		case (0x1e): // 11110
		case (0x16): // 10110
		case (0x12): // 10010
		case (0xe): // 01110
		case (6): // 00110
			// get the user first then we can process the post
		case (2): // 00010
			// add the maker url to the thread url queue
			String makerUrl = post.getMaker().getSeeAlso();
			if (!listener.inPastThreadURLs(makerUrl)) {
				listener.pushThreadURLUnchecked(postUrl);
				listener.pushThreadURL(makerUrl);
				logger.debug("Found maker link, "
						+ "added to thread queue and retrying comment from "
						+ postUrl);
			} else {
				logger.warn("Maker link already seen, dumping comment from "
						+ postUrl);
			}

			break;

		case (0xc): // 01100
			// i don't care if there is parent in post, the important thing
			// is parent in REST
		case (4): // 00100
			// no post in REST so no need to check if update or create,
			// simply enough to create.
			// also i have no thread data 
			// no user id so set to zero.
			cid = createComment(post, postUrl, 0, parentId, null);
			break;

		case (0xf): // 01111
			// see next two cases as this is a union of them
		case (0xd): // 01101
			// i don't care if there is parent in post, the important thing
			// is parent in REST
		case (7): // 00111
			// i don't care if the user is in the post, it's enough that he
			// is in the REST
		case (5): // 00101
			// no post in REST so no need to check if update or create,
			// simply enough to create.
			// also i have no thread data 
			// this time i have a user id
			cid = createComment(post, postUrl, uid, parentId, null);
			break;

		case (0x1b): // 11011
		case (0x1a): // 11010
		case (0x19): // 11001
		case (0x18): // 11000
		case (9): // 01001
		case (0xa): // 01010
		case (0xb): // 01011
			// get the parent first then we can proceed
		case (8): // 01000
			// bring the parent first then process this post.
			String parentUrl = post.getReplyOf().getSeeAlso();
			if (!listener.inPastThreadURLs(parentUrl)) {
				listener.pushThreadURLUnchecked(postUrl);
				listener.pushThreadURL(parentUrl);
				logger.debug("Found reply_of link, "
						+ "added to thread queue and retrying comment from "
						+ postUrl);
			} else {
				logger.warn("Reply_of link already seen, dumping comment from "
						+ postUrl);
			}
			break;

		case (0x10): // 10000
		 // update node without user data
			updateNode(post, postUrl, nid, Constants.ILLEGAL_ID);
			break;

		case (0x13): // 10011
			// i don't care about user in post if i have user in rest
		case (0x11): // 10001
			// update node with user data
			nid = updateNode(post, postUrl, nid, uid);
			break;

// postInREST-parentInPost-parentInREST-userInPost-userInREST
		case (0x1c): // 11100
			// i don't care about parent in post, i care about parent in
			// rest
		case (0x14): // 10100
			// check if post in REST as node, because then we need to delete it
			if (nid != Constants.ILLEGAL_ID) {
				
				// get a term for the node
				int tid = getTermForNode(nid);				
				// delete node and create comment
				deleteNode(post, postUrl, nid);
				// create a node with a user id that is already found
				cid = createComment(post, postUrl, Constants.ILLEGAL_ID, parentId, null);
				// if tid from before was valid, use it for the new node
				if (tid != Constants.ILLEGAL_ID) {
					nid = getParentNidByCid(cid); // get the nid of the comment parent
					setTermForNode(nid, tid); // set the term on the parent
				}				
			} else {
				// update the comment without user id
				cid = updateComment(post, postUrl, cid, Constants.ILLEGAL_ID, Constants.ILLEGAL_ID, null);
			}
			break;

		case (0x1f): // 11111
			// i don't care about user or parent in post, only care about
			// rest!
		case (0x1d): // 11101
			// i don't care about user in post, i have user in rest
		case (0x17): // 10111
			// i don't care about parent in post, i have user in rest
		case (0x15): // 10101
			// check if post in REST as node, because then we need to delete it
			if (nid != Constants.ILLEGAL_ID) {
				
				// get a term for the node
				int tid = getTermForNode(nid);				
				// delete node and create comment
				deleteNode(post, postUrl, nid);
				// create a node with a user id that is already found
				cid = createComment(post, postUrl, Constants.ILLEGAL_ID, parentId, null);
				// if tid from before was valid, use it for the new node
				if (tid != Constants.ILLEGAL_ID) {
					nid = getParentNidByCid(cid); // get the nid of the comment parent
					setTermForNode(nid, tid); // set the term on the parent
				}	
			} else {
				// update the comment with user id
				cid = updateComment(post, postUrl, cid, uid, Constants.ILLEGAL_ID, null);
			}			
			break;

		default:
			logger.error("Invalid state found: " + state.getState());
			break;
		}

		// check for REPLIES -> COMMENTS
		String thread = Constants.EMPTY_STRING;
		if (cid != Constants.ILLEGAL_ID) {
			thread = getCommentThread(cid);
			logger.debug("Found comment thread " + thread);
		}
		List<ISiocPost> replies = post.getHasReply();
		if (nid >= Constants.DRUPAL_MIN_ID && replies != null
				&& replies.size() > 0) {
			logger.debug("Creating replies for NID " + nid);
			/*
			 * these are the comments or sub-comments (comments of comments) of
			 * a node. this is why we must have a legal NID so that we can
			 * relate the comments to it. since we don't have the reply but we
			 * have the link to the reply, but when we will have the reply we
			 * might not know what it replies to, we will create a stub reply
			 * now with the url of the full reply as one of the comment field,
			 * so that later on we can get it and update the reply.
			 */
			int counter = 1;
			for (ISiocPost comment : replies) {
				// for EACH COMMENT				
				createStubComment(comment, nid, buildCommentThread(thread, counter++));
				listener.addURL(comment.getSeeAlso());
			}
		} // finish comments
		logger.debug("Done handling post with URI " + post.getURI() + " from URL " + postUrl);
	}

	private void getSeeAlso(ISiocPost post) {
		ISiocPost seeAlsoPost = null;
		if ( (seeAlsoPost = post.getPreviousByDate()) != null) {
			listener.addURL(seeAlsoPost.getSeeAlso());
		}
		if ( (seeAlsoPost = post.getPreviousVersion()) != null) {
			listener.addURL(seeAlsoPost.getSeeAlso());
		}
		if ( (seeAlsoPost = post.getNextByDate()) != null) {
			listener.addURL(seeAlsoPost.getSeeAlso());
		}
		if ( (seeAlsoPost = post.getNextVersion()) != null) {
			listener.addURL(seeAlsoPost.getSeeAlso());
		}
		if ( post.getContainer() != null) {
			listener.addURL(post.getContainer().getSeeAlso());
		}	
	}

	/**
	 * Builds a "thread" string for a comment, based on the thread string of the
	 * parent of the comment and on the index number of the comment (how many
	 * older comments that parent has).<br>
	 * 
	 * @param thread
	 * @param comNum
	 * @return
	 */
	protected String buildCommentThread(String thread, int comNum) {
		if (thread.endsWith("/")) {
			thread = thread.replace('/', '.');
		} else if (!thread.isEmpty()) {
			// this is not good, if the string is not empty then it should end
			// with '/'
			logger.error("Thread strings should end with '/': " + thread);
			return Constants.EMPTY_STRING;
		}
		if (comNum < 10) {
			thread += '0';
			thread += comNum;
		} else {
			thread += comNum;
		}

		thread += '/';
		// debug print here TODO take it off!
		if (thread.contains(".")) {
			logger.debug("Found a comment of a comment!");
		}
		return thread;
	}

	/**
	 * create a stub comment on the server - create an empty comment waiting to
	 * be filled once the comment RDF will be processed in the system. the stub
	 * therefore is a placeholder.
	 * 
	 * @param ISiocPost
	 *            that the new comment relates to
	 * @param parentId
	 *            the nid of the node this comment should relate to
	 * @param thread
	 *            a thread string for this comment
	 * @return
	 */
	protected int createStubComment(ISiocPost post, int parentId, String thread) {
		String postUrl = post.getSeeAlso();

		Map<String, String> map = converter.convertComment(post, postUrl);
		map.put(Constants.NID, Integer.toString(parentId));
		/*if (thread != null && !thread.isEmpty()) // this is done now by the drupal module restapi_comment
			map.put(Constants.THREAD, thread);*/

		int cid = restCon.createComment(map);
		if (cid == Constants.ILLEGAL_ID) { // failed creating comment
			logger.warn("Failed creating stub comment from URL " + postUrl);
		} else {
			logger.info("Created stub comment with CID " + cid
					+ " to post with NID " + parentId + " from URL " + postUrl);
		}
		return cid;
	}

	/**
	 * get a node id for a post from the REST server
	 * 
	 * @param uri
	 *            a URI of an sioc:post resource
	 * @return nid for the post, RESTConnection.ILLEGAL_ID if failed
	 */
	protected int getNodeId(String uri) {
		int v = Constants.ILLEGAL_ID;
		if (uri == null || uri.isEmpty()) {
			logger.debug("Can't get " + Constants.NID + " by empty URI");
			return v;
		}
		String sha1 = encryptor.encrypt(uri);
		v = restCon.getNodeId(Constants.NODE_URI_FIELD, sha1);
		if (v == Constants.ILLEGAL_ID) {
			logger.debug("Failed to get NID by " + uri + " -> " + sha1);
		} else {
			logger.debug("Succeeded to get NID by " + uri + " -> " + sha1);
		}
		return v;
	}

	/**
	 * get the comment id (cid) for a specific URI. returns Constatns.ILLEGAL_ID
	 * if failed.
	 * 
	 * @param uri
	 * @return
	 */
	protected int getCommentId(String uri) {
		int v = Constants.ILLEGAL_ID;
		if (uri == null || uri.isEmpty()) {
			logger.debug("Can't get " + Constants.CID + " by empty URI");
			return v;
		}
		String sha1 = encryptor.encrypt(uri);
		v = restCon.getCommentId(Constants.COMMENT_URI_FIELD, sha1);
		if (v == Constants.ILLEGAL_ID) {
			logger.debug("Failed to get " + Constants.CID + " by " + uri
					+ " -> " + sha1);
		} else {
			logger.debug("Succeede to get " + Constants.CID + " by " + uri
					+ " -> " + sha1);
		}
		return v;
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

	/**
	 * get the parent node id of a post specified by the URI of the parent. if
	 * parent is a comment then get the node id (nid) associated with it.
	 * returns Constatns.ILLEGAL_ID if failed.
	 * 
	 * @param parentUri
	 * @return
	 */
	protected int getParentNidByURI(String parentUri) {
		int pid = getNodeId(parentUri);
		if (pid == Constants.ILLEGAL_ID) {
			// check if the parent is a comment
			pid = getCommentId(parentUri);
			if (pid != Constants.ILLEGAL_ID) {
				// parent is a comment, get the nid associated with the parent
				pid = getParentNidByCid(pid);
			}
		}
		return pid;
	}

	/**
	 * get the thread string for a comment specified by a comment id.
	 * 
	 * @param cid
	 * @return
	 */
	protected String getCommentThread(int cid) {
		return restCon.getCommentFieldByKeyValue(Constants.CID, Integer
				.toString(cid), Constants.THREAD);
	}

	/**
	 * get the user id of the foaf:maker of a post. returns Constatns.ILLEGAL_ID
	 * if failed.
	 * 
	 * @param post
	 * @return
	 */
	protected int getMakerId(ISiocPost post) {
		// IF CHANGING USER ID METHOD CHANGE IT ALSO AT
		// RESTPersonHandler.getUserUid()
		IFoafPerson maker = post.getMaker();
		int uid = Constants.ILLEGAL_ID;
		if (maker != null) {
			uid = restCon.getUserId(Constants.USER_URI_FIELD, encryptor
					.encrypt(maker.getURI()));
			if (uid == Constants.ILLEGAL_ID) {
				// try by mboxsha1
				uid = restCon.getUserId(Constants.USER_MBOX_FIELD, maker
						.getMboxSha1Sum());
			}
		}
		return uid;
	}

	/**
	 * get a user id by a key-value combination, the key being a field name of a
	 * user and the value being the value of the field. returns the first user
	 * that has the given key-value. returns Constatns.ILLEGAL_ID if failed.
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	protected int getUserIdBy(String field, String value) {
		int uid = Constants.ILLEGAL_ID;
		if (value != null && !value.isEmpty()) {
			uid = restCon.getUserId(field, value);
		}
		return uid;
	}

	protected int createNode(ISiocPost post, String postUrl, int uid) {
		Map<String, String> map = converter.convertPost(post, postUrl);
		if (uid != Constants.ILLEGAL_ID)
			map.put(Constants.UID, Integer.toString(uid));

		int nid = restCon.createNode(map);
		if (nid == Constants.ILLEGAL_ID) { // failed creating post
			logger.warn("Failed creating node from URL " + postUrl);
		} else {
			logger
					.info("Created node with NID " + nid + " from URL "
							+ postUrl);
		}
		return nid;
	}
	
	protected int updateNode(ISiocPost post, String postUrl, int nid, int uid) {
		Map<String, String> map = converter.convertPost(post, postUrl);
		if (uid > Constants.DRUPAL_MIN_ID) // update uid only if it is larger then the anonymous id
			map.put(Constants.UID, Integer.toString(uid));
		nid = restCon.updateNode(map, Constants.NID, Integer.toString(nid));
		if (nid == Constants.ILLEGAL_ID) {
			logger.warn("Failed updating node from URL " + postUrl);
		} else {
			logger.info("Updated node with NID " + nid + " from URL "
					+ postUrl);
		}
		return nid;
	}
	
	private void deleteNode(ISiocPost post, String postUrl, int nid) {
		if (restCon.deleteNode(Constants.NID, Integer.toString(nid))) {
			logger.debug("Deleted node with "+Constants.NID+ " " + nid + " because it had a parent " + post.getReplyOf().getURI());
		} else {
			logger.warn("Failed deleting node with "+Constants.NID+ " " + nid );
		}		
	}

	protected int createComment(ISiocPost post, String postUrl, int uid,
			int parentId, String thread) {
		Map<String, String> map = converter.convertComment(post, postUrl);
		map.put(Constants.NID, Integer.toString(parentId));
		/*if (thread != null && !thread.isEmpty()) // this is done now by the drupal module restapi_comment
			map.put(Constants.THREAD, thread);*/
		if (uid > Constants.ILLEGAL_ID) // update uid only if it is larger then the illegal id
			map.put(Constants.UID, Integer.toString(uid));

		int cid = restCon.createComment(map);
		if (cid == Constants.ILLEGAL_ID) { // failed creating comment
			logger.warn("Failed creating comment from URL " + postUrl);
		} else {
			logger.info("Created comment with CID " + cid
					+ " to post with NID " + parentId + " from URL " + postUrl);
		}
		return cid;
	}

	protected int updateComment(ISiocPost post, String postUrl, int cid, int uid,
			int parentId, String thread) {
		Map<String, String> map = converter.convertComment(post, postUrl);
		map.put(Constants.CID, Integer.toString(cid));
		
		/*if (thread != null && !thread.isEmpty()) // this is done now by the drupal module restapi_comment
			map.put(Constants.THREAD, thread);*/
		
		if (uid != Constants.ILLEGAL_ID)
			map.put(Constants.UID, Integer.toString(uid));	
		
		if (parentId != Constants.ILLEGAL_ID)
			map.put(Constants.NID, Integer.toString(parentId));

		int newcid = restCon.updateComment(map, Constants.CID, Integer
				.toString(cid));
		if (newcid == Constants.ILLEGAL_ID) { // failed updating post
			logger.warn("Failed updating comment with CID "
					+ Integer.toString(cid) + " from URL " + postUrl);
		} else if (cid == newcid) {
			logger.info("Updated comment with CID "
					+ Integer.toString(newcid) + " from URL " + postUrl);
		} else {
			logger.error("New CID is different than old CID,"
					+ " failed updating comment with CID "
					+ Integer.toString(cid) + " from URL " + postUrl);
		}
		return newcid;
	}
	
	private int getTermForNode(int nid) {
		int tid = Constants.ILLEGAL_ID;
		String result = restCon.getNodeFieldByKeyValue(Constants.NID, Integer.toString(nid), Constants.TID);
		if (result != null && !result.isEmpty()) {
			tid = Integer.parseInt(result);			
		}		
		return tid;
	}
	
	private boolean setTermForNode(int nid, int tid) {
		return restCon.addTermNodeRelation(nid, tid);
	}
}
