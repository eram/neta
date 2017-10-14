package com.netalign.netascutter;

import com.netalign.netascutter.interfaces.*;
import com.netalign.netascutter.utils.*;
import com.netalign.sioc.*;
import java.util.*;
import org.apache.log4j.*;

/**
 *
 * @author yoavram
 */
public class RESTPersonAggregator implements IAggregator {
    
    protected static Logger logger = Logger.getLogger(RESTPersonAggregator.class);
    protected IRESTConnection restCon; 
    protected IUrlListener listener;
    protected IConverter converter;
    private IEncryptor encryptor;
    protected String host;  

    /**
     * Empty constructor, must set got or connection, listener and converter for proper work
     */
    public RESTPersonAggregator() {
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
     * add a person object to the REST server
     * @param incoming      an IFoafPerson instance
     * @param urlStr        the url from which the IFoafPerson was extracted from
     */
    @Override
    public void add(Object incoming, String urlStr) {        
        synchronized (this) {
            try {
                logger.debug(Thread.currentThread().getName() + " has the aggregator with " + urlStr);
                if (incoming instanceof IFoafPerson) {
                    aggregatePerson((IFoafPerson) incoming, urlStr);
                }
            } catch (Exception e) {
                logger.error(e);
            } finally {
                logger.debug(Thread.currentThread().getName() + " releases the aggregator");
            }
        }
    }

    /**
     * aggregates an IFoafPerson. logic resides here
     * @param person    an IFoafPerson instance
     * @param personUrl    the url from which the IFoafPerson was extracted from
     */
    protected void aggregatePerson(IFoafPerson person, String personUrl) { 
        // add all non-existing friends      
        List<IFoafPerson> friends = person.getFriends();
        Set<Integer> friendsUids = new HashSet<Integer>(); // keep uids of friends to add them to the person's friends list        
        for (IFoafPerson friend : friends) {
            int fuid =getUserUid(friend);    // get user seed - if partial or full no need to create it         
            if (fuid == Constants.ILLEGAL_ID) {
                fuid = createPerson(friend, friend.getSeeAlso());
            } else {  // get uid for a friend that's already been created
                fuid = getUserUid(friend);
            }
            friendsUids.add(fuid); // adds also ILLEGAL UID - we'll remove it  soon                  
            listener.addURL(friend.getSeeAlso());
        } // end of for loop on friends 
        friendsUids.remove(Constants.ILLEGAL_ID);

        int uid = getUserUid(person);
        String seeAlso = person.getSeeAlso();
        if (seeAlso.isEmpty()) {
        	seeAlso = personUrl;
        }
        if (uid == Constants.ILLEGAL_ID ) {        	
   			uid = createPerson(person, seeAlso);
    		listener.addURL(seeAlso);
        } else { 
        	uid = updatePerson(person, seeAlso);        	
        } 
        
        // add friends to person's friends list    
        addToFriendsList(uid, friendsUids);    
    }

	/**
     * create or update a user in the REST server
     * @param person    an IFoafPerson instance
     * @param urlStr    the url from which the IFoafPerson was extracted from
     * @param seed      the seed to set on the created/updated user
     * @param create    true will create a user, false will update the user
     * @return
     */
    protected int createOrUpdatePerson(IFoafPerson person, String urlStr, boolean create) {
        Map<String, String> map = converter.convertPerson(person, urlStr);        
        int uid = Constants.ILLEGAL_ID;
        if (create) {
            if (person.getURI().isEmpty()) { 
                logger.warn("Can't create user for person without a URI: " + urlStr);
                return Constants.ILLEGAL_ID;
            }
            uid = restCon.createUser(map);
            if (uid == Constants.ILLEGAL_ID) { // failed creating user
                logger.warn("Failed creating user for person with URI " + person.getURI());
            } else {
                logger.info("Created user with UID " + uid + " for person with URI " + person.getURI());
            }
        } else {
            // update             
            uid = getUserUid(person);
        	int newuid = restCon.updateUser(map, Constants.UID, Integer.toString(uid));
            if (newuid == Constants.ILLEGAL_ID) { // failed updating user                
                logger.info("Failed updating user for person with UID " + Integer.toString(uid));
            } else if (newuid == uid){
                logger.info("Updated user for person with UID " + Integer.toString(uid));
            } else {
            	logger.error("New UID is different from the old one, update failed for person with UID " + uid);
            	uid = Constants.ILLEGAL_ID;
            }
        }
        return uid;
    }

    /**
     * create a user on the REST server
     * @param person    an IFoafPerson instance
     * @param urlStr    the url from which the IFoafPerson was extracted from
     * @param seed      the seed to set on the created user
     * @return          the uid of the created user
     */
    protected int createPerson(IFoafPerson person, String urlStr) {
        return createOrUpdatePerson(person, urlStr, true);
    }

    /**
     * update a user on the REST server
     * @param person    an IFoafPerson instance
     * @param urlStr    the url from which the IFoafPerson was extracted from
     * @param seed      the seed to set on the created user
     * @return          the uid of the updated user
     */
    protected int updatePerson(IFoafPerson person, String urlStr) {
        return createOrUpdatePerson(person, urlStr, false);
    }

    /**
     * add a user to another user's firends list
     * @param personUid      this id identifies the user whose friends list we add to
     * @param uid            this id identifies the user we add to the list
     */
    protected void addToFriendsList(int personUid, int uid) {
        boolean buddies = restCon.checkBuddies(personUid, uid);
        if (buddies) {
            logger.debug("UID " + Integer.toString(uid) + " is already a friend of UID " + Integer.toString(personUid));
        } else {
            restCon.addBuddy(personUid, uid);
            buddies = restCon.checkBuddies(personUid, uid);
            if (buddies) {
                logger.debug("Addded UID " + Integer.toString(uid) + " to friends list of UID " + Integer.toString(personUid));
            } else {
                logger.warn("Failed adding UID " + Integer.toString(uid) + " to friends list of UID " + Integer.toString(personUid));
            }
        }
    }

    /**
     * add users to another user's firends list
     * @param personUid      this id identifies the user whose friends list we add to
     * @param uid            this ids identify the users we add to the list
     */
    protected void addToFriendsList(int personUid, Set<Integer> uids) {
        if (personUid != Constants.ILLEGAL_ID) {
            for (int uid : uids) {
                addToFriendsList(personUid, uid);
            }
        }
    }

    /**
     * get a user id for a person from the REST server
     * @param person    an IFoafPerson instance
     * @param personUrl the url of the person foaf resource
     * @return          uid for the person, RESTConnection.ILLEGAL_ID if failed
     */
    protected int getUserUid(IFoafPerson person) {
    	int uid = Constants.ILLEGAL_ID;
   		uid = restCon.getUserId(Constants.USER_ID_FIELD, encryptor.encrypt(person.getURI()));
        return uid;
    }
    
    /**
     * add this urls to the listener
     * @param urls  the urls to add to the listener
     */
    protected void addUrlsToListener(Set<String> urls) {
        if (listener != null) {
	    	Iterator<String> it = urls.iterator();
	        while (it.hasNext()) { // TODO don't add empty urls? construct the URL here?
	            listener.addURL(it.next());
	        }
        }
    }
}
