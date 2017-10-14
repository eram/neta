package com.netalign.netascutter.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The <code>W3CTime</code> class converts between unix timestamp and the W3C time format used in SIOC and FOAF docs
 * via the dcterms:created property.
 * <p>
 * Times without a timezone will be considered in the UTC timezone
 * <p> 
 * The class uses the {@link SimpleDateFormat} class to format strings to timestamps.
 * 
 * @author yoavram
 * @see SimpleDateFormat
 * @see <a href="http://www.w3.org/TR/NOTE-datetime">W3C Time</a>
 * @see <a href="http://web.resource.org/rss/1.0/modules/dcterms/#created">DCTerms:created</a>
 * @see <a href="http://en.wikipedia.org/wiki/Unix_time">Unix Time</a>
 *
 */
public class W3CTime {
	private List<SimpleDateFormat> formats;
	private static Logger logger = Logger.getLogger(W3CTime.class);
			
	public W3CTime() {
		formats = new ArrayList<SimpleDateFormat>(6);
		/*
   Year:
      YYYY (eg 1997)
   Year and month:
      YYYY-MM (eg 1997-07)
   Complete date:
      YYYY-MM-DD (eg 1997-07-16)
   Complete date plus hours and minutes:
      YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)
   Complete date plus hours, minutes and seconds:
      YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
   Complete date plus hours, minutes, seconds and a decimal fraction of a second
      YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00)
		*/
		formats.add(new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ"));		
		formats.add(new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss")); // this is not defined by the spec but it appears in real life data		
		formats.add(new SimpleDateFormat( "yyyy-MM-dd'T'HH:mmZ"));
		formats.add(new SimpleDateFormat( "yyyy-MM-dd"));
		formats.add(new SimpleDateFormat( "yyyy-MM"));
		formats.add(new SimpleDateFormat( "yyyy"));		
		// XXX the last format is not implemented as the SimpleDateFormat doesn't handle decimal fractions of a second
	}
		
	/**
	 * Formats a timestamp in Unix format (seconds since 1/1/1970 00:00:00 UTC) to a W3C Date formated string.
	 * @param timestamp
	 * @return a string of the date
	 */
	public String unixToW3C(long timestamp) {		
		return formats.get(0).format(timestamp);
	}
	
	/**
	 * Formats a Date object to a W3C Date formatted string
	 * @param timestamp
	 * @return
	 */
	public String dateToW3C(Date timestamp) {
		return formats.get(0).format(timestamp);
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
		
		return formats.get(0).format(date); 
	}
	
	/**
	 * Parses a date string
	 * @param str
	 * @return a Date object representing the date, or a Date object representing NOW if parsing failed.
	 */
	public Date w3cToUnix(String str) {
		str = str.replaceFirst("Z", "UTC"); // Z is shortage for UTC, but the parser down't know it!
		str = str.replaceFirst(" ", "T"); // some bad programmers write a whitespace instead of a T
		Exception exception = null;
		for (SimpleDateFormat format : formats)
		try {
			return format.parse(str);
		} catch (ParseException e) {
			exception = e;
		}
		// if got here then all formats failed
		logger.error("Failed parsing W3C date string '" + str + "': " + exception);
		return new Date(0);
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
