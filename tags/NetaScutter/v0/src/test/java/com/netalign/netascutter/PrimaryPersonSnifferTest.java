/*
 * This file is in the Public Domain
 */
package com.netalign.netascutter;

import com.netalign.netascutter.PrimaryPersonSniffer;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.FOAF;

import junit.framework.TestCase;

/**
 * TODO -- description of PrimaryPersonSnifferTest
 * 
 * @author ldodds
 */
public class PrimaryPersonSnifferTest extends TestCase
{
    private Model _model;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(PrimaryPersonSnifferTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        _model = ModelFactory.createDefaultModel();                
    }

    public void testFindPrimaryResourceWithOnlyMaker()
    {
        final String testURI = "http://www.example.com/foaf-beans/test";
        
        Resource r = _model.createResource();
        r.addProperty(RDF.type, FOAF.Person);
        
        Resource doc = _model.createResource(testURI);
        doc.addProperty(FOAF.maker, r);
        
        Resource primary = PrimaryPersonSniffer.findPrimaryResource(_model, testURI, false);
        
        assertNotNull(primary);
        assertEquals(r, primary);        
    }

    public void testFindPrimaryResourceWithOnlyCreator()
    {
        final String testURI = "http://www.example.com/foaf-beans/test";
        
        Resource r = _model.createResource();
        r.addProperty(RDF.type, FOAF.Person);
        
        Resource doc = _model.createResource(testURI);
        doc.addProperty(DC.creator, r);
        
        Resource primary = PrimaryPersonSniffer.findPrimaryResource(_model, testURI, false);
        
        assertNotNull(primary);
        assertEquals(r, primary);
        
        primary = PrimaryPersonSniffer.findPrimaryResource(_model, "", false);
        assertNull(primary);        
    }
    
    public void testFindPrimaryResourceWithMakerAndCreator()
    {
        final String testURI = "http://www.example.com/foaf-beans/test";
        
        Resource r = _model.createResource();
        r.addProperty(RDF.type, FOAF.Person);
        
        Resource r2 = _model.createResource();
        r.addProperty(RDF.type, FOAF.Person);
        
        Resource doc = _model.createResource(testURI);
        doc.addProperty(FOAF.maker, r);
        doc.addProperty(DC.creator, r2);
        
        Resource primary = PrimaryPersonSniffer.findPrimaryResource(_model, testURI, false);
        
        assertNotNull(primary);
        assertEquals(r, primary);        
    }
    
    public void testFindPrimaryResourceWithOnlyPersonalProfileDocument()
    {
        final String testURI = "http://www.example.com/foaf-beans/test";
        
        Resource r = _model.createResource();
        r.addProperty(RDF.type, FOAF.Person);
        
        Resource doc = _model.createResource(testURI);
        doc.addProperty(RDF.type, FOAF.PersonalProfileDocument);        
        doc.addProperty(FOAF.primaryTopic, r);
        
        Resource primary = PrimaryPersonSniffer.findPrimaryResource(_model, testURI, false);
        
        assertNotNull(primary);
        assertEquals(r, primary);
        
        primary = PrimaryPersonSniffer.findPrimaryResource(_model, "", false);
        assertNull(primary);        
        
    }

    public void testFindPrimaryResourceWithPersonalProfileDocumentAndMaker()
    {
        final String testURI = "http://www.example.com/foaf-beans/test";
        
        Resource r = _model.createResource();
        r.addProperty(RDF.type, FOAF.Person);

        Resource r2 = _model.createResource();
        r2.addProperty(RDF.type, FOAF.Person);
        
        Resource doc = _model.createResource(testURI);
        doc.addProperty(RDF.type, FOAF.PersonalProfileDocument);        
        doc.addProperty(FOAF.primaryTopic, r);
        //shouldn't occur, but lets test we return the topic in preference 
        doc.addProperty(FOAF.maker, r2);
        
        Resource primary = PrimaryPersonSniffer.findPrimaryResource(_model, testURI, false);
        
        assertNotNull(primary);
        assertEquals(r, primary);
        
        primary = PrimaryPersonSniffer.findPrimaryResource(_model, "", false);
        assertNull(primary);        
        
    }
    
    public void testFindPrimaryResourceWithMixedModel()
    {
        final String testURI = "http://www.example.com/foaf-beans/test";
        
        Resource r = _model.createResource();
        r.addProperty(RDF.type, FOAF.Person);
        
        //first doc, has uri
        Resource doc = _model.createResource(testURI);
        doc.addProperty(DC.creator, r);

        Resource r2 = _model.createResource();
        r2.addProperty(RDF.type, FOAF.Person);
        
        //second doc, anonymous
        Resource doc2 = _model.createResource("");
        doc2.addProperty(DC.creator, r2);
        
        Resource primary = PrimaryPersonSniffer.findPrimaryResource(_model, testURI, false);
        
        assertNotNull(primary);
        assertEquals(r, primary);
        
        primary = PrimaryPersonSniffer.findPrimaryResource(_model, "", false);
        assertNotNull(primary);       
        assertEquals(r2, primary);
    }
    
    public void testGuessPersonTopicFromRelationships()
    {
        //TODO -- test guess person topic
    }

}
