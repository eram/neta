/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.fetcher;

import java.io.*;
import java.util.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.*;
import org.apache.log4j.*;

import com.netalign.netascutter.Constants;
import com.netalign.netascutter.interfaces.*;

/**
 * @author yoavram
 * @see <a href="http://hc.apache.org/httpclient-3.x/tutorial.html">Apache Commons <code>HttpClient</code> Tutorial</a>
 * TODO write timeouts to a file for late fetching
 */
public class HttpFetcher implements IFetcher {	
	protected int numOfRetries = 5;
	private String agentName = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1";
	private int timeout = 5000;
	
	public int getNumOfRetries() {
		return numOfRetries;
	}

	public void setNumOfRetries(int numOfRetries) {
		this.numOfRetries = numOfRetries;
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
		httpClient.getParams().setParameter("http.socket.timeout", timeout);
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
		httpClient.getParams().setParameter("http.useragent", this.agentName);
	}

	protected HttpClient httpClient;
	protected Map<BufferedInputStream,HttpMethod> stream2method;
	protected static Logger logger = Logger.getLogger(HttpFetcher.class);
	
	public HttpFetcher() {			
		httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
		httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);	
		httpClient.getParams().setParameter("http.protocol.expect-continue", true);
		httpClient.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.IGNORE_COOKIES);
		httpClient.getParams().setParameter("http.useragent", agentName);
		httpClient.getParams().setParameter("http.socket.timeout", timeout);
		stream2method = new Hashtable<BufferedInputStream,HttpMethod>();
	}
	
	/* (non-Javadoc)
	 * @see com.netalign.netascutter.interfaces.IFetcher#fetch()
	 */
	@Override
	public BufferedInputStream fetch(String url) {
		// Create a method instance.
		HttpMethod method = null;
		try {			
			method = new GetMethod(url);
		}
		catch (IllegalArgumentException e) {
			logger.warn("Bad URL: " + url + ": " + e);
		}
		if (method == null) {
			logger.warn("Failed creating the GET method");
			return null;
		}
		InputStream inputStream = null;
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
				new DefaultHttpMethodRetryHandler(numOfRetries, false));			
		try {
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Fetch failed: " + method.getStatusLine() + " - " + url);
			} else {
				// Read the response body.
				inputStream = method.getResponseBodyAsStream();
			}
		} catch (HttpException e) {
			logger.error("Fatal protocol violation: " + e.getMessage() + " - " + url);
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage() + " - " + url);			
		} 		
		if (inputStream == null) {
			logger.warn("Failed fetching the URL " + url);
			method.releaseConnection();
			return null;
		}		
		BufferedInputStream resetableInputStream = new BufferedInputStream(inputStream);
		stream2method.put(resetableInputStream, method);
		logger.debug("Fetch succeeded - " + url);
		return resetableInputStream;
	}
	
	@Override
	public void close(BufferedInputStream inputStream) {
		// Release the connection.	
		try {
			if (inputStream != null) inputStream.close();
		} catch (IOException e) {
			logger.warn("Failed closing input stream: " + e);
		}
		HttpMethod method = stream2method.remove(inputStream);
		if (method != null) method.releaseConnection();		
	}	
	
	@Override
	protected void finalize() throws Throwable {
		HttpConnectionManager mgr = httpClient.getHttpConnectionManager();
		if (mgr instanceof MultiThreadedHttpConnectionManager) {
			((MultiThreadedHttpConnectionManager)mgr).shutdown();	
		}
		super.finalize();
	}	
}
