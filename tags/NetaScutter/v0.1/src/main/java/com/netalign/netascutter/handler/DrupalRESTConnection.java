package com.netalign.netascutter.handler;

import org.json.*;
import java.net.*;
import java.util.*;

import com.netalign.netascutter.interfaces.*;
import com.netalign.netascutter.utils.HttpMan;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.*;
import com.netalign.netascutter.Constants;

/**
 * <p>
 * The <code>DrupalRESTConnection</code> class allows a connection to a
 * Drupal-REST server, or any other REST server that implements the same
 * methods. This connection enables several methods on the server such as
 * creating entities, updating entities, getting entity fields etc.
 * <p>
 * The connection sends HTTP requests to the host under the url
 * /=/arg1/arg2/arg3/arg4. The request type (GET, POST, PUT, DELETE) determines
 * the type of the method, where GET is a query, POST is a creation of a new
 * item, PUT is an update method and DELETE is a deletion method.
 * <li>arg1 is mandatory and specifies the type of object (user, node, comment,
 * buddy).</li>
 * Other args are optional and determine the type of the method:
 * <li>arg2 is usually an item's member field.</li>
 * <li>arg3 is a value of the member field specified by arg 2.</li>
 * <li>arg4 is a second member field.</li> For a list of methods on the REST
 * server connect to http://host/=/help
 * <p>
 * <br>
 * This class uses the {@link IHttpClient} interface, implemented by the
 * {@link HttpMan} class for HTTP connection.
 * 
 * @author yoavram
 * @see com.netalign.netascutter.utils.HttpMan
 * @see com.netalign.netascutter.utils.IHttpClient
 * 
 */
public class DrupalRESTConnection implements IRESTConnection {

	public static final String BAD_URL_MESSAGE = "Bad URL: ";
	public static final String BUDDY_KEY = "has";
	public static final String BUDDY_RELATIVE_PATH = "buddy";

	public static final String JSON_VALUE_FIELD = "value";
	public static final String JSON_MSGS_FIELD = "messages";
	public static final String JSON_ERROR_VALUE = "error";
	public static final String JSON_POSTFIX = ".json";
	private static final String JSON_WARNING_VALUE = "warning";
	public static final String VALUE = "value";

	protected static final JSONObject EMPTY_JSON_OBJECT = new JSONObject();

	protected static Logger logger = Logger
			.getLogger(DrupalRESTConnection.class);
	protected String host;
	protected URL serverUrl;
	protected String baseUrl;
	protected IHttpClient httpClient;

	public DrupalRESTConnection() {
	}

	public DrupalRESTConnection(String host) {
		setHost(host);
		setHttpClient(new HttpMan());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.netalign.netascutter.IRESTConnection#setHttpClient(com.netalign.
	 * netascutter.interfaces.IHttpClient)
	 */
	@Override
	public void setHttpClient(IHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#getHost()
	 */
	@Override
	public String getHost() {
		return host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#setHost(java.lang.String)
	 */
	@Override
	public void setHost(String host) {
		this.host = host;
		baseUrl = "HTTP" + "://" + host + "/=/";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#createNode(java.util.Map)
	 */
	@Override
	public int createNode(Map<String, String> map) {
		return createObject(map, Constants.NODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#createUser(java.util.Map)
	 */
	@Override
	public int createUser(Map<String, String> map) {
		return createObject(map, Constants.USER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IRESTConnection#createComment(java.util.Map)
	 */
	@Override
	public int createComment(Map<String, String> map) {
		return createObject(map, Constants.COMMENT);
	}

	private int createObject(Map<String, String> map, String objectType) {
		int result = Constants.ILLEGAL_ID;
		String urlStr = baseUrl + objectType + JSON_POSTFIX;
		try {
			serverUrl = new URL(urlStr); // throws MalformedURLException
			JSONObject reply = this.write(map, IHttpClient.POST);
			String idType = getIdForObjectType(objectType);
			result = parseJsonObjectToInt(reply, idType);

		} catch (MalformedURLException e) {
			logger.error(BAD_URL_MESSAGE + e.getMessage());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#updateUser(java.util.Map)
	 */
	@Override
	public int updateUser(Map<String, String> map, String key, String value) {
		return updateObject(map, Constants.USER, key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#updateNode(java.util.Map)
	 */
	@Override
	public int updateNode(Map<String, String> map, String key, String value) {
		return updateObject(map, Constants.NODE, key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IRESTConnection#updateComment(java.util.Map)
	 */
	@Override
	public int updateComment(Map<String, String> map, String key, String value) {
		return updateObject(map, Constants.COMMENT, key, value);
	}

	private int updateObject(Map<String, String> map, String objectType,
			String key, String value) {
		int result = Constants.ILLEGAL_ID;
		String urlStr = baseUrl + objectType + "/" + key + "/" + value
				+ JSON_POSTFIX;
		try {
			serverUrl = new URL(urlStr); // throws MalformedURLException
			JSONObject reply = this.write(map, IHttpClient.PUT);
			result = parseJsonObjectToInt(reply, getIdForObjectType(objectType));
		} catch (MalformedURLException e) {
			logger.error(BAD_URL_MESSAGE + e.getMessage());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#checkBuddies(int, int)
	 */
	@Override
	public boolean checkBuddies(int uid, int buddyUid) {
		boolean result = false;
		if (uid < Constants.DRUPAL_MIN_ID || buddyUid < Constants.DRUPAL_MIN_ID) {
			return result;
		}
		try {
			String urlStr = baseUrl + BUDDY_RELATIVE_PATH + "/"
					+ Integer.toString(uid) + "/" + Integer.toString(buddyUid)
					+ JSON_POSTFIX;
			serverUrl = new URL(urlStr);
			JSONObject reply = this.read();
			if (reply != EMPTY_JSON_OBJECT) {
				result = parseJsonObjectToBoolean(reply, VALUE);
			}
		} catch (MalformedURLException e) {
			logger.error(BAD_URL_MESSAGE + e.getMessage());
		}
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#addBuddy(int, int)
	 */
	@Override
	public void addBuddy(int uid, int buddyUid) {
		if (uid < Constants.DRUPAL_MIN_ID || buddyUid < Constants.DRUPAL_MIN_ID) {
			return;
		}
		try {
			String urlStr = baseUrl + BUDDY_RELATIVE_PATH + "/"
					+ Integer.toString(uid) + "/" + Integer.toString(buddyUid)
					+ JSON_POSTFIX;
			serverUrl = new URL(urlStr);
			// TODO this is semantic , because write doesn't accept empty maps
			HashMap<String, String> map = new HashMap<String, String>(1);
			map.put(Integer.toString(uid), Integer.toString(buddyUid));
			this.write(map, IHttpClient.POST);
		} catch (MalformedURLException e) {
			logger.error(BAD_URL_MESSAGE + e.getMessage());
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#removeBuddy(int, int)
	 */
	@Override
	public boolean removeBuddy(int uid, int buddyUid) {
		boolean result = false;
		if (uid < Constants.DRUPAL_MIN_ID || buddyUid < Constants.DRUPAL_MIN_ID) {
			return result;
		}
		try {
			String urlStr = baseUrl + BUDDY_RELATIVE_PATH + "/"
					+ Integer.toString(uid) + "/" + Integer.toString(buddyUid)
					+ JSON_POSTFIX;
			serverUrl = new URL(urlStr);
			JSONObject reply = this.read(IHttpClient.DELETE);
			if (reply != EMPTY_JSON_OBJECT) {
				result = parseJsonObjectToBoolean(reply, BUDDY_KEY);
			}
		} catch (MalformedURLException e) {
			logger.error(BAD_URL_MESSAGE + e.getMessage());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#getUserId(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int getUserId(String key, String value) {
		int result = Constants.ILLEGAL_ID;
		String reply = getUserFieldByKeyValue(key, value, Constants.UID);
		if (!reply.isEmpty()) {
			result = Integer.parseInt(reply);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.IRESTConnection#getNodeId(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int getNodeId(String key, String value) {
		int result = Constants.ILLEGAL_ID;
		String reply = getNodeFieldByKeyValue(key, value, Constants.NID);
		if (!reply.isEmpty()) {
			result = Integer.parseInt(reply);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IRESTConnection#getCommentId(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int getCommentId(String key, String value) {
		int result = Constants.ILLEGAL_ID;
		String reply = getCommentFieldByKeyValue(key, value, Constants.CID);
		if (!reply.isEmpty()) {
			result = Integer.parseInt(reply);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IRESTConnection#getUserFieldByKeyValue(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getUserFieldByKeyValue(String key, String value, String lookup) {
		return getObjectFieldByKeyValue(key, value, lookup, Constants.USER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IRESTConnection#getNodeFieldByKeyValue(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getNodeFieldByKeyValue(String key, String value, String lookup) {
		return getObjectFieldByKeyValue(key, value, lookup, Constants.NODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IRESTConnection#getCommentFieldByKeyValue(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getCommentFieldByKeyValue(String key, String value,
			String lookup) {
		return getObjectFieldByKeyValue(key, value, lookup, Constants.COMMENT);
	}

	private String getObjectFieldByKeyValue(String key, String value,
			String lookup, String objectType) {
		String result = Constants.EMPTY_STRING;
		if (key == null || key.isEmpty() || value == null || value.isEmpty()
				|| lookup == null || lookup.isEmpty()) {
			return result;
		}
		try {
			String urlStr = baseUrl + objectType + "/" + key + "/"
					+ URLEncoder.encode(value, Constants.UTF8_ENCODING) + "/"
					+ lookup + JSON_POSTFIX;
			// String urlStr = baseUrl + objectType + "/" + key + "/" + value +
			// "/" + lookup + JSON_POSTFIX;

			serverUrl = new URL(urlStr); // throws Malformed...
			JSONObject reply = this.read();
			if (reply != EMPTY_JSON_OBJECT) {
				result = parseJsonObject(reply, lookup);
			}
		} catch (MalformedURLException e) {
			logger.error(BAD_URL_MESSAGE + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IRESTConnection#hasUserWithKeyValue(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public boolean hasUserWithKeyValue(String key, String value) {
		if (getUserId(key, value) != Constants.ILLEGAL_ID) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IRESTConnection#hasNodeWithKeyValue(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public boolean hasNodeWithKeyValue(String key, String value) {
		if (getNodeId(key, value) != Constants.ILLEGAL_ID) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.IRESTConnection#hasCommentWithKeyValue(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public boolean hasCommentWithKeyValue(String key, String value) {
		if (getCommentId(key, value) != Constants.ILLEGAL_ID) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteNode(String key, String value) {
		return deleteObjectByKeyValue(key, value, Constants.NODE);
	}
	
	private boolean deleteObjectByKeyValue(String key, String value, String objectType) {
		boolean result = false;
		if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
			return result;
		}
		try {
			String urlStr = baseUrl + objectType + "/" + key + "/"
					+ URLEncoder.encode(value, Constants.UTF8_ENCODING) + JSON_POSTFIX;

			serverUrl = new URL(urlStr); // throws Malformed...
			JSONObject reply = this.delete();
			if (reply != EMPTY_JSON_OBJECT) {
				result = parseJsonObjectToBoolean(reply, VALUE);
			}
		} catch (MalformedURLException e) {
			logger.error(BAD_URL_MESSAGE + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		return result;
	}
	
	@Override
	public boolean addTermNodeRelation(int nid, int tid) {
		if (nid < Constants.DRUPAL_MIN_ID || tid < Constants.DRUPAL_MIN_ID) {
			return false;
		}
		try {			
			String urlStr = baseUrl + Constants.TERM + "/" + Constants.TID + "/"
					+ Integer.toString(tid) + "/" + Constants.NODE + "/" + Integer.toString(nid)
					+ JSON_POSTFIX;
			serverUrl = new URL(urlStr);
			// this is semantic , because write doesn't accept empty maps
			HashMap<String, String> map = new HashMap<String, String>(1);
			map.put(Integer.toString(nid), Integer.toString(tid));
			this.write(map, IHttpClient.POST);
		} catch (MalformedURLException e) {
			logger.error(BAD_URL_MESSAGE + e.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean checkTermNodeRelation(int nid, int tid) {
		String result = getNodeFieldByKeyValue(Constants.NID, Integer.toString(nid), Constants.TID);		
		return result.equals(Integer.toString(tid));
	}

	@Override
	public int createTerm(Map<String, String> map) {
		return createObject(map, Constants.TERM);
	}

	@Override
	public int getTermId(String key, String value) {
		int result = Constants.ILLEGAL_ID;
		String reply = getTermFieldByKeyValue(key, value, Constants.TID);
		if (!reply.isEmpty()) {
			result = Integer.parseInt(reply);
		}
		return result;
	}
	
	@Override
	public String getTermFieldByKeyValue(String key, String value, String lookup) {
		return getObjectFieldByKeyValue(key, value, lookup, Constants.TERM);
	}

	@Override
	public int updateTerm(Map<String, String> map, String key, String value) {
		return updateObject(map, Constants.TERM, key, value);
	}
	
	/**
	 * reads from the server url using the requested method (GET or DELETE).
	 * 
	 * @return the JSONObject that was read. May return an empty JSONObject if
	 *         failed.
	 */
	private JSONObject read(String method) {
		JSONObject reply = EMPTY_JSON_OBJECT;
		try {
			String httpResult = Constants.EMPTY_STRING;
			if (method.equals(IHttpClient.DELETE)) { // DELETE HTTP
				httpResult = httpClient.delete(serverUrl);
			} else { // else do a GET HTTP
				httpResult = httpClient.get(serverUrl);
			}
			if (!httpResult.isEmpty()) {
				reply = new JSONObject(httpResult);
			}
		} catch (JSONException e) {
			logger.error("Failed parsing JSON answer from "
					+ serverUrl.toString() + ": " + e.getMessage());
		}
		return reply;
	}

	/**
	 * reads from the server url using the GET method
	 * 
	 * @return the JSONObject that was read. May return an empty JSONObject if
	 *         failed.
	 */
	private JSONObject read() {
		return read(IHttpClient.GET);
	}
	
	/**
	 * deletes from the server url using the DELETE method
	 * 
	 * @return the JSONObject that was read. May return an empty JSONObject if
	 *         failed.
	 */
	private JSONObject delete() {
		return read(IHttpClient.DELETE);
	}

	/**
	 * writes to the server url and reads response using the requested method
	 * 
	 * @return the JSONObject that was read. May return an empty JSONObject if
	 *         failed.
	 */
	private JSONObject write(Map<String, String> map, String method) {
		JSONObject reply = EMPTY_JSON_OBJECT;
		if (map == null || map.isEmpty()) {
			return reply;
		}
		try {
			// send request and try to parse it if it is not an empty string
			String httpResult = Constants.EMPTY_STRING;
			if (method.equals(IHttpClient.PUT)) { // PUT HTTP
				httpResult = httpClient.put(serverUrl, map);
			} else { // else do a POST HTTP
				httpResult = httpClient.post(serverUrl, map);
			}
			if (!httpResult.isEmpty()) {
				reply = new JSONObject(httpResult);
			}
		} catch (JSONException e) {
			logger.error("Failed parsing JSON answer from "
					+ serverUrl.toString() + ": " + e.getMessage());
		}
		return reply;
	}

	private String getIdForObjectType(String objectType) {
		String id = Constants.EMPTY_STRING;
		if (objectType.equals(Constants.USER)) {
			id = Constants.UID;
		} else if (objectType.equals(Constants.NODE)) {
			id = Constants.NID;
		} else if (objectType.equals(Constants.COMMENT)) {
			id = Constants.CID;
		} else if (objectType.equals(Constants.TERM)) {
			id = Constants.TID;
		}
		return id;
	}

	/**
	 * Parses a JSONObject to retrieve the value of a specific key.
	 * 
	 * @param json
	 *            the json object to parse
	 * @param key
	 *            the specific key to parse
	 * @return the value of the key, may return an empty string
	 */
	private String parseJsonObject(JSONObject json, String key) {
		String output = Constants.EMPTY_STRING;
		if (json == EMPTY_JSON_OBJECT) {
			return output;
		}
		try {
			// parse the messages
			if (json.has(JSON_MSGS_FIELD)
					&& json.get(JSON_MSGS_FIELD) instanceof JSONObject) {
				JSONObject msgs = json.getJSONObject(JSON_MSGS_FIELD);
				if (msgs.has(JSON_ERROR_VALUE)) {
					logger.error(decodeJsonMessage(msgs
							.getString(JSON_ERROR_VALUE)));					
				} else if (msgs.has(JSON_WARNING_VALUE)) {
					logger.warn(decodeJsonMessage(msgs
							.getString(JSON_WARNING_VALUE)));
				} else {
					logger.warn("Server reply not parsed: " + msgs.toString());
				}
			}
			// parse value
			if (json.has(JSON_VALUE_FIELD)
					&& json.get(JSON_VALUE_FIELD) instanceof JSONObject) {
				JSONObject val = json.getJSONObject(JSON_VALUE_FIELD);
				if (val.has(key)) {
					output = val.getString(key);
				} else {
					logger.debug("Key " + key
							+ " couldn't be found in server reply");
				}
			} else if (json.has(JSON_VALUE_FIELD)
					&& json.get(JSON_VALUE_FIELD) instanceof Boolean) {
				output = Boolean.toString(json.getBoolean(JSON_VALUE_FIELD));
			} else if (json.has(JSON_VALUE_FIELD)
					&& json.get(JSON_VALUE_FIELD) instanceof String) {
				output = json.getString(JSON_VALUE_FIELD);
			}
			if (!json.has(JSON_VALUE_FIELD) && !json.has(JSON_MSGS_FIELD)) {
				// value is not a valid JSONObject and messages isn't either
				logger
						.warn("Reply from server was empty - no expected JSON objects");
			}
		} catch (JSONException e) {
			logger.warn("Failed getting key '" + key + "' from JSON object: "
					+ e.getMessage());
		}
		return output;
	}

	/**
	 * parses a JSON object field to an integer
	 * 
	 * @param json
	 *            a JSON object
	 * @param key
	 *            the key of the field to parse
	 * @return the standard java Integer.parseInt() of the JSON value by the key
	 *         given
	 */
	private int parseJsonObjectToInt(JSONObject json, String key) {
		int output = Constants.ILLEGAL_ID;
		String str = parseJsonObject(json, key);
		if (!str.isEmpty()) {
			try {
				output = Integer.parseInt(str);
			} catch (NumberFormatException e) {
				logger.debug("JSON value for key " + key
						+ " is not numerical: " + str);
			}
		}
		return output;
	}

	/**
	 * parses a JSON object field to a boolean
	 * 
	 * @param json
	 *            a JSON object
	 * @param key
	 *            the key of the field to parse
	 * @return the standard java Boolean.valueOf() of the JSON value by the key
	 *         given
	 */
	private Boolean parseJsonObjectToBoolean(JSONObject json, String key) {
		return Boolean.valueOf(parseJsonObject(json, key));
	}

	/**
	 * decodes JSON messages from the Drupal REST server, which for example,
	 * encodes " to \&quot;
	 * 
	 * @see http://www.w3.org/TR/html401/sgml/entities.html
	 * @param str
	 *            to decode
	 * @return a decoded string
	 */
	private String decodeJsonMessage(String str) {
		int start = 0;
		int end = 0;
		str = str.replaceAll("\\\\n", " - ");
		str = str.replaceAll("&#039;", "'");
		str = str.replaceAll("\\\\&quot;", "\"");
		str = str.replaceAll("&amp;", "&"); // last, because it is the escape
		// char
		if (str.startsWith("[\"")) {
			start = 2;
		}
		if (str.endsWith("\"]")) {
			end = 2;
		}
		return str.substring(start, str.length() - end);
	}
}
