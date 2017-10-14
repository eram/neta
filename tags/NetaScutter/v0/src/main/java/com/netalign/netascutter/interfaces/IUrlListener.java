package com.netalign.netascutter.interfaces;

import com.hp.hpl.jena.rdf.model.Model;

import java.net.URL;

/**
 *	The <code>IUrlListener</code> interface receives URLs.
 *  The user of this interface may send it URLs as a {@link String} or as a {@link URL},
 *  it may send it a JENA {@link Model} in order for the listener to retrieve all "seeAlso" links from the model,
 *  and it may ask for the current number of URLs. 
 *
 * @author yoavram
 * @see com.netalign.netascutter.Scutter
 * @see com.netalign.netascutter.DummyListener
 */
public interface IUrlListener {

	/**
	 * Adds all "rdfs:seeAlso" links from the JENA {@link Model} to the listener.
	 * @param incoming a JENA {@link Model} such as a SIOC:Post RDF file
	 * @return returns true if the URL was added or false otherwise
	 */
    boolean addSeeAlsos(Model incoming);    
    /**
     * Add a URL to the listener. The listener will check the URL prior to addition.
     * @param url a {@link URL} object
     * @return returns true if the URL was added or false otherwise
     */
    boolean addURL(URL url);
    /**
     * Add a URL to the listener. The listener will check the URL prior to addition.
     * @param url a {@link String} of a URL
     * @return returns true if the URL was added or false otherwise
     */
    boolean addURL(String url);
    /**
     * Add a URL to the listener. The listener will NOT check the URL prior to addition.
     * @param url a {@link URL} object
     * @return returns true if the URL was added or false otherwise
     */
    boolean addURLUnchecked(URL url);
    /**
     * Add a URL to the listener. The listener will NOT check the URL prior to addition.
     * @param url a {@link String} of a URL
     * @return returns true if the URL was added or false otherwise
     */
    boolean addURLUnchecked(String url);
	/**
	 * The listener will return the number of URLs in it's queue.
	 * @return Number of URLs in the listener's URL queue.
	 */
	int getNumOfUrls();

}
