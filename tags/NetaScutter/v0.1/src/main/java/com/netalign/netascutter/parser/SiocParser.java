/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter.parser;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.*;
import com.netalign.netascutter.interfaces.*;
import com.netalign.sioc.*;

/**
 * The <code>SiocParser</code> class implements the {@link IParser} interface.<br>
 * The class parses RDF data, given in an {@link InputStream} using the {@link JenaReader} class, 
 * part of the <a href="http://jena.sourceforge.net/">Jena Semantic Web Framework</a>. The model that is created
 * by Jena is then searched for specific elements, all implementations of the {@link IRdfResource} interface:
 * <li><b>foaf:person</b> - if it is the object of a foaf:primaryTopic statement or of a foaf:maker statement - 
 * one per model</li>
 * <li><b>sioc:post</b> - if it is the object of a foaf:primaryTopic statement, or if it is the subject of
 *  a foaf:maker statement or is not the object of any sioc:has_reply statements - one per model</li>
 * <li><b>sioc:container</b> - extracts all, including sioc:forum</li>
 * <p>
 * The <code>SiocParser</code> uses an {@link RDFValidator} to validate that the input stream does indeed
 * contain RDF data.<br>
 * The code was created using the <i>foafbeans</i> project as a start code, but what changed almost completely
 * since.
 * <p>
 * @author yoavram
 * @see IParser
 * @see RDFValidator
 * @see IRdfResource
 * @see ISiocPost
 * @see IFoafPerson
 * @see ISiocContainer
 */
public class SiocParser implements IParser<IRdfResource> {
	protected static Logger logger = Logger.getLogger(SiocParser.class);
    
	public SiocParser() {		
    }
	
	/* (non-Javadoc)
	 * @see com.netalign.netascutter.interfaces.IParser#parse(java.io.InputStream)
	 */
	@Override
	public List<IRdfResource> parse(InputStream inputStream, String url) {
		List<IRdfResource> list = Collections.emptyList();
		if (inputStream == null) {
			logger.warn("Failed parsing: null input stream");
			return list;
		}	
		// validate the input stream
		if (!RDFValidator.validate(inputStream)) {
			return list;
		}
		// parse the input stream
		Model model = ModelFactory.createMemModelMaker().createDefaultModel();		
		JenaReader reader = new JenaReader();
		SiocParserErrorHandler errorHandler = new SiocParserErrorHandler(logger);
        reader.setErrorHandler(errorHandler);
        Exception caught = null;
		try {	        
	        reader.read(model, inputStream, url);
		} catch (Exception e) {
			caught = e; 
		} 
        if (!errorHandler.isOk()) { //may be set to false in fatalError() and error() 
        	if (caught != null) {
        		logger.warn("Failed parsing: " + caught);
        	} else {
        		logger.warn("Failed parsing");
        	}        	
        } else {
        	list = extractObjects(model, url);
        }
        return list;
	}
	
	private List<IRdfResource> extractObjects(Model model, String url) {
        // extract persons first, then posts and forums
		FOAFGraphImpl graph = new FOAFGraphImpl(model);
        List<IRdfResource> list = new ArrayList<IRdfResource>();
        list.add(extractPrimaryPerson(graph, url));
        list.add(extractPrimaryPost(graph, url));
        list.addAll(extractContainers(graph, url)); 
        while (list.remove(null)) {;}
        return list;
    }
       
    private IFoafPerson extractPrimaryPerson(FOAFGraphImpl graph, String url) {
        // check for foaf:person as a primary topic of the RDF model
        // this will be used to extract a post object form the rdf model    
        IFoafPerson person = graph.findPrimaryPerson(url);   // finds the primary foaf:psot and parses it to an object
        if (person == null) {
            logger.debug("No person extracted from "+ url);
        } else {
            logger.debug("Found primary person in " + url);
        }        
        return person;
    }
    
    private ISiocPost extractPrimaryPost(FOAFGraphImpl graph, String urlStr) {
    	// check for sioc:post as a primary topic of the RDF model
        // this will be used to extract a post object form the rdf model    
        ISiocPost post = graph.findPrimaryPost(urlStr);   // finds the primary sioc:post and parses it to an object        
        if (post == null) {
            logger.debug("No post extracted from "+ urlStr);
        } else {
            logger.debug("Found primary post in " + urlStr);
        }
        
        return post;
    }
    
    private List<ISiocContainer> extractContainers(FOAFGraphImpl graph, String urlStr) {
        // check for all available sioc:container elements in Model
        // this will be used to extract forum objects form the rdf model
    	// finds all sioc:container, including sioc:forum, and parses it to an object
        List<ISiocContainer> containers = graph.findAllContainers();           
        if (containers == null || containers.isEmpty()) {
            logger.debug("No container extracted from "+ urlStr);
        } else {
            logger.debug("Found " +  containers.size() + " containers in " + urlStr);
        }
        
        return containers;
    }
    
	private class SiocParserErrorHandler implements RDFErrorHandler {
		private boolean ok = true;
		private Logger logger;
		public SiocParserErrorHandler(Logger logger) {
			this.logger = logger;
		}
		
		@Override
	    public void warning(Exception e) {
	        logger.warn(e);
	    }
	  
	    @Override
	    public void error(Exception e) {
	        logger.error(e);
	        ok = false;
	    }
	    
	    @Override
	    public void fatalError(Exception e) {
	        logger.error(e);
	        ok = false;
	    }	
	    
	    public boolean isOk() {
	    	return ok;	    	
	    }
	    
	    public void setOk(boolean ok) {
	    	this.ok = ok;
	    }
	}
}
