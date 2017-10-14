/**
 * 
 */
package com.netalign.netascutter.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netalign.netascutter.fetcher.HttpFetcher;

/**
 * @author yoavram
 *
 */
public class RDFValidatorTest {
	private static BufferedInputStream trueIn;
	private static BufferedInputStream true2In;
	private static BufferedInputStream falseIn;
	
	@BeforeClass
    public static void setUpClass() throws Exception {
    	System.out.println("setUpClass");
    	HttpFetcher fetcher = new HttpFetcher();
    	trueIn = fetcher.fetch("http://192.168.123.4/yoavram.foaf");
    	true2In = fetcher.fetch("http://www.adamtibi.net/sioc.axd");
    	falseIn = fetcher.fetch("http://192.168.123.4/");
    	if (trueIn == null || true2In == null || falseIn == null) {
    		System.err.println("input streams nulled!");
    		fail();
    	}
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    	System.out.println("tearDownClass");    	
    }

    @Before
    public void setUp() {        
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testValidateTrue() {
        System.out.println("**testValidateTrue");
        assertTrue("validator returned false", RDFValidator.validate(trueIn) );    
    }
    
    @Test
    public void testValidateTrue2() {
        System.out.println("**testValidateTrue2");
        assertTrue("validator returned false", RDFValidator.validate(true2In) );    
    }
    
    @Test
    public void testAgainValidateTrue() {
        System.out.println("**testAgainValidateTrue");
        assertTrue("validator returned false", RDFValidator.validate(trueIn) );    
    }
    
    @Test
    public void testValidateFalse() {
        System.out.println("**testValidateFalse");
        assertFalse("validator returned true", RDFValidator.validate(falseIn) );    
    }
    
    @Test
    public void testAgainValidateFalse() {
        System.out.println("**testAgainValidateFalse");
        assertFalse("validator returned true", RDFValidator.validate(falseIn) );    
    }

}
