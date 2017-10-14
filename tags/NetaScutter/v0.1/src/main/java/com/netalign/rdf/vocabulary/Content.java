package com.netalign.rdf.vocabulary;

import com.hp.hpl.jena.rdf.model.*;

/**
* Vocabulary definitions from http://purl.org/rss/1.0/modules/content/
* @author yoavram
*/
public class Content {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/rss/1.0/modules/content/";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /**
     * An element whose contents are the entity-encoded or CDATA-escaped version of the content of the item.
     */
    public static final Property encoded = m_model.createProperty( "http://purl.org/rss/1.0/modules/content/encoded" );
}