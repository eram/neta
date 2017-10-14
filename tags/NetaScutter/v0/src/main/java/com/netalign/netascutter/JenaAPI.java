package com.netalign.netascutter;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.netalign.netascutter.utils.ModelUtils;
import com.netalign.rdf.vocabulary.*;

public class JenaAPI {

	public static Resource findPrimaryPerson(Model model, String base) {		
		Resource primary = findPrimaryTopic(model, base);
		if (primary != null && isFoafPerson(primary)) {
			return primary;
		}
		// guess primary person						
		for (StmtIterator itr = model.listStatements(null, RDF.type, FOAF.Person); itr.hasNext(); ) {			
			Statement personStmt = itr.nextStatement();			
//			// debug print			
//			System.out.println("pre:\t"+personStmt.getPredicate().toString());
//			System.out.println("obj:\t"+personStmt.getObject().toString());
//			System.out.println("sub:\t"+personStmt.getSubject().toString());
//			System.out.println();
			
			Resource personRs = model.getResource( personStmt.getSubject().toString() );
			// check if the resource found is the object of knows, maker or has_creator, if so, null it
			for (StmtIterator t = model.listStatements(null, null, personStmt.getSubject()); t.hasNext();) {
				Statement objectof = t.nextStatement();
//				// debug print	
//				System.out.println("pre:\t"+objectof.getPredicate().toString());
//				System.out.println("obj:\t"+objectof.getObject().toString());
//				System.out.println("sub:\t"+objectof.getSubject().toString());
//				System.out.println();
				
				Property predicate =  objectof.getPredicate();
				if (predicate.equals(FOAF.knows) || predicate.equals(FOAF.maker) 
						|| predicate.equals(SIOC.has_creator) ) {
					// the person is the object of knows, maker or has_creator
					personRs = null;					
				}
			}
			if (personRs != null && isFoafPerson(personRs)) {
				return personRs; 
			}							
		}		
		return null;
	}
	
	public static List<Statement> statementObjectOf(Model model, Statement stmt) {
		List<Statement> list = new ArrayList<Statement>();
		for (StmtIterator it = model.listStatements(null, null, stmt.getSubject()); it.hasNext();) {
			list.add(it.nextStatement());
		}
		
		return list;
	}
	
	public static Resource findPrimaryUser(Model model, String base) {		
		Resource primary = findPrimaryTopic(model, base);
		if (primary != null && isSiocUser(primary)) {
			return primary;
		}		
		
		return null;
	}
	
	public static Resource findPrimaryPost(Model model, String base) {		
		Resource primary = findPrimaryTopic(model, base);
		if (primary != null && isSiocPost(primary)) {
			return primary;
		}
		return null;
	}
	
    public static Resource findPrimaryTopic(Model model, String base)
    {    	
        Resource r = model.getResource(base);

        Resource primaryTopic = null;
        if ( r.hasProperty(FOAF.primaryTopic) )
        {
            primaryTopic = (Resource) r.getProperty(FOAF.primaryTopic).getObject();
        }
        return primaryTopic;
    }
	
	public static Resource findMaker(Model model, String base) {
		Resource output = null;
		for (NodeIterator i = model.listObjectsOfProperty(FOAF.maker); i.hasNext();) {
			Resource r = (Resource) i.nextNode().as(Resource.class);
			if (ModelUtils.isFoafPerson(r)) {
				output = r;
				break;
			}
		}
		return output;		
	}

	public static Resource findCreator(Model model, String base) {
		Resource output = null;
		for (NodeIterator i = model.listObjectsOfProperty(SIOC.has_creator); i.hasNext();) {
			Resource r = (Resource) i.nextNode().as(Resource.class);
			if (ModelUtils.isSiocUser(r)) {
				output = r;
				break;
			}
		}
		return output;
	}

	public static boolean isFoafPerson(Resource resource) {
		return resource.hasProperty(RDF.type, FOAF.Person);
	}

	public static boolean isSiocPost(Resource resource) {
		return resource.hasProperty(RDF.type, SIOC.Post);
	}

	public static boolean isSiocUser(Resource resource) {
		return resource.hasProperty(RDF.type, SIOC.User);
	}

	public static boolean isSiocForum(Resource resource) {
		return resource.hasProperty(RDF.type, SIOC.Forum);
	}
}
