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
 * The <code>RESTPersonHandler</code> class implements the {@link IHandler}
 * interface.
 * <p>
 * The class handles {@link IFoafPerson} objects, communicating them to a
 * {@link Drupal}-like REST server using an {@link IRESTConnection}
 * implementation.
 * 
 * @author yoavram
 * @see IHandler
 * @see RESTAbstractHandler
 * @see IFoafPerson
 * @see IRESTConnection TODO create user for person without URI but with
 *      MBOXSHA1
 */
public class RESTPersonHandler extends RESTAbstractHandler<IFoafPerson> {

	protected static Logger logger = Logger.getLogger(RESTPersonHandler.class);

	@Override
	public synchronized void handle(IFoafPerson person, String personUrl) {
		logger.debug("Handling person with URI " + person.getURI()
				+ " from URL " + personUrl);
		// add all non-existing friends
		List<IFoafPerson> friends = person.getFriends();
		// keep uids of friends to add person's friends list
		Set<Integer> friendsUids = new HashSet<Integer>();
		for (IFoafPerson friend : friends) {
			// get user seed - if partial or full no need to create it
			int fuid = getUserUid(friend);
			if (fuid == Constants.ILLEGAL_ID) {
				fuid = createPerson(friend, friend.getSeeAlso());
			}
			// adds also ILLEGAL UID - we'll remove it soon
			friendsUids.add(fuid);
			listener.addURL(friend.getSeeAlso());
		} // end of for loop on friends
		friendsUids.remove(Constants.ILLEGAL_ID);

		int uid = getUserUid(person);
		String seeAlso = person.getSeeAlso();
		if (seeAlso.isEmpty()) {
			seeAlso = personUrl;
		}
		if (uid == Constants.ILLEGAL_ID) {
			uid = createPerson(person, seeAlso);
			listener.addURL(seeAlso);
		} else {
			uid = updatePerson(person, seeAlso);
		}

		// add friends to person's friends list
		addToFriendsList(uid, friendsUids);
		logger.debug("Done handling person with URI " + person.getURI()
				+ " from URL " + personUrl);
	}

	/**
	 * get a user id for a person from the REST server
	 * 
	 * @param person
	 *            an IFoafPerson instance
	 * @param personUrl
	 *            the url of the person foaf resource
	 * @return uid for the person, RESTConnection.ILLEGAL_ID if failed
	 */
	protected int getUserUid(IFoafPerson person) {
		// IF CHANGING USER ID METHOD CHANGE IT ALSO AT
		// RESTPostHandler.getMakerId()
		int uid = restCon.getUserId(Constants.USER_URI_FIELD, encryptor
				.encrypt(person.getURI()));
		if (uid == Constants.ILLEGAL_ID) {
			// try by mboxsha1
			uid = restCon.getUserId(Constants.USER_MBOX_FIELD, person
					.getMboxSha1Sum());
		}
		return uid;
	}

	/**
	 * create a user on the REST server
	 * 
	 * @param person
	 *            an IFoafPerson instance
	 * @param urlStr
	 *            the url from which the IFoafPerson was extracted from
	 * @param seed
	 *            the seed to set on the created user
	 * @return the uid of the created user
	 */
	protected int createPerson(IFoafPerson person, String urlStr) {
		return createOrUpdatePerson(person, urlStr, true);
	}

	/**
	 * update a user on the REST server
	 * 
	 * @param person
	 *            an IFoafPerson instance
	 * @param urlStr
	 *            the url from which the IFoafPerson was extracted from
	 * @param seed
	 *            the seed to set on the created user
	 * @return the uid of the updated user
	 */
	protected int updatePerson(IFoafPerson person, String urlStr) {
		return createOrUpdatePerson(person, urlStr, false);
	}

	/**
	 * create or update a user in the REST server
	 * 
	 * @param person
	 *            an IFoafPerson instance
	 * @param urlStr
	 *            the url from which the IFoafPerson was extracted from
	 * @param seed
	 *            the seed to set on the created/updated user
	 * @param create
	 *            true will create a user, false will update the user
	 * @return
	 */
	protected int createOrUpdatePerson(IFoafPerson person, String urlStr,
			boolean create) {
		Map<String, String> map = converter.convertPerson(person, urlStr);
		int uid = Constants.ILLEGAL_ID;
		if (create) {
			if (person.getURI().isEmpty()) {
				logger.warn("Can't create user for person without a URI: "
						+ urlStr);
				return Constants.ILLEGAL_ID;
			}
			uid = restCon.createUser(map);
			if (uid == Constants.ILLEGAL_ID) { // failed creating user
				logger.warn("Failed creating user for person with URI "
						+ person.getURI());
			} else {
				logger.info("Created user with UID " + uid
						+ " for person with URI " + person.getURI());
			}
		} else {
			// update
			uid = getUserUid(person);
			int newuid = restCon.updateUser(map, Constants.UID, Integer
					.toString(uid));
			if (newuid == Constants.ILLEGAL_ID) { // failed updating user
				logger.info("Failed updating user for person with UID "
						+ Integer.toString(uid));
			} else if (newuid == uid) {
				logger.info("Updated user for person with UID "
						+ Integer.toString(uid));
			} else {
				logger
						.error("New UID is different from the old one, update failed for person with UID "
								+ uid);
				uid = Constants.ILLEGAL_ID;
			}
		}
		return uid;
	}

	/**
	 * add a user to another user's friends list
	 * 
	 * @param personUid
	 *            this id identifies the user whose friends list we add to
	 * @param uid
	 *            this id identifies the user we add to the list
	 */
	protected void addToFriendsList(int personUid, int uid) {
		boolean buddies = restCon.checkBuddies(personUid, uid);
		if (buddies) {
			logger.debug("UID " + Integer.toString(uid)
					+ " is already a friend of UID "
					+ Integer.toString(personUid));
		} else {
			restCon.addBuddy(personUid, uid);
			buddies = restCon.checkBuddies(personUid, uid);
			if (buddies) {
				logger.debug("Addded UID " + Integer.toString(uid)
						+ " to friends list of UID "
						+ Integer.toString(personUid));
			} else {
				logger.warn("Failed adding UID " + Integer.toString(uid)
						+ " to friends list of UID "
						+ Integer.toString(personUid));
			}
		}
	}

	/**
	 * add users to another user's firends list
	 * 
	 * @param personUid
	 *            this id identifies the user whose friends list we add to
	 * @param uid
	 *            this ids identify the users we add to the list
	 */
	protected void addToFriendsList(int personUid, Set<Integer> uids) {
		if (personUid != Constants.ILLEGAL_ID) {
			for (int uid : uids) {
				addToFriendsList(personUid, uid);
			}
		}
	}
}
