package com.netalign.netascutter;

import com.netalign.netascutter.interfaces.*;
import com.netalign.netascutter.utils.*;
import com.netalign.sioc.*;
import com.netalign.rdf.vocabulary.*;

import java.util.*;
import org.apache.log4j.*;
import org.omg.PortableInterceptor.USER_EXCEPTION;

/**
 *
 * @author yoavram
 */
public class RESTPostAggregator implements IAggregator {

    private static final int NO_PARENT = 0;
	protected static Logger logger = Logger.getLogger(RESTPostAggregator.class);
    protected IRESTConnection restCon; // init on setHost
    protected IUrlListener listener;
    protected String host;
    protected IConverter converter;
	private IEncryptor encryptor;

    /**
     * Empty constructor, must set got or connection, listener and converter for proper work
     */
    public RESTPostAggregator() {
    	encryptor = new SHA1Encryptor();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public IRESTConnection getRestCon() {
        return restCon;
    }
    
    public void setRestCon(IRESTConnection restCon) {
        this.restCon = restCon;
    }
  
    @Override
    public void setListener(IUrlListener listener) {
        this.listener = listener;
    }
    
    public IConverter getConverter() {
        return converter;
    }
    
    public void setConverter(IConverter converter) {
        this.converter = converter;
    }


    /**
     * add a post object to the REST server
     * @param incoming      an ISiocPost instance
     * @param urlStr        the url from which the ISiocPost was extracted from
     */
    @Override
    public void add(Object incoming, String urlStr) {
        synchronized (this) {
            try {
                logger.debug(Thread.currentThread().getName() + " has the aggregator with " + urlStr);
                if (incoming instanceof ISiocPost) {
                    aggregatePost((ISiocPost) incoming, urlStr);
                }
            } catch (Exception e) {
                logger.error(e);
            } finally {
                logger.debug(Thread.currentThread().getName() + " releases the aggregator");
            }
        }
    }

    protected void aggregatePost(ISiocPost post, String postUrl) {
    	int uid = Constants.ILLEGAL_ID;
    	int nid = Constants.ILLEGAL_ID;
    	int cid = Constants.ILLEGAL_ID;
    	String thread = Constants.EMPTY_STRING;
		String url = Constants.EMPTY_STRING;
    	
    	// check if the post has a MAKER (foaf)
    	if (post.getMaker() == null) {
    		// NO maker    		
    		uid = 0;
    		logger.debug("No Maker - UID set to 0 for post from URL "+postUrl);
    	} else {  
    		// YES - get the UID of the maker
    		if ( (uid = getMakerId(post)) == Constants.ILLEGAL_ID) {
    			// NO UID, set uid to 0
    			uid = 0;
    			logger.debug("No maker - UID set to 0 for post from URL "+postUrl);    			
    		} else {
    			logger.debug("Maker exists with UID " + Integer.toString(uid));
    		}
    	}
    	// uid is set
    	// check if the post is a NODE OR COMMENT (reply_of)
    	if (post.getReplyOf() != null) {
    		// COMMENT
    		logger.debug("Identified a Comment from " + postUrl);
    		// get CID
    		if ( (cid = getCommentId(post.getURI())) == Constants.ILLEGAL_ID ) {
    			// NO CID found
    			logger.debug("No CID for comment from " + postUrl);
    			// try to get PARENT URL and RETURN
    			if ( !(url = getParentUrl(post)).isEmpty() ) {
    				// SEEALSO not empty - add it to queue
    				logger.debug("Found reply_of link, adding to queue and dumping comment from "+postUrl);
    				listener.addURL(url);
    				listener.addURLUnchecked(postUrl);    				
    			} else {
    				logger.info("Dumping a comment without a CID or a reply_of link from " + postUrl);
    			}
    			return; 
    		}
    		logger.debug("Got CID " + Integer.toString(cid) + " for " + postUrl);
    		// cid is set, next is NID
    		if ( (nid = getParentNid(cid)) == Constants.ILLEGAL_ID ) {
    			// nid is -1 - this should not happen as we are looking for the nid using the cid
    			logger.error("Failed getting NID for comment with CID " + Integer.toString(cid));
    			return;
    		}
    		logger.debug("Got parent NID " + Integer.toString(nid) + " for CID " + Integer.toString(cid));
    		// nid is set, next is THREAD
    		if ( (thread =  getCommentThread(cid)).isEmpty() ) {
    			// thread is empty - this should not happen as we are looking for the thread using the cid
    			logger.error("Failed getting Thread for comment with CID " + Integer.toString(cid));
    			return;
    		}
    		logger.debug("Got thread " + thread + " for CID " + Integer.toString(cid));
    		//thread is set
    		// now UPDATE the COMMENT with the data we have
    		Map<String,String> map = converter.convertComment(post, postUrl);
    		map.put(Constants.CID, Integer.toString(cid));
    		map.put(Constants.NID, Integer.toString(nid));
    		map.put(Constants.UID, Integer.toString(uid));
    		map.put(Constants.THREAD, thread);    			        

    		int newcid = restCon.updateComment(map, Constants.CID, Integer.toString(cid));
	        if (newcid == Constants.ILLEGAL_ID) { // failed updating post        	
	            logger.warn("Failed updating comment with CID " + Integer.toString(cid) + " from URL " + postUrl);	            
	        } else if (cid == newcid) {
	            logger.info("Updated comment with CID " + Integer.toString(newcid)+ " from URL " + postUrl);	     
	        } else {
	        	logger.error("New CID is different than old CID, failed updating comment with CID " + Integer.toString(cid)+ " from URL " + postUrl);	     
	        	return;
	        }
    		// ending comment
    	} else {
    		// NODE
    		logger.debug("Identified a Post from " + postUrl);
    		// check if node EXIST? - 
    		if ((nid = getNodeId(post.getURI())) == Constants.ILLEGAL_ID) {
    			// NOT EXIST  -  create it
    			Map<String, String> map = converter.convertPost(post, postUrl);  
    			map.put(Constants.UID, Integer.toString(uid));
	    		nid = restCon.createNode(map);
	            if (nid == Constants.ILLEGAL_ID) { // failed creating post
	                logger.warn("Failed creating node from URL " + postUrl);                
	            } else {
	                logger.info("Created node with NID " + nid + " from URL " + postUrl);//TODO change post to node
	            }  
    		} else {
    			// NODE EXIST
    			logger.debug("Node already exist with NID " + nid + " from URL " + postUrl);
    			return;
    		}
    	} // ending node 
    	// check for REPLIES -> COMMENTS
    	List<ISiocPost> replies = post.getHasReply();
    	if (nid >= Constants.DRUPAL_MIN_ID && replies != null && replies.size() > 0) {
    		/* these are the comments or sub-comments (comments of comments) of a node.
    		this is why we must have a legal NID so that we can relate the comments to it.
    		since we don't have the reply but we have the link to the reply, but when we 
    		will have the reply we might not know what it replies to, we will create a stub reply now with 
    		the url of the full reply as one of the comment field, so that later on we can get it 
    		and update the reply.*/
    		for (ISiocPost comment : replies) {
    			// for EACH COMMENT
    			createStubComment(comment, nid, thread); // if thread is empty then the comment will start a thread
   				listener.addURL(comment.getSeeAlso());
    		}     		
    	} // finish comments
    } //end of aggregatePost
    
	/**
     * create or update a post in the REST server
     * @param post      an ISiocPost instance
     * @param urlStr    the url from which the ISiocPost was extracted from
     * @return
     */
    protected int createOrUpdatePost(ISiocPost post, String urlStr, boolean create) {
        Map<String, String> map = converter.convertPost(post, urlStr);    
        
        int uid = getCreatorOrMakerId(post);
    	if (uid != Constants.ILLEGAL_ID) {
    		map.put(Constants.UID, Integer.toString(uid)); // update the uid of the post
    	}

        int nid = Constants.ILLEGAL_ID;
        
        if (create) {
            nid = restCon.createNode(map);
            if (nid == Constants.ILLEGAL_ID) { // failed creating post
                logger.warn("Failed creating post rom URL " + urlStr);
            } else {
                logger.info("Created post with NID " + nid + " from URL " + urlStr);
            }
        } else {
            // update             
            nid = getNodeId(urlStr);
        	int newnid = restCon.updateNode(map, Constants.NID, Integer.toString(nid));
            if (newnid == Constants.ILLEGAL_ID) { // failed updating post
                logger.info("Failed updating post with NID " + Integer.toString(nid));
            } else if (nid == newnid) {
                logger.info("Updated post with NID " + Integer.toString(nid));
            } else {
            	logger.error("New NID is differnet from old NID, update failed for post with NID " + Integer.toString(nid));
            	nid = newnid;
            }
        }
        return nid;
    }
        
	/**
     * create a post on the REST server
     * @param post    an ISiocPost instance
     * @param urlStr    the url from which the ISiocPost was extracted from
     * @return          the nid of the created post
     */
    protected int createPost(ISiocPost post, String urlStr) {
        return createOrUpdatePost(post, urlStr, true);
    }    

    /**
     * update a post on the REST server
     * @param post    an ISiocPost instance
     * @param urlStr    the url from which the ISiocPost was extracted from
     * @return          the nid of the updated post
     */
    protected int updatePost(ISiocPost post, String urlStr) {
        return createOrUpdatePost(post, urlStr, false);
    } 
 
    protected int updateComment(ISiocPost post, String urlStr) { 
    	int cid = getCommentId(post.getSeeAlso());
    	if (cid == Constants.ILLEGAL_ID) { // comment not found - can not be updated
         	logger.warn("Couldn't find stub comment with URL " + urlStr + ", comment will not be updated");
	    } else {        	
	        Map<String,String> map = converter.convertComment(post, urlStr);
	        map.put(Constants.CID, Integer.toString(cid));    
	        
	        int uid = getCreatorOrMakerId(post);
	    	if (uid != Constants.ILLEGAL_ID) {
	    		map.put(Constants.UID, Integer.toString(uid)); // update the uid of the post
	    	}
	        
	    	int newcid = restCon.updateComment(map, Constants.CID, Integer.toString(cid));
	        if (newcid == Constants.ILLEGAL_ID) { // failed updating post        	
	            logger.info("Failed updating comment with CID " + Integer.toString(cid));
	            cid = newcid;
	        } else if (cid == newcid) {
	            logger.info("Updated comment with CID " + Integer.toString(newcid));
	        } else {
	        	logger.error("New CID is different than old CID ,faile dupdating comment with CID " + Integer.toString(cid));
	        	cid = Constants.ILLEGAL_ID;
	        }
	    }        
        return cid;
    }    
    
    protected int createStubComment(ISiocPost post, int parentId) {
    	return createStubComment(post, parentId, Constants.EMPTY_STRING);
    }
    
    protected int createStubComment(ISiocPost post, int parentId, String thread) {
    	String postUrl = post.getSeeAlso();
    	
    	Map<String, String> map = converter.convertComment(post, postUrl);            
        map.put(Constants.NID, Integer.toString(parentId)); 
        map.put(Constants.THREAD, thread);
        //map.put("status", "1"); //TODO change to const, make it work - drupal ignores this
                
        int cid = restCon.createComment(map);
        if (cid == Constants.ILLEGAL_ID) { // failed creating comment
            logger.warn("Failed creating comment from URL " + postUrl);
        } else {
            logger.info("Created stub comment with CID " + cid + " to post with NID " + parentId +" from URL " + postUrl);
        }
        return cid;
    }        
    /**
     * get a node id for a post from the REST server
     * @param url    	an url of a post rdf
     * @return          nid for the post, RESTConnection.ILLEGAL_ID if failed
     */
    protected int getNodeId(String url) {
    	String sha1 = encryptor.encrypt(url);
    	logger.debug("Trying to get node ID by " + url + " -> " + sha1);
        return restCon.getNodeId(Constants.NODE_ID_FIELD, encryptor.encrypt(url));
    }       
    protected int getCommentId(String url) {
    	String sha1 = encryptor.encrypt(url);
    	logger.debug("Trying to get comment ID by " + url + " -> " + sha1);
    	return restCon.getCommentId(Constants.COMMENT_ID_FIELD, sha1);		
	}    
    protected int getParentNid(ISiocPost post) {
    	ISiocPost parent = post.getReplyOf();    
    	if (parent == null) {
    		return NO_PARENT; // there is not parent!
    	}
    	return getNodeId(parent.getURI());    	    
    }
    protected String getParentUrl(ISiocPost comment) {
    	String url = Constants.EMPTY_STRING;
    	if (comment != null && comment.getReplyOf() != null && 
    			comment.getReplyOf().getSeeAlso() != null && !comment.getReplyOf().getSeeAlso().isEmpty()) {
    		url = comment.getReplyOf().getSeeAlso();
    	}
    	return url;   	
    }
    protected int getParentNid(int cid) {
    	return Integer.parseInt(restCon.getCommentFieldByKeyValue(Constants.CID, Integer.toString(cid), Constants.NID)); 	    
    } 
    protected String getCommentThread(int cid)  {
    	return restCon.getCommentFieldByKeyValue(Constants.CID, Integer.toString(cid), Constants.THREAD);    	
    }
    protected int getCreatorOrMakerId(ISiocPost post) {
    	if (post.getMaker() != null) {
    		return getMakerId(post);
    	}
    	else if(post.getCreator() != null ) {
    		return getCreatorId(post);
    	}
    	return Constants.ILLEGAL_ID;
    }
    protected String getCreatorOrMakerUrl(ISiocPost post) {
    	String url = Constants.EMPTY_STRING;
    	if (post != null && post.getCreator() != null && post.getCreator().getSeeAlso() != null && 
    			!post.getCreator().getSeeAlso().isEmpty()) {
    		url = post.getCreator().getSeeAlso();
    	} else if(post != null && post.getMaker() != null && post.getMaker().getSeeAlso() != null && 
    			!post.getMaker().getSeeAlso().isEmpty()) {
    		url = post.getMaker().getSeeAlso();
    	}
    	return url;
    }
    protected int getCreatorId(ISiocPost post) {
    	ISiocUser creator;
    	int uid = Constants.ILLEGAL_ID;
    	if ((creator = post.getCreator()) != null) {
    		uid = getUserIdBy( Constants.USER_ID_FIELD, encryptor.encrypt( creator.getURI() ) );    		
    	}    	
    	return uid;
    }    
    protected int getMakerId(ISiocPost post) {
    	IFoafPerson maker;
    	int uid = Constants.ILLEGAL_ID;
    	if ((maker = post.getMaker()) != null) {    		
    		uid = getUserIdBy( Constants.USER_ID_FIELD, encryptor.encrypt( maker.getURI() ) );
    	}    	
    	return uid;
    }    
    protected int getUserIdBy(String field, String value) {
		int uid = Constants.ILLEGAL_ID;
    	if (value != null && !value.isEmpty()) {
    		uid = restCon.getUserId(field, value);			
		}
    	return uid;
    }   
    /**
     * add this url to the listener
     * @param url  the url to add to the listener
     */
    protected void addUrlToListener(String url) {
            listener.addURL(url);
    }    
    /**
     * add this url to the listener without listener checking if it already seen it
     * @param url  the url to add to the listener
     */
    protected void addSeenUrlToListener(String url) {
            listener.addURLUnchecked(url);
    }
}
