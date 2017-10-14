package com.netalign.netascutter.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * This class converts between unix timestamp and the W3C time format used in SIOC and FOAF docs
 * via the dc:created property:
 * @see http://www.w3.org/TR/NOTE-datetime
 * @see http://web.resource.org/rss/1.0/modules/dcterms/#created
 * @see http://en.wikipedia.org/wiki/Unix_time
 * 
 * @author yoavram
 *
 */
public class W3CTime {
	private SimpleDateFormat format;
	private String pattern;
	private static Logger logger = Logger.getLogger(W3CTime.class);
	
	public static final String W3C_DATE_PATTERN  = "yyyy-MM-dd'T'HH:mm:ssZ";
		
	public W3CTime() {
		 this( W3C_DATE_PATTERN );
	}
	
	private W3CTime(String pattern) {
		this.pattern = pattern;		
		format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" );
	}
		
	/**
	 * Formats a timestamp in Unix format (seconds since 1/1/1970 00:00:00 UTC) to a W3C Date formated string.
	 * @param timestamp
	 * @return a string of the date
	 */
	public String unixToW3C(long timestamp) {
		return format.format(timestamp);
	}
	
	/**
	 * Formats a Date object to a W3C Date formatted string
	 * @param timestamp
	 * @return
	 */
	public String dateToW3C(Date timestamp) {
		return format.format(timestamp);
	}
	
	/**
	 * Formats a string of a unix timestamp (seconds since 1/1/1970 00:00:00 UTC) to a W3C Date formated string.
	 * @param str must be a numerical string.
	 * @return a string of the date, or a string of NOW if failed parsing the input string.
	 */
	public String unixToW3C(String str) {		
		Date date = new Date();
		try {
			long time = Long.parseLong(str);			
			date.setTime(time*1000); // in milisecs 
			
		} catch (NumberFormatException e) {
			logger.error("Failed parsing timestamp string '" + str + "': " + e);
			date = new Date();
		}				
		return format.format(date); 
	}
	
	/**
	 * Parses a date string
	 * @param str
	 * @return a Date object representing the date, or a Date object representing NOW if fparsing failed.
	 */
	public Date w3cToUnix(String str) {
		str = str.replace("Z", "UTC");
		try {
			return format.parse(str);
		} catch (ParseException e) {
			logger.error("Failed parsing W3C date string '" + str + "': " + e);
			return new Date();
		}
	}
	
	/**
	 * Parses a date string 
	 * @param str
	 * @return a long number representing the date in unix format , or NOW if failed parsing.
	 */
	public long w3cToSecondsLong(String str) {
		return w3cToUnix(str).getTime()/1000;
		// getTime() returns in miliseconds, we want seconds		
	}
	
	/**
	 * Parses a date string
	 * @param str
	 * @return a string of a number representing the date, or NOW if failed parsing.
	 */
	public String w3cToUnixAsString(String str) {
		return Long.toString(w3cToSecondsLong(str));
	}
}
