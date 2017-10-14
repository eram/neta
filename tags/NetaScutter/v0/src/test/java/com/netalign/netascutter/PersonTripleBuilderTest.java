package com.netalign.netascutter;

import java.io.*;
import junit.framework.TestCase;
import com.hp.hpl.jena.rdf.model.*;
import com.netalign.rdf.vocabulary.*;

/**
 * 
 * @author ldodds
 */
public class PersonTripleBuilderTest extends TestCase {

    /**
     * Constructor for PersonTripleBuilderTest.
     * 
     * @param arg0
     */
    public PersonTripleBuilderTest(String arg0) {
        super(arg0);        
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PersonBuilderTest.class);
    }

    public void testAddPersonToModel() throws Exception {
        Person me = new Person();
        Model model = ModelFactory.createDefaultModel();
        me.setMbox("ldodds@ingenta.com");
        me.setTitle("Mr");
        me.setFirstName("Leigh");
        me.setSurname("Dodds");
        me.setHomepage("http://www.ldodds.com");
        PersonTripleBuilder builder = new PersonTripleBuilder(model);
        builder.addPerson(me);

        RDFWriter writer = model.getWriter("RDF/XML-ABBREV");

        model.getGraph().getPrefixMapping().setNsPrefix("foaf", FOAF.getURI());
        writer.write(model, new BufferedWriter(new OutputStreamWriter(
                System.out)), "");

    //TODO where's the asserts eh?!
    }
}