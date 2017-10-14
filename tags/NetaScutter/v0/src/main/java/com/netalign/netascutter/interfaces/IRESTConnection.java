package com.netalign.netascutter.interfaces;

import java.util.Map;

 /**
 * The <code>IRESTConnection</code> interface allows a connection to a REST server that implements the same methods.
 * This connection enables several methods on the server such as creating entities, updating entities, 
 * getting entity fields etc.  
 * <p>
 * The connection sends HTTP requests to the host to activate the server methods.
 * The request type (GET, POST, PUT, DELETE) determines the type of the method, 
 *  
 * @author yoavram
 * @see com.netalign.netascutter.DrupalRESTConnection
 *
 */
public interface IRESTConnection {

	public abstract void setHttpClient(IHttpClient httpClient);

	public abstract String getHost();

	public abstract void setHost(String host);

	/**
	 * Sends a create node request to the server site.
	 * @param map      a collection with keys and values to of the new node properties.
	 * @return         the node id of the new node, ILLEGAL_ID if failed
	 */
	public abstract int createNode(Map<String, String> map);

	/**
	 * Sends a create user request to the server site.
	 * @param map      a collection with keys and values to of the new user properties.
	 * @return         the user id of the new user, ILLEGAL_ID if failed
	 */
	public abstract int createUser(Map<String, String> map);

	public abstract int createComment(Map<String, String> map);

	/**
	 * Sends an update user request to the server site to update the first user found with a key an value.
	 * @param map      a collection with keys and values to of the new user properties.
	 * @param key	   the key to identify the user by
	 * @param value	   the value to identify the user by
	 * @return         the user id of the new user, ILLEGAL_ID if failed
	 */
	public abstract int updateUser(Map<String, String> map, String key, String value);

	public abstract int updateNode(Map<String, String> map, String key, String value);

	public abstract int updateComment(Map<String, String> map, String key, String value);

	/**
	 * Sends a request to server to check if one user is in the buddy list of another
	 * @param uid       the user whose buddylist to check
	 * @param buddyUid  the user to look for in the buddylist
	 * @return          true if the user is in the buddylist, false if not
	 */
	public abstract boolean checkBuddies(int uid, int buddyUid);

	/**
	 * send a request to the server to add a user to another user's buddylist
	 * @param uid           id of user whose buddylist to add to
	 * @param buddyUid      id user to add to the buddylist
	 */
	public abstract void addBuddy(int uid, int buddyUid);

	/**
	 * sends a request to server to remove a user from another user's buddy list
	 * @param uid       id of user whose buddylist to remvoe from
	 * @param buddyUid  id of user to remove from the buddylist
	 * @return          true if removal was successful, false if not
	 */
	public abstract boolean removeBuddy(int uid, int buddyUid);

	/**
	 * Requests the user id of the first user found with the given key and value
	 * @paranm key      A key for the user profile field to check
	 * @param value     The value to check on given key
	 * @return          the user id found, ILLEGAL_ID if failed
	 * 4 tests written - tested and works
	 */
	public abstract int getUserId(String key, String value);

	public abstract int getNodeId(String key, String value);

	public abstract int getCommentId(String key, String value);

	/**
	 * Requests the first user with given key and value, and returns a specific user field.
	 * @param key       query the users list by this key
	 * @param value     the value of the key to query
	 * @param lookup    the user field to lookup and return it's value
	 * @return          the value of the lookup field, an empty string if failed.
	 */
	public abstract String getUserFieldByKeyValue(String key, String value,
			String lookup);

	public abstract String getNodeFieldByKeyValue(String key, String value,
			String lookup);

	public abstract String getCommentFieldByKeyValue(String key, String value,
			String lookup);

	/**
	 * Requests to check if a user exists with a given key and value
	 * @param key       the key to check
	 * @param value     the value to compare
	 * @return          true if a user exists, false if not
	 */
	public abstract boolean hasUserWithKeyValue(String key, String value);

	public abstract boolean hasNodeWithKeyValue(String key, String value);

	public abstract boolean hasCommentWithKeyValue(String key, String value);

}