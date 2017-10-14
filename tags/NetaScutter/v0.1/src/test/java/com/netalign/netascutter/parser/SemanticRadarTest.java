package com.netalign.netascutter.parser;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netalign.netascutter.fetcher.HttpFetcher;
import com.netalign.netascutter.interfaces.*;

public class SemanticRadarTest {
	private static BufferedInputStream trueIn;
	private static BufferedInputStream falseIn;
	private static IFetcher fetcher;
	private static IParser<URL> parser;
	private static String falseUrl = "http://192.168.123.4/sioc/site";
	private static String trueUrl = "http://192.168.123.4/";

	@BeforeClass
	public static void setUpClass() throws Exception {
		System.out.println("setUpClass");
		fetcher = new HttpFetcher();
		trueIn = fetcher.fetch(trueUrl);
		falseIn = fetcher.fetch(falseUrl);
		if (trueIn == null || falseIn == null) {
			System.err.println("input streams nulled!");
			fail();
		}
		parser = new SemanticRadarParser();

	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		System.out.println("tearDownClass");
		fetcher.close(trueIn);
		fetcher.close(falseIn);
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testExtractURLs() {
		System.out.println("**testExtractURLs");
		List<URL> list = parser.parse(trueIn, trueUrl);
		assertNotNull("list of urls is null", list);
		assertFalse("list is empty", list.isEmpty());
	}

	@Test
	public void testExtractURLsSecondTimeFail() { // because the parser doesn't
													// reset the input stream
		System.out.println("**testExtractURLs2");
		List<URL> list = parser.parse(trueIn, trueUrl);
		assertNotNull("list of urls is null", list);
		assertTrue("list is not empty", list.isEmpty());
	}

	@Test
	public void testNotHTML() {
		System.out.println("**testNotHTML");
		List<URL> list = parser.parse(falseIn, falseUrl);
		assertNotNull("list of urls is null", list);
		assertTrue("list is not empty", list.isEmpty());
	}
}
