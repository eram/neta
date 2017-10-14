/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netalign.netascutter.utils;

import java.util.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yoavram
 */
public class W3CTimeTest {
   
	private static W3CTime timeIt;
	
    public W3CTimeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception { 
    	timeIt = new W3CTime();

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

    @Test
    public void LongUnixToW3CTimeTest() {
    	long timestamp = System.currentTimeMillis();
    	String str = timeIt.unixToW3C(timestamp);
    	assertNotNull(str);
    	assertFalse(str.isEmpty());
    }
       
    @Test
    public void DateToW3CTimeTest() {
    	Date timestamp = new Date();
    	String str = timeIt.dateToW3C(timestamp);
    	assertNotNull(str);
    	assertFalse(str.isEmpty());
    }
    
    @Test
    public void StringUnixToW3CTest() {
    	String timestamp = Long.toString( System.currentTimeMillis() );
    	String str = timeIt.unixToW3C(timestamp);
    	assertNotNull(str);
    	assertFalse(str.isEmpty());
    }
    
    @Test
    public void StringUnixTimeTest() {
    	String timestamp = Long.toString( System.currentTimeMillis() );
    	String str = timeIt.unixToW3C(timestamp);
    	assertNotNull(str);
    	assertFalse(str.isEmpty());
    }
    
    @Test
    public void W3CToUnixTimeTest() {
    	String str = "2008-07-16T18:35:33Z";
    	Date date = timeIt.w3cToUnix(str);
    	assertNotNull(date);    	
    }
    
    @Test
    public void W3CToUnixAsLongTimeTest() {
    	String str = "2008-07-16T18:35:33Z";
    	long date = timeIt.w3cToSecondsLong(str);
    	assertTrue(date > 0);
    	long now = (new Date()).getTime();
    	assertTrue(date != now);
    }
    
    @Test
    public void W3CToUnixAsStringTimeTest() {
    	String str = "2008-07-16T18:35:33Z";
    	String date = timeIt.w3cToUnixAsString(str);
    	assertNotNull(date);    
    	assertFalse(date.isEmpty());
    	assertFalse(date.equals(str));
    	String now = Long.toString( (new Date()).getTime() );
    	assertFalse(date.equals(now));        	
    }
}
