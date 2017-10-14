package com.netalign.netascutter.utils;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netalign.netascutter.DummyListener;


public class PtswReaderTest {

	@BeforeClass
    public static void setUpClass() throws Exception { 
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

    //@Test
    public void RunTest() {
    	Thread t = new PtswReaderThread(new DummyListener(), "ptsw_all_pings.xml");
    	t.run();    	
    	
    }        
}
