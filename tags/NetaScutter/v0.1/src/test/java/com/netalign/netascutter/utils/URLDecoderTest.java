package com.netalign.netascutter.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.junit.Test;

public class URLDecoderTest {
	public URLDecoderTest() {
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		try {
			String s = "Jürgen straße ä Mönte";
			String t1 = URLEncoder.encode(s);
			String t2 = URLEncoder.encode(s, "UTF8");
			System.out.println(t1);
			System.out.println(t2);
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
	}
}
