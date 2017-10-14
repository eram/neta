package com.netalign.netascutter.interfaces;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * The <code>IHttpClient</code> interface allows the user to preform HTTP requests on a remote HTTP server, 
 * including the HTTP methods GET, POST, HEAD, PUT and DELETE.
 * <p>
 * URLs are given either as {@link String} or {@link URL}.<br>
 * Data is given to the interface as a {@link Map} of keys and values to send as "[key]=[value]"
 * <p>
 * Implementations of this interface do not throw exceptions and return empty strings on failures, 
 * thus any implementation must log all errors.
 *  
 * @author yoavram
 * @see <a href="http://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol">HTTP on Wikipedia</a>
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616.html">HTTP spec</a>
 * 
 */
public interface IHttpClient {

    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    
    String put(URL url, Map<String, String> message);

    String put(String url, Map<String, String> message);

    String post(URL url, Map<String, String> message);

    String post(String url, Map<String, String> message);

    String get(URL url);

    String get(String url);
    
    Map<String, List<String>> head(URL url);

    Map<String, List<String>> head(String url);

    String delete(URL url);

    String delete(String url);
}
