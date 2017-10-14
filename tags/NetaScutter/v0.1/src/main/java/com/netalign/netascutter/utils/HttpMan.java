package com.netalign.netascutter.utils;

import java.net.*;
import java.io.*;
import java.util.*;
import com.netalign.netascutter.interfaces.IHttpClient;
import com.netalign.netascutter.Constants;
import org.apache.log4j.*;

/**
 * TODO change with an external client with cookies support
	and continous sessions to improve speed and to allow login -
	replace with the open source ASSAF found (apache.something)
 * Class allows for HTTP basic methods - GET POST PUT DELETE
 * All methods return an empty string uppon failures, 
 * no exceptions are thrown - failures are logged.
 * @author yoavram
 */
public class HttpMan implements IHttpClient {

    public static final String BAD_URL_MESSAGE = "Bad URL: ";
    public static final String FILE_NOT_FOUND_MESSAGE = "File not found: ";
    public static final String NEWLINE = "\n";
    protected static Logger logger = Logger.getLogger(HttpMan.class);

    public HttpMan() {
    }

    @Override
    public String put(URL urlStr, Map<String, String> message) {
        return send(urlStr, message, PUT);
    }

    @Override
    public String put(String urlStr, Map<String, String> message) {
        try {
            return put(new URL(urlStr), message);
        } catch (MalformedURLException e) {
            logger.warn(BAD_URL_MESSAGE + e.getMessage());
        }
        return Constants.EMPTY_STRING;
    }

    @Override
    public String post(URL urlStr, Map<String, String> message) {
        return send(urlStr, message, POST);
    }

    @Override
    public String post(String urlStr, Map<String, String> message) {
        try {
            return post(new URL(urlStr), message);
        } catch (MalformedURLException e) {
            logger.warn(BAD_URL_MESSAGE + e.getMessage());
        }
        return Constants.EMPTY_STRING;
    }

    @Override
    public String get(URL urlStr) {
        return recieve(urlStr, GET);
    }

    @Override
    public String get(String urlStr) {
        try {
            return get(new URL(urlStr));
        } catch (MalformedURLException e) {
            logger.warn(BAD_URL_MESSAGE + e.getMessage());
        }
        return Constants.EMPTY_STRING;
    }
    
    @Override
    public Map<String,List<String>> head(URL urlStr) {
    	Map<String,List<String>> headmap = Collections.emptyMap();
        try {
            HttpURLConnection connection = openConnection(urlStr);
            connection.setRequestMethod(HEAD);
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            headmap = connection.getHeaderFields();                       	            	                        
        } catch (Exception e) {
            logger.warn(e);
        }
        return headmap;
    }

    @Override
    public Map<String,List<String>> head(String urlStr) {
        try {
            return head(new URL(urlStr));
        } catch (MalformedURLException e) {
            logger.warn(BAD_URL_MESSAGE + e.getMessage());
        }
        return Collections.emptyMap();
    }

    @Override
    public String delete(URL urlStr) {
        return recieve(urlStr, DELETE);
    }

    @Override
    public String delete(String urlStr) {
        try {
            return delete(new URL(urlStr));
        } catch (MalformedURLException e) {
            logger.warn(BAD_URL_MESSAGE + e.getMessage());
        }
        return Constants.EMPTY_STRING;
    }

    private String send(URL urlStr, Map<String, String> message, String method) {
        String output = Constants.EMPTY_STRING;
        try {
            HttpURLConnection connection = openConnection(urlStr);
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);             
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
            		connection.getOutputStream(), Constants.UTF8_ENCODING));
            
            StringBuilder outBuidler = new StringBuilder();
            for (Map.Entry<String, String> entry : message.entrySet()) {
                outBuidler.append(entry.getKey());
                outBuidler.append("=");
                outBuidler.append(URLEncoder.encode(entry.getValue(), Constants.UTF8_ENCODING));
                outBuidler.append("&");
            }
            outBuidler.deleteCharAt(outBuidler.length()-1); // if the last & is not deleted then php will infer there is another key value
            // send the message            
            out.print(outBuidler.toString());
            out.close();

            output = readConnection(connection);

        } catch (Exception e) {
            logger.warn(e);
        }
        return output;
    }

    private String recieve(URL urlStr, String method) {
        String output = Constants.EMPTY_STRING;
        try {
            HttpURLConnection connection = openConnection(urlStr);
            connection.setRequestMethod(method);
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            output = readConnection(connection);
        } catch (Exception e) {
            logger.warn(e);
        }
        return output;
    }

    private HttpURLConnection openConnection(URL url) throws MalformedURLException, IOException {
        return (HttpURLConnection) url.openConnection();
    }

    private String readConnection(HttpURLConnection connection) { 	
    	StringBuilder inBuidler = new StringBuilder();
        try {       	
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), Constants.UTF8_ENCODING));        	
            String line;
            while ((line = in.readLine()) != null) {
                inBuidler.append(line);
                inBuidler.append(NEWLINE);
            }
            in.close();
        } catch (FileNotFoundException e) {
            logger.debug(FILE_NOT_FOUND_MESSAGE + e.getMessage());
        } catch (Exception e) {
            logger.warn(e);
        }
        inBuidler.trimToSize();        
        return inBuidler.toString();
    }
}
