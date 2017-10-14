/*
 * NetaScutter v.0.1
 */
package com.netalign.netascutter.utils;

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
public class HttpCommonsTest {
    private static  HttpCommons httpCommons;
    private static final  String HOST = "192.168.123.4";
    
    @BeforeClass
    public static void setUpClass() throws Exception { 
        httpCommons = new HttpCommons();
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

        String result = httpCommons.post(urlStr, message);
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
        
        String result = httpCommons.get(urlStr);
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