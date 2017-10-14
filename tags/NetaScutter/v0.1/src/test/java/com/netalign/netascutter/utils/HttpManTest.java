/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netalign.netascutter.utils;

import com.netalign.netascutter.utils.HttpMan;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * TODO tests take 1 sec - too much?
 * @author yoavram
 */
public class HttpManTest {
    private static  HttpMan httpMan;
    private static final  String HOST = "192.168.123.4";
    
    public HttpManTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception { 
        httpMan = new HttpMan();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of post method, of class HttpMan.
     */
    @Test
    public void testPost() {
        System.out.println("post");
        String urlStr = "http://"+HOST+"/=/login";
        Map<String, String> message = new HashMap<String, String>();
        message.put("name", "admin");
        message.put("pass", "admin");

        String result = httpMan.post(urlStr, message);
        System.out.println(result);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test of put method, of class HttpMan.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        System.out.println("no put test");   
    }

    /**
     * Test of get method, of class HttpMan.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        String urlStr = "http://"+HOST+"/=/login";
        
        String result = httpMan.get(urlStr);
        System.out.println(result);
        assertNotNull(result);
        assertFalse(result.isEmpty());       
    }

    /**
     * Test of head method, of class HttpMan.
     */
    @Test
    public void testHead() {
        System.out.println("head");
        String urlStr = "http://"+HOST+"/=/login";
        
        Map result = httpMan.head(urlStr);
        System.out.println(result);
        assertNotNull(result);
        assertFalse(result.isEmpty());        
    }
    
    /**
     * Test of delete method, of class HttpMan.
     */
    @Test
    public void testDelete() {
        System.out.println("delete");
        System.out.println("no delete test");      
    }
}