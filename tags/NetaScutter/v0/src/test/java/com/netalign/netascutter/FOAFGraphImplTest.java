/*
 * This file is in the Public Domain
 */
package com.netalign.netascutter;

import com.netalign.rdf.vocabulary.FOAF;
import com.netalign.netascutter.FOAFGraphImpl;
import com.netalign.sioc.IFoafPerson;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.netascutter.interfaces.IFOAFGraph;

/**
 * TODO -- description of FOAFGraphImplTest
 * 
 * @author ldodds
 */
public class FOAFGraphImplTest extends TestCase
{
    private Model _model;
    private static final String TEST_URI = "http://www.example.com/foaf-beans/test";
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(FOAFGraphImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        _model = ModelFactory.createDefaultModel();
        
        Resource person1 = _model.createResource(FOAF.Person);
        person1.addProperty(FOAF.name, "Person A");
        Resource mbox = _model.createResource("mailto:leigh@ldodds.com");
        person1.addProperty(FOAF.mbox, mbox );
        
        Resource person2 = _model.createResource(FOAF.Person);
        person2.addProperty(FOAF.name, "Person B");
        person2.addProperty(FOAF.mbox, mbox);
                
        Resource person3 = _model.createResource(FOAF.Person);
        person3.addProperty(FOAF.name, "Person C");
        
        person1.addProperty(FOAF.knows, person3);
        person3.addProperty(FOAF.knows, person2);
        
        Resource ppd = _model.createResource(TEST_URI);
        ppd.addProperty(RDF.type, FOAF.PersonalProfileDocument);
        ppd.addProperty(FOAF.primaryTopic, person1);
    }

    public void testSmush()
    {
        //TODO
    }

    public void testFindAllPeople()
    {
        IFOAFGraph graph = new FOAFGraphImpl(_model);
        List<IFoafPerson> people = graph.findAllPeople();

        assertTrue( people.size() == 3 );
        boolean foundA = false;
        boolean foundB = false;
        boolean foundC = false;
        
        for (Iterator<IFoafPerson> iter = people.iterator(); iter.hasNext(); )
        {
        	IFoafPerson p = iter.next();
            if ("Person A".endsWith(p.getName()) )
            {
                foundA = true;
            }
            if ("Person B".endsWith(p.getName()) )
            {
                foundB = true;
            }
            if ("Person C".endsWith(p.getName()) )
            {
                foundC = true;
            }
        }
        assertTrue( foundA && foundB && foundC );
    }

    /*
     * Class under test for Person findPrimaryPerson(String)
     */
    public void testFindPrimaryPersonString()
    {        
        IFOAFGraph graph = new FOAFGraphImpl(_model);
        IFoafPerson person = graph.findPrimaryPerson(TEST_URI);
        
        assertNotNull(person);
        assertEquals("Person A", person.getName());
        
    }

    public void testFindPersonByProperty()
    {
        IFOAFGraph graph = new FOAFGraphImpl(_model);
        List<IFoafPerson> people = graph.findPersonByProperty(FOAF.name.getURI(), "Person B");
        
        assertEquals(1, people.size());
        
        people = graph.findPersonByProperty(FOAF.mbox.getURI(), "mailto:leigh@ldodds.com");
        
        assertEquals(2, people.size());
        
    }
    
    public void testFindPersonWithProperty()
    {
        IFOAFGraph graph = new FOAFGraphImpl(_model);
        List<IFoafPerson> people = graph.findPersonWithProperty(FOAF.knows.getURI());
        
        assertEquals(2, people.size());
    }
}
