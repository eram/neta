/*
 * This file is in the Public Domain
 */
package com.netalign.netascutter;

import com.netalign.rdf.vocabulary.FOAF;
import com.netalign.netascutter.PersonBuilder;
import com.netalign.sioc.IFoafPerson;
import com.hp.hpl.jena.rdf.model.*;
import com.netalign.netascutter.Person;

import junit.framework.TestCase;

/**
 * TODO -- description of PersonBuilderTest
 * 
 * TODO -- test all properties supported by Person object
 * @author ldodds
 */
public class PersonBuilderTest extends TestCase
{
    private Model _model;
    private Resource _person;
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(PersonBuilderTest.class);
    }

    protected void setUp() throws Exception
    {
        _model = ModelFactory.createDefaultModel();
        
        _person = _model.createResource();
        _person.addProperty(FOAF.nick, "ldodds");
        _person.addProperty(FOAF.mbox, "leigh@ldodds.com");
        _person.addProperty(FOAF.family_name, "Dodds");
        _person.addProperty(FOAF.firstName, "Leigh");
        _person.addProperty(FOAF.homepage, "http://www.ldodds.com");
        _person.addProperty(FOAF.title, "Mr");
        
    }

    /**
     * Constructor for PersonBuilderTest.
     * @param arg0
     */
    public PersonBuilderTest(String arg0)
    {
        super(arg0);
    }

    public void testBuild()
    {
        PersonBuilder builder = new PersonBuilder(_person, new Person());
        IFoafPerson person = builder.build();
        
        assertNotNull(person);
        assertEquals("ldodds", person.getNick());
        assertEquals("leigh@ldodds.com", person.getMbox());
        assertNotNull(person.getMboxSha1Sum());
    }

}
