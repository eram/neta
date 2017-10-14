/*
 * This file is in the Public Domain
 */
package com.netalign.netascutter.parser;

import com.netalign.netascutter.parser.PrimaryPostSniffer;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.FOAF;
import com.netalign.rdf.vocabulary.SIOC;

import junit.framework.TestCase;

/**
 * 
 * @author yoavram
 */
public class PrimaryPostSnifferTest extends TestCase
{
    private Model model;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(PrimaryPostSnifferTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        model = ModelFactory.createDefaultModel();                
    }

    
    
    public void testFindPrimaryResourceWithOnlyPersonalProfileDocument()
    {
        final String testURI = "http://www.netalign.com/netascutter/test";
        
        Resource r = model.createResource();
        r.addProperty(RDF.type, SIOC.Post);
        
        Resource doc = model.createResource(testURI);
        doc.addProperty(RDF.type, FOAF.PersonalProfileDocument);        
        doc.addProperty(FOAF.primaryTopic, r);
        
        Resource primary = PrimaryPostSniffer.findPrimaryResource(model, testURI, false);
        
        assertNotNull(primary);
        assertEquals(r, primary);
    }
    
    public void testFindPrimaryResourceGuesswork() {
    	final String testURI = "http://www.netalign.com/netascutter/test";
        
        Resource r = model.createResource();
        r.addProperty(RDF.type, SIOC.Post);
        
        Resource primary = PrimaryPostSniffer.findPrimaryResource(model, testURI, true);
        
        assertNotNull(primary);
        assertEquals(r, primary);
    }
    
    public void testFindPrimaryResourceNull() {
    	Resource primary = PrimaryPostSniffer.findPrimaryResource(model, "", true);
        assertNull(primary);        
    }

   

}
