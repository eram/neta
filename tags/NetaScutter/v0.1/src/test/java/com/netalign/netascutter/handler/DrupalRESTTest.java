package com.netalign.netascutter.handler;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netalign.netascutter.Constants;
import com.netalign.netascutter.interfaces.IHttpClient;
import com.netalign.netascutter.utils.HttpMan;


/**
 * TODO - test take about 6-8 secs...
 * 
 * @author yoavram
 *
 */
public class DrupalRESTTest {
	
	private static final String POSTFIX = ".json";
	private static final String SLASH = "/";
	private static final String NAME = "name";
	private static final String USERNAME = "testuser";
	private static final String MAIL = "mail";
	private static final String USERMAIL = "testemail@netalign.com";
	private static final String VALUE = "value";
	private static final String TITLE = "title";
	private static final String BODY = "body";
	private static final String COMMENT = "comment";
	private static final String TITLE_TXT = "testpost";
	private static final String BODY_TXT = "testing 1 2 3";
	private static final String TYPE = "type";
	private static final String PASSWORD = "pass";
	private static final String ADMIN = "admin";
	private static final String LOGIN = "login";
	private static final String DEBUG = "?XDEBUG_SESSION_START=drupal";
	private static final String SUBJECT_TXT = "testcomment";
	private static final String SUBJECT = "subject";
	private static final String COMMENT_TXT = "testing the comments";
	
	private static String host;
	private static IHttpClient http;
	private static int uid;
	private static int nid;
	private static int cid;
	
    @BeforeClass
    public static void setUpClass() throws Exception {
    	host = "http://192.168.123.4/=/";
		http = new HttpMan();
		System.out.println("Make sure previous test runs did not leave undeleted users or posts, this will ruin the tests");
    }
    
	@Before
    public void setUp() {
		
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testPostLogin() {
    	System.out.println("testLogin");
    	Map<String,String> map = new HashMap<String, String>();
    	map.put(NAME, ADMIN);
    	map.put(PASSWORD, ADMIN);
    	String result = http.post(host+LOGIN+POSTFIX, map);      
    	
    	//System.out.println(result);
    	
    	assertTrue("login reply incorrect", hasSubstring(result, Constants.USER+SLASH+Integer.toString(Constants.DRUPAL_MIN_ID)));    	
    }
    
    @Test
    public void testGetLogin() {
    	System.out.println("testGetLogin");
    	String result = http.get(host+LOGIN+POSTFIX);      
    	
    	//System.out.println(result);
    	
    	assertTrue("login reply incorrect", hasSubstring(result, Constants.UID));    	
    }
    
    
    /**
     * on GET     /=/user                        list all columns
     */
    @Test
    public void testGetUser() {
    	System.out.println("testGetUser");
    	String result = http.get(host+Constants.USER+POSTFIX);
    	
    	assertNotNull("null reply from server", result);
    	assertFalse("empty reply from server", result.isEmpty());
    	    	
    	//System.out.println(result);
    	
    	assertTrue("server reply missing " + Constants.UID, hasSubstring(result, Constants.UID) );
    }
    
    /**
     * on GET     /=/node                        list all columns
     */
    @Test
    public void testGetNode() {
    	System.out.println("testGetNode");
    	String result = http.get(host+Constants.NODE+POSTFIX);
    	
    	assertNotNull("null reply from server", result);
    	assertFalse("empty reply from server", result.isEmpty());
    	   	
    	System.out.println(result);
    	
    	assertTrue("server reply missing " + Constants.NID, hasSubstring(result, Constants.NID) );
    }
    
    /**
     * on GET     /=/comment                     list all columns
     */
    @Test
    public void testGetComment() {
    	System.out.println("testGetComment");
    	String result = http.get(host+Constants.COMMENT+POSTFIX);
    	
    	assertNotNull("null reply from server", result);
    	assertFalse("empty reply from server", result.isEmpty());
    	   	
    	//System.out.println(result);
    	
    	assertTrue("server reply missing " + Constants.CID, hasSubstring(result, Constants.CID) );
    }
    
	/**
	on POST    /=/user                        create a user
     */
    @Test
    public void testPostUser() {
    	System.out.println("testPostUser");
    	uid = 0;
    	Map<String,String> map = new HashMap<String,String>();
    	map.put(NAME, USERNAME);
    	map.put(MAIL, USERMAIL);
    	String result = http.post(host+Constants.USER+POSTFIX, map);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	 	
    	uid = getInt(result, Constants.UID);    
    	assertTrue(Constants.UID+" is illegal: "+Integer.toString(uid), uid >= Constants.DRUPAL_MIN_ID);
    	
    	//System.out.println(result);
    	//System.out.println(Constants.UID+SLASH+Integer.toString(uid));
    }
  
    /**
    on POST    /=/buddy/<uid>/<buddy_uid>     add the second to the buddylist of the first
     */
    @Test
    public void testPostBuddyUidBuddyUid() {
    	System.out.println("testPostBuddyUidBuddyUid");
    	String myUid = Integer.toString(Constants.DRUPAL_MIN_ID); 
    	String buddyUid = Integer.toString(uid);
    	HashMap<String, String> map = new HashMap<String, String>(1); // TODO this is semantic, because write doesn't accept empty maps
        map.put(myUid,buddyUid);
    	String result = http.post(host+Constants.BUDDY+SLASH+myUid+SLASH+buddyUid+POSTFIX, map);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	
    	//System.out.println(result);
    	
    	assertTrue( "missing " + Boolean.TRUE.toString() +" in server reply", hasSubstring(result, Boolean.TRUE.toString()));
    }
    
    /**
    on GET     /=/buddy/<uid>                 the buddylist for this user
     */
    @Test
    public void testGetBuddyUid() {
    	System.out.println("testGetBuddyUid");    	
    	String myUid = Integer.toString(Constants.DRUPAL_MIN_ID);    	        
    	String result = http.get(host+Constants.BUDDY+SLASH+myUid+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	
    	//System.out.println(result);
    	
    	assertTrue( Constants.UID + " missing", hasSubstring(result, Integer.toString(uid)));
    }

    /**
    on DELETE  /=/buddy/<uid>/<buddy_uid>     remove the second from the buddylist of the first
     */
    @Test
    public void testDeleteBuddyUidBuddyUid() {
    	System.out.println("testDeleteBuddyUidBuddyUid");
    	String buddyUid = Integer.toString(Constants.DRUPAL_MIN_ID);
    	String myUid = Integer.toString(uid);
    	String result = http.delete(host+Constants.BUDDY+SLASH+buddyUid+SLASH+myUid+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	
    	//System.out.println(result);
    	
    	assertTrue( "missing " + Boolean.TRUE.toString() +" in server reply", hasSubstring(result, Boolean.TRUE.toString()));
    }
    
    /**
    on GET     /=/buddy/<uid>/<buddy_uid>     tests if the second is in the buddylist of the first
     */
    @Test
    public void testGetBuddyUidBuddyUid() {
    	System.out.println("testGetBuddyUidBuddyUid");    	
    	String myUid = Integer.toString(Constants.DRUPAL_MIN_ID); 
    	String buddyUid = Integer.toString(uid);
    	String result = http.get(host+Constants.BUDDY+SLASH+myUid+SLASH+buddyUid+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	
    	//System.out.println(result);
    	
    	assertTrue( "missing " + Boolean.FALSE.toString() +" in server reply", hasSubstring(result, Boolean.FALSE.toString()));
    }
    
    /**
	on GET     /=/user/<column>               list distinct values for that column
     */
    @Test
    public void testGetUserColumn() {
    	System.out.println("testGetUserColumn");
    	String result = http.get(host+Constants.USER+SLASH+Constants.UID+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	
    	assertTrue("server reply missing " + Constants.UID,  hasSubstring(result, Integer.toString(uid)));
    	
    	//System.out.println(result);
    }
    
    /**
	on GET     /=/user/<column>/<key>         load the first user where <column> = <key>
     */
    @Test
    public void testGetUserColumnKey() {
    	System.out.println("testGetUserColumnKey");
    	
    	String result = http.get(host+Constants.USER+SLASH+Constants.UID+SLASH+Integer.toString(uid)+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	
    	assertTrue("bad " + Constants.UID, getInt(result,Constants.UID) == uid );    	    	    	
    	assertTrue("bad " + NAME, getString(result, NAME).equals(USERNAME) );    	
    	
    	//System.out.println(result);
    }
    
    /**
	on GET     /=/user/<column>/<key>/<field> show the field for the first user where <column> = <key>
     */
    @Test
    public void testGetUserColumnKeyField() {
    	System.out.println("testGetUserColumnKeyField");
    	String result = http.get(host+Constants.USER+SLASH+Constants.UID+SLASH+ Integer.toString(uid) +SLASH+NAME+POSTFIX);    	
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	
    	//System.out.println(result);
    	
    	assertTrue(VALUE + " was not found", hasSubstring(result, VALUE));    	
    	assertTrue("Unexpected " + NAME, getString(result, VALUE).equals(USERNAME));    			    	    
    }
    
    /**
	on PUT     /=/user/<column>/<key>         update the first user where <column> = <key>
     */
    @Test
    public void testUpdateUserColumnKey() {
    	System.out.println("testDeleteUserColumnKey");
    	Map<String,String> map = new HashMap<String,String>();
    	map.put(NAME, USERNAME+USERNAME);
    	map.put(MAIL, USERMAIL);
    	String result = http.put(host+Constants.USER+SLASH+Constants.UID+SLASH+Integer.toString(uid)+POSTFIX, map);

    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	 	
    	uid = getInt(result, Constants.UID);    
    	assertTrue(Constants.UID+" is illegal: "+Integer.toString(uid), uid >= Constants.DRUPAL_MIN_ID);
    	assertTrue(getString(result, NAME).equals(USERNAME+USERNAME));
    	
    	//System.out.println(result);
    }
         
    /**
	on DELETE  /=/user/<column>/<key>         delete the first user where <column> = <key>
     */
    @Test
    public void testDeleteUserColumnKey() {
    	System.out.println("testDeleteUserColumnKey");

    	String result = http.delete(host+Constants.USER+SLASH+Constants.UID+SLASH+Integer.toString(uid)+POSTFIX);
    	
    	assertTrue("'deleted' was not found in server reply", hasSubstring(result, "deleted"));
    	//System.out.println(result);
    }
             
	/**
	on POST    /=/node                        create a node
     */
    @Test
    public void testPostNode() {
    	System.out.println("testPostNode");
    	nid = 0;
    	Map<String,String> map = new HashMap<String,String>();
    	map.put(TITLE, TITLE_TXT);
    	map.put(BODY, BODY_TXT);
    	map.put(COMMENT, "2"); 
    	map.put(TYPE, Constants.NODE_TYPE);
    	map.put("field_url_sha1sum", "ABCDFGGHGFDDDCGGGFRDSVD");
    	map.put("field_url", "http://www.netalign.com");
    	String result = http.post(host+Constants.NODE+POSTFIX, map);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	 	
    	//System.out.println(result);    	
    	
    	nid = getInt(result, Constants.NID);    
    	assertTrue(Constants.NID+" is illegal: "+Integer.toString(nid), nid >= Constants.DRUPAL_MIN_ID);   	    	
    }
    
    /**
	on POST    /=/comment                     create a comment
     */
    @Test
    public void testPostComment() {
    	System.out.println("testPostComment");
    	cid = 0;
    	Map<String,String> map = new HashMap<String,String>();
    	map.put(SUBJECT, SUBJECT_TXT);
    	map.put(Constants.COMMENT, COMMENT_TXT);
    	map.put(Constants.NID, Integer.toString(nid));    	
    	String result = http.post(host+Constants.COMMENT+POSTFIX, map);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    	 	
    	//System.out.println(result);    	
    	
    	cid = getInt(result, Constants.CID);    
    	assertTrue(Constants.CID+" is illegal: "+Integer.toString(cid), cid >= Constants.DRUPAL_MIN_ID);   	    	
    }
    
    /**
     * on GET     /=/comment/<column>            list distinct values for that column
     */
    @Test
    public void testGetCommentColumn() {
    	System.out.println("testGetCommentColumn");
    	
    	String result = http.get(host+Constants.COMMENT+SLASH+SUBJECT+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    
    	//System.out.println(result);  
    	
    	assertTrue("missing " + SUBJECT + " of last created comment", hasSubstring(result, SUBJECT_TXT));    	
    }
    
    /**
     * on GET     /=/comment/<column>/<key>      load the first comment where <column> = <key>
     */
    @Test
    public void testGetCommentColumnKey() {
    	System.out.println("testGetCommentColumnKey");
    	
    	String result = http.get(host+Constants.COMMENT+SLASH+SUBJECT+SLASH+SUBJECT_TXT+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    
    	//System.out.println(result);  
    	    	    	
    	assertTrue("bad " + Constants.CID, getInt(result, Constants.CID) == cid );
    }

    /**
     * on GET     /=/comment/<column>/<key>/<field> show the field for the first comment where <column> = <key>
     */
    @Test
    public void testGetCommentColumnKeyField() {
    	System.out.println("testGetCommentColumnKeyField");
    	
    	String result = http.get(host+Constants.COMMENT+SLASH+SUBJECT+SLASH+SUBJECT_TXT+SLASH+Constants.NID+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    
    	//System.out.println(result); 
    	
    	assertTrue( getInt(result, VALUE) == nid); 
    }

	/**
	 * on PUT     /=/comment/<column>/<key>      update the first comment where <column> = <key>
	 */
    @Test
    public void testPutCommentColumnKey() {
    	System.out.println("testPutCommentColumnKey");
    	Map<String,String> map = new HashMap<String,String>();
    	map.put(SUBJECT, SUBJECT_TXT+SUBJECT_TXT);    		    
    	
    	String result = http.put(host+Constants.COMMENT+SLASH+Constants.CID+SLASH+Integer.toString(cid)+POSTFIX, map);    	
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    
    	//System.out.println(result); 
    	
    	assertTrue( SUBJECT + " was not changed", getString(result, SUBJECT).equals(SUBJECT_TXT+SUBJECT_TXT));    	    	    	     	
    }
    
    /**
     * on DELETE  /=/comment/<column>/<key>      delete the first comment where <column> = <key>
     */
    @Test
    public void testDeleteCommentColumnKey() {
    	System.out.println("testDeleteCommentColumnKey");      	
    	String result = http.delete(host+Constants.COMMENT+SLASH+Constants.CID+SLASH+Integer.toString(cid)+POSTFIX);    	

    	//System.out.println(result);
    	
    	assertTrue("'true' was not found in server reply", hasSubstring(result, "true"));    	   	   
    }
    
    /**
     * on GET     /=/node/<column>               list distinct values for that column
     */
    @Test
    public void testGetNodeColumn() {
    	System.out.println("testGetNodeColumn");
    	
    	String result = http.get(host+Constants.NODE+SLASH+TITLE+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    
    	//System.out.println(result);  
    	
    	assertTrue("missing " + TITLE + " of last created post", hasSubstring(result, TITLE_TXT));    	
    }
    
    /**
     * on GET     /=/node/<column>/<key>         load the first node where <column> = <key>
     */
    @Test
    public void testGetNodeColumnKey() {
    	System.out.println("testGetNodeColumnKey");
    	
    	String result = http.get(host+Constants.NODE+SLASH+TITLE+SLASH+TITLE_TXT+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    
    	//System.out.println(result);  
    	    	    	
    	assertTrue("bad " + Constants.NID + ", expected "+Integer.toString(nid), getInt(result, Constants.NID) == nid );
    }
           
    /**
     * on GET     /=/node/<column>/<key>/<field> show the field for the first node where <column> = <key>
     */
    @Test
    public void testGetNodeColumnKeyField() {
    	System.out.println("testGetNodeColumnKeyField");
    	
    	String result = http.get(host+Constants.NODE+SLASH+TITLE+SLASH+TITLE_TXT+SLASH+TYPE+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    
    	//System.out.println(result); 
    	
    	assertTrue( getString(result, VALUE).equals(Constants.NODE_TYPE)); 
    }
    
    /**
     * on PUT     /=/node/<column>/<key>         update the first node where <column> = <key>
     */
    @Test
    public void testPutNodeColumnKey() {
    	System.out.println("testPutNodeColumnKey");
    	Map<String,String> map = new HashMap<String,String>();
    	map.put(TITLE, TITLE_TXT+TITLE_TXT);
    	String result = http.put(host+Constants.NODE+SLASH+Constants.NID+SLASH+Integer.toString(nid)+POSTFIX, map);    	
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    
    	//System.out.println(result); 
    	
    	result = http.get(host+Constants.NODE+SLASH+Constants.NID+SLASH+Integer.toString(nid)+SLASH+TITLE+POSTFIX);
    	assertNotNull("null reply from server", result);
    	assertFalse("empty result from server", result.isEmpty());
    
    	//System.out.println(result); 
    	
    	assertTrue( getString(result, VALUE).equals(TITLE_TXT+TITLE_TXT));     	
    }
          
    /**
	on DELETE  /=/node/<column>/<key>         delete the first node where <column> = <key>
     */
    @Test
    public void testDeleteNodeColumnKey() {
    	System.out.println("testDeleteNodeColumnKey");
    	String result = http.delete(host+Constants.NODE+SLASH+Constants.NID+SLASH+Integer.toString(13563)+POSTFIX);    	

    	//System.out.println(result);
    	
    	assertTrue("'deleted' was not found in server reply", hasSubstring(result, "deleted"));    	   	   
    }
    
    
    
    private String getString(String str, String field) {    	
    	int index = str.indexOf(field) + field.length();     	
    	assertTrue(str.charAt(index++) == '"');
    	assertTrue(str.charAt(index++) == ':');
    	assertTrue(Character.isWhitespace(str.charAt(index++)));
    	while (str.charAt(index++) == '"');
    	int begin = index-1;    	    	
    	while( Character.isJavaIdentifierPart( str.charAt(index++) ) );    	
    	index--;    	
    	
    	return str.substring(begin, index);  
    }
    
    private int getInt(String str, String field) {
    	String t = getString(str, field);
    	for (char c : t.toCharArray()) {
    		assertTrue(Character.isDigit(c));
    	}    	
    	return Integer.parseInt( t );  
    }
    
    private boolean hasSubstring(String str, String substr) {
    	return str.indexOf(substr) != -1;
	}
}
