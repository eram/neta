/**
 * NetaScutter v.0.1
 */
package com.netalign.netascutter.utils;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.*;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import com.netalign.netascutter.Constants;
import com.netalign.netascutter.interfaces.IHttpClient;

/**
 * The <code>HttpCommons</code> class is a wrapper for
 * <code>link org.apache.commons.httpclient</code> that implements the
 * {@link IHttpClient} interface.
 * 
 * @author yoavram
 * @see <a href="http://hc.apache.org/httpclient-3.x/userguide.html">Commons-HttpClient User Guide</a>
 * @see IHttpClient
 * 
 */
public class HttpCommons implements IHttpClient {
	protected static Logger logger = Logger.getLogger(HttpCommons.class);

	protected HttpClient httpClient;
	protected int numOfRetries = 5;
	protected int timeout = 5000;
	
	public HttpCommons() {
		// TODO from properties file
		httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
		httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);		
		httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
		httpClient.getParams().setParameter("http.useragent", Constants.NETASCUTTER+Constants.VERSION);
		httpClient.getParams().setParameter("http.protocol.expect-continue", true);
		// TODO change this, login, and then don't login in php 
		httpClient.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.IGNORE_COOKIES);
		httpClient.getParams().setParameter("http.socket.timeout", timeout);
	}
	
	public void setAgentName(String name) {
		httpClient.getParams().setParameter("http.useragent", Constants.NETASCUTTER+Constants.VERSION);
	}
	
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.interfaces.IHttpClient#delete(java.net.URL)
	 */
	@Override
	public String delete(URL url) {
		return delete(url.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.interfaces.IHttpClient#delete(java.lang.String)
	 */
	@Override
	public String delete(String url) {
		logger.debug(IHttpClient.DELETE + " " + url);
		HttpMethod method = buildMethod(url, IHttpClient.DELETE);	

		return executeMethod(method);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.interfaces.IHttpClient#get(java.net.URL)
	 */
	@Override
	public String get(URL url) {
		return get(url.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.interfaces.IHttpClient#get(java.lang.String)
	 */
	@Override
	public String get(String url) {
		logger.debug(IHttpClient.GET + " " + url);
		HttpMethod method = buildMethod(url, IHttpClient.GET);

		return executeMethod(method);
	}

	/* Not Implemented
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.interfaces.IHttpClient#head(java.net.URL)
	 */
	@Override
	public Map<String, List<String>> head(URL url) {
		return head(url.toString());
	}

	/* Not Implemented
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.interfaces.IHttpClient#head(java.lang.String)
	 */
	@Override
	public Map<String, List<String>> head(String url) {
		throw new NotImplementedException("head is not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.interfaces.IHttpClient#post(java.net.URL,
	 * java.util.Map)
	 */
	@Override
	public String post(URL url, Map<String, String> message) {
		return post(url.toString(), message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.interfaces.IHttpClient#post(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public String post(String url, Map<String, String> message) {
		logger.debug(IHttpClient.POST + " " + url);
		PostMethod method = (PostMethod)buildMethod(url, IHttpClient.POST);

		if (!setBody(method, message)) {
			return Constants.EMPTY_STRING;
		}			
		method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		
		return executeMethod(method);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netalign.netascutter.interfaces.IHttpClient#put(java.net.URL,
	 * java.util.Map)
	 */
	@Override
	public String put(URL url, Map<String, String> message) {
		return put(url.toString(), message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netalign.netascutter.interfaces.IHttpClient#put(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public String put(String url, Map<String, String> message) {
		logger.debug(IHttpClient.PUT + " " + url);
		PutMethod method = (PutMethod)buildMethod(url, IHttpClient.PUT);
		
		if (!setBody(method, message)) {
			return Constants.EMPTY_STRING;
		}
		method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		
		return executeMethod(method);
	}

	/**
	 * Create a method instance.
	 * 
	 * @param url
	 * @param methodName
	 *            name of method - one of the consts in IHttpClient: GET POST
	 *            PUT DELETE HEAD
	 * @return a method or null if failed building method
	 */
	private HttpMethod buildMethod(String url, String methodName) {
		HttpMethod method = null;
		try {
			if (methodName == IHttpClient.GET)
				method = new GetMethod(url);
			else if (methodName == IHttpClient.POST)
				method = new PostMethod(url);
			else if (methodName == IHttpClient.PUT)
				method = new PutMethod(url);
			else if (methodName == IHttpClient.DELETE)
				method = new DeleteMethod(url);
			else if (methodName == IHttpClient.HEAD)
				method = new HeadMethod(url);
			else {
				logger.error("Method " + methodName + " is not supported");
			}
		} catch (IllegalArgumentException e) {
			logger.warn("Bad URL with " + methodName + ": " + url + ": " + e);
		}
		if (method == null) {
			logger.warn("Failed creating " + methodName + " method");
		} else {
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(numOfRetries, false));			
		}
		return method;
	}
	
	 
	
	@Override
	protected void finalize() throws Throwable {
		HttpConnectionManager mgr = httpClient.getHttpConnectionManager();
		if (mgr instanceof MultiThreadedHttpConnectionManager) {
			((MultiThreadedHttpConnectionManager)mgr).shutdown();	
		}
		super.finalize();
	}

	/**
	 * executes the given http method and returns the string value of the response body
	 * @param method
	 * @return
	 */
	private String executeMethod(HttpMethod method) {
		String output = Constants.EMPTY_STRING;
		try {
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);
			
			if (statusCode != HttpStatus.SC_OK) {
				// this message is in debug because only the user of the http client know if this call could have failed or not
				logger.debug(method.getName() + " failed with status "
						+ statusCode + ": " + method.getStatusLine());
			} else {
				logger.debug(method.getName() + " succeeded with status "
						+ statusCode + ": " + method.getStatusLine());
				// Read the response body.
				output = method.getResponseBodyAsString();
			}
		} catch (HttpException e) {
			logger.warn("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			logger.warn("Fatal transport error: " + e.getMessage());
		} finally {
			method.releaseConnection();
		}
		if (output == null) {
			output = Constants.EMPTY_STRING;
		}
		logger.debug("Finished " + method.getName() + " " + method.getPath());
		return output;
	}
	
	private String buildBody(Map<String,String> message) throws UnsupportedEncodingException {
		StringBuilder outBuidler = new StringBuilder();
        for (Map.Entry<String, String> entry : message.entrySet()) {
            outBuidler.append(entry.getKey());
            outBuidler.append("=");
            outBuidler.append(URLEncoder.encode(entry.getValue(), Constants.UTF8_ENCODING));
            outBuidler.append("&");
        }
        outBuidler.deleteCharAt(outBuidler.length()-1); // if the last & is not deleted then php will infer there is another key value
        return outBuidler.toString();
	}
	
	private boolean setBody(EntityEnclosingMethod method, Map<String,String> message) {		
		try {
			method.setRequestEntity(new StringRequestEntity(
					buildBody(message),"application/x-www-form-urlencoded", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.warn(e);
			return false;
		}
		return true;
	}
}
