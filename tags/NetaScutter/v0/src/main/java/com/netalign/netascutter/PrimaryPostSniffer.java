package com.netalign.netascutter;

import com.netalign.netascutter.utils.ModelUtils;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.*;

/**
 * @author ldodds (PrimaryPersonSniffer) / yoavram (refactoring and changes)
 *  
 */
public class PrimaryPostSniffer
{
    /**
     * Note: will never return null if there's a post in the document because 
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
        if (primary != null && !ModelUtils.isSiocPost(primary)) { // not a sioc:post!
        	 primary = null;
        }

        if (primary == null && allowGuessWork) {        
            primary = guessPostTopicFromRelationships(model);
        }
        
        return primary;
    }

    /**
     * Attempts to determine the primary post of a SIOC document. The algorithm
     * is far from fool-proof, but will deal with most simple SIOC docs, e.g.
     * those created by WordPress/Drupal SIOC exporter.
     * 
     * The method will return the first Resource it finds in the model that
     * isn't the object of any sioc:has_reply relationships. This therefore assumes
     * that all posts in a simple SIOC file will be replies to a single main
     * post. Obviously this isn't always true.
     * 
     * @param model
     * @return the sioc:Post Resource object, or null
     */
    public static Resource guessPostTopicFromRelationships(Model model)
    {
        ResIterator posts = model.listSubjectsWithProperty(RDF.type,
                SIOC.Post);

        Resource post = null;

        while ( posts.hasNext() )
        {
            Resource p = posts.nextResource();

            ResIterator replies = model.listSubjectsWithProperty(
                    SIOC.has_reply, p);

            if ( !replies.hasNext() )
            {
                post = p;
                break;
            }
        }

        return post;
    }
}