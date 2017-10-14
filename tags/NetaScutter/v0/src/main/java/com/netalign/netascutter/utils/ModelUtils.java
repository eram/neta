package com.netalign.netascutter.utils;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.FOAF;
import com.netalign.rdf.vocabulary.SIOC;

/**
 * Utility class for working with <code>foaf:Person</code> resources within a
 * Jena model.
 * 
 * @author ldodds (person) / yoavram (post)
 */
public class ModelUtils
{

    public static Selector getKnowsSelector(final Resource person)
    {
        return new Selector() {
            @Override
            public boolean test(Statement statement)
            {
                if ( statement.getSubject().equals(person)
                        && statement.getPredicate().getLocalName().equals(
                                FOAF.knows.toString()) )
                {
                    return true;
                }
                return false;
            }

            @Override
            public Resource getSubject()
            {
                return null;
            }

            @Override
            public Property getPredicate()
            {
                return null;
            }

            @Override
            public RDFNode getObject()
            {
                return null;
            }

            @Override
            public boolean isSimple()
            {
                return false;
            }
        };
    }
    
    public static Selector getHasReplySelector(final Resource post)
    {
        return new Selector() {
            @Override
            public boolean test(Statement statement)
            {
                if ( statement.getSubject().equals(post)
                        && statement.getPredicate().getLocalName().equals(
                                SIOC.has_reply.toString()) )
                {
                    return true;
                }
                return false;
            }

            @Override
            public Resource getSubject()
            {
                return null;
            }

            @Override
            public Property getPredicate()
            {
                return null;
            }

            @Override
            public RDFNode getObject()
            {
                return null;
            }

            @Override
            public boolean isSimple()
            {
                return false;
            }
        };
    }

    public static Resource findTopicOfPersonalProfileDocument(Model model, String base)
    {
        Resource ppd = model.getResource(base);

        Resource primaryTopic = null;
        if ( ppd.hasProperty(FOAF.primaryTopic) )
        {
            primaryTopic = (Resource) ppd.getProperty(FOAF.primaryTopic).getObject();
        }
        return primaryTopic;
    }

    public static Resource findMaker(Model model, String base)
    {/*
        Selector makerSelector = new SimpleSelector(model.createResource(base),
                FOAF.maker, (Object) null);

        StmtIterator iterator = model.listStatements(makerSelector);
        if ( !iterator.hasNext() )
        {
            return null;
        }
        Statement statement = (Statement) iterator.next();
        return (Resource) statement.getObject();
        */
    	return findPersonByProperty(model, FOAF.maker);
    }
    
    private static Resource findPersonByProperty(Model model, Property property) {
    	for (NodeIterator i = model.listObjectsOfProperty(property); i.hasNext() ; ) {
    		Resource r = (Resource)i.nextNode().as(Resource.class);
    		if (ModelUtils.isFoafPerson(r)) {
    			return r;
    		}
    	}
    	return null;
    }

    public static Resource findCreator(Model model, String base)
    {
    	/*
        Selector creatorSelector = new SimpleSelector(model
                .createResource(base), DC.creator, (Object) null);

        StmtIterator iterator = model.listStatements(creatorSelector);
        if ( !iterator.hasNext() )
        {
            return null;
        }
        Statement statement = (Statement) iterator.next();
        return (Resource) statement.getObject();
        */
    	Resource r = null;
    	if (( r = findPersonByProperty(model, SIOC.has_creator)) != null ) {
    		;
    	} else if (( r = findPersonByProperty(model, DC.creator)) != null ) {
    		;
    	}     	
    	return r;
    }

    public static boolean isFoafPerson(Resource resource)
    {
        return resource.hasProperty(RDF.type, FOAF.Person);
    }
    
    public static boolean isSiocPost(Resource resource)
    {
        return resource.hasProperty(RDF.type, SIOC.Post); 
    } 
    
    public static boolean isSiocUser(Resource resource)
    {
        return resource.hasProperty(RDF.type, SIOC.User); 
    }
    
    public static boolean isSiocForum(Resource resource)
    {
        return resource.hasProperty(RDF.type, SIOC.Forum); 
    } 
}