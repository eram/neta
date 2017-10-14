package com.netalign.netascutter;

import com.netalign.netascutter.utils.ModelUtils;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.*;

/**
 * @author ldodds
 *  
 */
public class PrimaryPersonSniffer
{
    /**
     * Note: will never return null if there's a person in the document because 
     * of attempts to "guess" the primary topic. Change the switch to alter this 
     * behaviour
     * 
     * @param model
     * @param base
     * @return
     */
    public static Resource findPrimaryResource(Model model, String base, boolean allowGuessWork)
    {
    	Resource primary = ModelUtils.findTopicOfPersonalProfileDocument(model,
                base);
    	
        if ( primary != null && ModelUtils.isFoafPerson(primary) )
        {
            return primary;
        }

        primary = ModelUtils.findMaker(model, base);
        if ( primary != null && ModelUtils.isFoafPerson(primary) )
        {
            return primary;
        }

        primary = ModelUtils.findCreator(model, base);
        if ( primary != null && ModelUtils.isFoafPerson(primary) )
        {
            return primary;
        }

        if (!allowGuessWork)
        {
            return null;
        }
        
        primary = guessPersonTopicFromRelationships(model);
        if ( primary != null && ModelUtils.isFoafPerson(primary))
        {
            return primary;
        }
        return null;
    }

    /**
     * Attempts to determine the primary person of a FOAF document, if said
     * document doesn't have a foaf:maker or dc:creator property. The algorithm
     * is far from fool-proof, but will deal with most simple FOAF docs, e.g.
     * those created by FOAF-a-Matic Mark 1.
     * 
     * The method will return the first Resource it finds in the model that
     * isn't the object of any foaf:knows relationships. This therefore assumes
     * that all people in a simple FOAF file will be known by a single main
     * person. Obviously this isn't always true.
     * 
     * @param model
     * @return the foaf:Person Resource object, or null
     */
    public static Resource guessPersonTopicFromRelationships(Model model)
    {
        ResIterator people = model.listSubjectsWithProperty(RDF.type,
                FOAF.Person);

        Resource person = null;

        while ( people.hasNext() )
        {
            Resource p = people.nextResource();

            ResIterator peopleWhoKnowMe = model.listSubjectsWithProperty(
                    FOAF.knows, p);

            if ( !peopleWhoKnowMe.hasNext() )
            {
                person = p;
                break;
            }
        }

        return person;
    }
}