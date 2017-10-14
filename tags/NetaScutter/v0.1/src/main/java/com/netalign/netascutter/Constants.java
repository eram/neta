package com.netalign.netascutter;

/**
 *
 * @author yoavram
 */
public class Constants {
    /**
     * Empty string constant. Will return 'true' on .isEmpty().
     */
    public static final String EMPTY_STRING = "";
    /**
     * A constant string array of length 0.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * A constant object array of length 0.
     */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    /**
     * Illegal ID for users and nodes.
     */
	public static final int ILLEGAL_ID = -1;
	/**
	 * The minimum ID allowed in Drupal system
	 */
	public static final int DRUPAL_MIN_ID = 1;     // no ids exist below 1 (0 is guest)
	public static final String NID = "nid";
	public static final String UID = "uid";
	public static final String CID = "cid";
	public static final String TID = "tid";
	public static final String VID = "vid";
	public static final String NODE = "node";
	public static final String USER = "user";
	public static final String COMMENT = "comment";
	public static final String TERM = "term";
	public static final String NODE_TYPE = "forum";		
	public static final String USER_URI_FIELD = "uri_sha1sum";
	public static final String USER_MBOX_FIELD = "foaf_mbox_sha1sum";
	public static final String COMMENT_URI_FIELD = "homepage";
	public static final String NODE_URI_FIELD = "field_uri_sha1sum_value";
	public static final String TERM_URI_FIELD = "synonyms";
	public static final String BUDDY = "buddy";
	public static final String PARENT_ID_FIELD = "parent_uri_sha1sum"; // see restapi_comment.module
	public static final String FORUMS_VID = "1";
	/**
	 * This is one of the fields for a comment, stating the comment thread in the post.
	 */
	public static final String THREAD = "thread";
	public static final String UTF8_ENCODING = "utf8";
	public static final String VERSION = "v.0.1";
	public static final String NETASCUTTER = "NetaScutter";

	
}
