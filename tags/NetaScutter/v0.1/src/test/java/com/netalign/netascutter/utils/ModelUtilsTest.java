package com.netalign.netascutter.utils;

import com.netalign.netascutter.utils.ModelUtils;
import junit.framework.TestCase;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.FOAF;

/**
 * @author ldodds
 */
public class ModelUtilsTest extends TestCase
{
    private Model _model;
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ModelUtilsTest.class);
    }

    public ModelUtilsTest(String arg0)
    {
        super(arg0);
    }

    public void testGetKnowsSelector()
    {
        assertNotNull(ModelUtils.getKnowsSelector(null));
    }

    public void testFindTopicOfPersonalProfileDocument()
    {
        final String testURI = "http://www.example.com/foaf-beans/test";
        
        _model = ModelFactory.createDefaultModel();
        
        Resource ppd = _model.createResource(testURI);
        ppd.addProperty(RDF.type, FOAF.PersonalProfileDocument);
        
        Resource me = _model.createResource();
        me.addProperty(RDF.type, FOAF.Person);
        
        ppd.addProperty(FOAF.primaryTopic, me);
        
        Resource foundMe = ModelUtils.findTopicOfPersonalProfileDocument(_model, testURI);
        
        assertNotNull(foundMe);
        assertEquals(me, foundMe);
        assertTrue( ModelUtils.isFoafPerson(me) );
        
        foundMe = ModelUtils.findTopicOfPersonalProfileDocument(_model, "");
        assertNull(foundMe);
    }

    public void testFindMakerWithURI()
    {
        final String testURI = "http://www.example.com/foaf-beans/test";
        
        _model = ModelFactory.createDefaultModel();
        
        Resource maker = _model.createResource();
        
        Resource r = _model.createResource(testURI);
        r.addProperty(FOAF.maker, maker );

        Resource foundMaker = ModelUtils.findMaker(_model, testURI);
        assertNotNull(foundMaker);
        assertEquals(maker, foundMaker);
                      
    }

    public void testFindMakerWithEmptyURI()
    {
        final String testURI = "";
        
        _model = ModelFactory.createDefaultModel();
        
        Resource maker = _model.createResource();

        Resource r = _model.createResource( testURI );
        r.addProperty(FOAF.maker, maker);

        Resource foundMaker = ModelUtils.findMaker(_model, testURI);
        
        assertNotNull(foundMaker);
        assertEquals(maker, foundMaker);
        
    }
    
    public void testFindCreatorWithURI()
    {
        final String testURI = "http://www.example.com/foaf-beans/test";
        
        _model = ModelFactory.createDefaultModel();
        
        Resource maker = _model.createResource();
        
        Resource r = _model.createResource(testURI);
        r.addProperty(DC.creator, maker );

        Resource foundMaker = ModelUtils.findCreator(_model, testURI);
        assertNotNull(foundMaker);
        assertEquals(maker, foundMaker);

    }

    public void testFindCreatorWithEmptyURI()
    {
        final String testURI = "";
        
        _model = ModelFactory.createDefaultModel();
        
        Resource maker = _model.createResource();
        
        Resource r = _model.createResource(testURI);
        r.addProperty(DC.creator, maker );

        Resource foundMaker = ModelUtils.findCreator(_model, testURI);
        assertNotNull(foundMaker);
        assertEquals(maker, foundMaker);        
    }
    
    public void testIsFoafPerson()
    {
        _model = ModelFactory.createDefaultModel();
        
        Resource person = _model.createResource();
        person.addProperty(RDF.type, FOAF.Person);
        
        Resource notPerson = _model.createResource();
        notPerson.addProperty(RDF.type, FOAF.Document);
        
        assertTrue( ModelUtils.isFoafPerson(person) );
        assertFalse( ModelUtils.isFoafPerson(notPerson) );
    }

}