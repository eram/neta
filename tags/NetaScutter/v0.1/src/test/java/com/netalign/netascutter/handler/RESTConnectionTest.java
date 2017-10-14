/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.netalign.netascutter.handler;

import java.util.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netalign.netascutter.Constants;
import com.netalign.netascutter.handler.DrupalRESTConnection;
import com.netalign.netascutter.interfaces.*;
import com.netalign.netascutter.utils.HttpMan;

import static org.junit.Assert.*;

/**
 * TODO - tests take too much time? ~7-8 secs
 * @author yoavram
 */
public class RESTConnectionTest {
    
	private static final String EXTENDED = "X";
	private static final String UPDATED = "-updated";
	private static IRESTConnection connection;
    private static IHttpClient http; 
    private static String host;
    private static String rand; 
    private static int uid;
    private static int nid;
    private static int cid;
    
    public RESTConnectionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    	System.out.println("setUpClass");
    	host = "192.168.123.4";
    	connection = new DrupalRESTConnection(host);   
        http = new HttpMan();
        rand = "TEST-"+UUID.randomUUID().toString();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    	System.out.println("tearDownClass");
    	http.delete("http://"+host+"/=/"+Constants.USER+"/"+"name"+"/"+rand+UPDATED);
    	http.delete("http://"+host+"/=/"+Constants.NODE+"/"+"title"+"/"+rand);
    	http.delete("http://"+host+"/=/"+Constants.COMMENT+"/"+"subject"+"/"+rand);
    	http.delete("http://"+host+"/=/"+Constants.USER+"/"+"name"+"/"+rand+EXTENDED);
    }

    @Before
    public void setUp() {        
    }

    @After
    public void tearDown() {
    }

    
    /**
     * Test of getUserId method, of class RESTConnection.
     * Get user id of the admin - should be 1.
     */
    @Test
    public void testGetUserIdAdmin() {
        System.out.println("getUserIdAdmin");
        String key = "name";
        String value = "admin";       
        int result = connection.getUserId(key, value);
        assertEquals(1, result);
    }
       
     /**
     * Test of getUserId method, of class RESTConnection.
     * Get user id of the user with mail yoavram@netalign.com - should be 1.
     */
    @Test
    public void testGetUserIdByMail() {
        System.out.println("GetUserIdByMail");
        String key = "mail";
        String value = "yoavram@netalign.com";       
        int result = connection.getUserId(key, value);
        assertEquals(1, result);
    }
          
     /**
     * Test of getUserId method, of class RESTConnection.
     * test it with uid 0 - hsould fail as uid 0 belongs to annonymous and returns HTTP 403.
     */
    @Test
    public void testGetUserIdUid0() {
        System.out.println("GetUserIdUid0");
        String key = "uid";
        String value = "0";
        int result = connection.getUserId(key, value);
        assertEquals(Constants.ILLEGAL_ID, result);
    }
         
    /**
     * Test of getUserId method, of class RESTConnection.
     * test it with a bad key - should return illegal id because of 403
     */
    @Test
    public void testGetUserIdBadKey() {
        System.out.println("GetUserIdBadKey");
        String key = "foo";
        String value = "bar";
        int expResult = Constants.ILLEGAL_ID;
        int result = connection.getUserId(key, value);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetNodeIdBadKey() {
        System.out.println("GetNodeIdBadKey");
        String key = "foo";
        String value = "bar";
        int expResult = Constants.ILLEGAL_ID;
        int result = connection.getNodeId(key, value);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetCommentIdBadKey() {
        System.out.println("GetCommentIdBadKey");
        String key = "foo";
        String value = "bar";
        int expResult = Constants.ILLEGAL_ID;
        int result = connection.getCommentId(key, value);
        assertEquals(expResult, result);
    }

    /**
     * Test of createUser method, of class RESTConnection.
     */
    @Test
    public void testCreateUserNullData() {
        System.out.println("CreateUserNullData");
        Map<String, String> data = null;
        int result = connection.createUser(data);
        assertEquals(Constants.ILLEGAL_ID, result);
    }         
    
    @Test
    public void testCreateNodeNullData() {
        System.out.println("CreateNodeNullData");
        Map<String, String> data = null;
        int result = connection.createNode(data);
        assertEquals(Constants.ILLEGAL_ID, result);
    }
    
    @Test
    public void testCreateCommentNullData() {
        System.out.println("CreateCommentNullData");
        Map<String, String> data = null;
        int result = connection.createComment(data);
        assertEquals(Constants.ILLEGAL_ID, result);
    }
    
    /**
     * Test of createUser method, of class RESTConnection.
     */
    @Test
    public void testCreateUserEmptyData() {
        System.out.println("CreateUserEmptyData");
        Map<String, String> data = new HashMap<String, String>();
        int result = connection.createUser(data);
        assertEquals(Constants.ILLEGAL_ID, result);
    }
    
    @Test
    public void testCreateNodeEmptyData() {
        System.out.println("CreateNodeEmptyData");
        Map<String, String> data = new HashMap<String, String>();
        int result = connection.createNode(data);
        assertEquals(Constants.ILLEGAL_ID, result);
    }
    
    @Test
    public void testCreateCommentEmptyData() {
        System.out.println("CreateCommentEmptyData");
        Map<String, String> data = new HashMap<String, String>();
        int result = connection.createComment(data);
        assertEquals(Constants.ILLEGAL_ID, result);
    }
           
	 /**
	 * Test of createUser method, of class RESTConnection.
	 */
    @Test
    public void testCreateUserBasicData() {
        System.out.println("CreateUserBasicData");
        String name = rand;
        Map<String, String> userData = new HashMap<String, String>();
        userData.put("name", name); 
        userData.put("pass", "password"); 
        userData.put("mail", name+"@local");
        userData.put("init", "NetaScutterTest");
        int result = connection.createUser(userData);
        assertNotSame(Constants.ILLEGAL_ID, result);
        uid = result;
    }
                   
    /**
     * Test of createUser method, of class RESTConnection.
     */
    @Test
    public void testCreateUserExtendedData() {
        System.out.println("CreateUserExtendedData");
        String name = rand+EXTENDED;
                Map<String, String> userData = new HashMap<String, String>();
        userData.put("name", name); 
        userData.put("pass", "password"); 
        userData.put("mail", name+"@local");
        userData.put("init", "NetaScutterTest");
        userData.put("dc_created", "1970-07-07T19:00:00");
        userData.put("foaf_homepage", "http://www.netalign.com/test?name=test&pass=pass");        
        int unexpResult = Constants.ILLEGAL_ID;
        int result = connection.createUser(userData);
        assertNotSame(unexpResult, result);
        
        
    }
    
    @Test
    public void testCreateNodeBasicData() {
        System.out.println("CreateNodeBasicData");
        String title = rand;
        Map<String, String> data = new HashMap<String, String>();
        data.put("title", title); 
        data.put("body", title+"\n"+title);
        data.put("type", "post");
        data.put("comment", "2");
        data.put("uid", "0");     
        int result = connection.createNode(data);
        assertNotSame(Constants.ILLEGAL_ID, result);   
        nid = result;
    }
    
    @Test
    public void testGetNodeIdTestNode() {
        System.out.println("getNodeIdTestNode");
        String key = "title";
        String value = rand;       
        int result = connection.getNodeId(key, value);
        assertEquals(nid, result);      
    }
    
    @Test
    public void testCreateCommentBasicData() {
        System.out.println("CreateCommentBasicData");
        String subject = rand;
        Map<String, String> data = new HashMap<String, String>();
        data.put("subject", subject); 
        data.put("comment", subject+"\n"+subject);
        data.put("nid", Integer.toString(nid));
        data.put("uid", "0");   
        int result = connection.createComment(data);
        assertNotSame(Constants.ILLEGAL_ID, result);      
        cid = result;
    }
    
    @Test
    public void testGetCommentIdTestNode() {
        System.out.println("GetCommentIdTestNode");
        String key = "subject";
        String value = rand;       
        int result = connection.getCommentId(key, value);
        assertEquals(cid, result);      
    }
        
    @Test
    public void testUpdateUser() {
    	System.out.println("testUpdateUser");    	
    	
    	Map<String, String> map = new HashMap<String, String>();
        String expName = rand+UPDATED;
        map.put("name", expName);    // new mail!
    	
        int newuid = connection.updateUser(map, Constants.UID, Integer.toString(uid));
        assertEquals(uid, newuid);
    	  	
    	String name = connection.getUserFieldByKeyValue(Constants.UID, Integer.toString(uid), "name");
    	assertEquals(expName, name);    	
    }
    
    @Test
    public void testUpdateNode() {
    	System.out.println("testUpdateNode");
    	int nid = connection.getNodeId("title", rand);
    	assertNotSame("can't get nid for comment to update", Constants.ILLEGAL_ID, nid);
    	
    	Map<String, String> map = new HashMap<String, String>();
    	String expectedContent = rand+"\n"+rand+"\n"+rand;
        map.put("body", expectedContent);
        String expectedUid = "1";
        map.put("uid", expectedUid);    // new uid!
    	
        int newnid = connection.updateNode(map, Constants.NID, Integer.toString(nid));
        assertEquals(nid, newnid);
    	  	
    	String uidStr = connection.getNodeFieldByKeyValue(Constants.NID, Integer.toString(newnid), Constants.UID);
    	assertEquals(expectedUid, uidStr);
    	
    }
    
    @Test
    public void testUpdateComment() {
    	System.out.println("UpdateComment");
    	
    	Map<String, String> map = new HashMap<String, String>();
    	String expectedContent = rand+"\n"+rand+"\n"+rand;
        map.put("comment", expectedContent);        
        String expectedUid = "1";
        map.put("uid", expectedUid);    // new uid!
    	
        int newcid = connection.updateComment(map, Constants.CID, Integer.toString(cid));
        assertEquals(cid, newcid);
    	  	
    	String uidStr = connection.getCommentFieldByKeyValue(Constants.CID, Integer.toString(cid), Constants.UID);
    	assertEquals(expectedUid, uidStr);    	
    }

     /**
     * Test of checkBuddies method, of class RESTConnection.
     * test it with bad values
     */
    @Test
    public void testCheckBuddiesFalse() {
        System.out.println("checkBuddies");
        int uid = 1 ;
        int buddyUid = 1;
        boolean result = connection.checkBuddies(uid, buddyUid);
        assertFalse(result);
    }
    
    /**
     * Test of addBuddy method, of class RESTConnection.
     * test it with good values
     */
    @Test
    public void testAddBuddyAndCheckBuddiesTrue() {
        System.out.println("addBuddy");
        int buddyUid = 1;
        connection.addBuddy(uid, buddyUid);
        boolean result = connection.checkBuddies(uid, buddyUid);
        
        assertTrue("checkBuddies didn't return true" ,result);
    }  
    
    /**
     * Test of removeBuddy method, of class RESTConnection.
     * test it with good values
     */
    @Test
    public void testRemoveBuddy() {
        System.out.println("removeBuddy");
        int buddyUid = 1;
        boolean result = connection.removeBuddy(uid, buddyUid);
        assertTrue(result);
    }
    
    

}