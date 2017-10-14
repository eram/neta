package com.netalign.netascutter.parser;

import com.netalign.netascutter.utils.ModelUtils;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.*;

/**
 * @author ldodds (PrimaryPersonSniffer) / yoavram (refactoring and changes)
 * 
 */
public class PrimaryPostSniffer {
	/**
	 * Note: will never return null if there's a post in the document because of
	 * attempts to "guess" the primary topic. Change the switch to alter this
	 * behaviour
	 * 
	 * @param model
	 * @param base
	 * @return
	 */
	public static Resource findPrimaryResource(Model model, String base,
			boolean allowGuessWork) {
		Resource primary = ModelUtils.findTopicOfPersonalProfileDocument(model,
				base);
		if (primary != null && !ModelUtils.isSiocPost(primary)) { 
			// not a sioc:post !
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
	 * has a maker or isn't the object of any sioc:has_reply relationships. This therefore
	 * assumes that all posts in a simple SIOC file will be replies to a single
	 * main post, thus will not have makers and will be the subjects of has_reply.
	 * Obviously this isn't always true.
	 * 
	 * @param model
	 * @return the sioc:Post Resource object, or null
	 */
	public static Resource guessPostTopicFromRelationships(Model model) {
		// TODO check that this captures subclasses of Post such as BoardPost
		ResIterator posts = model.listSubjectsWithProperty(RDF.type, SIOC.Post);

		Resource post = null;

		while (posts.hasNext()) {
			Resource p = posts.nextResource();
			// check if the post a has a maker in the document. if so it is the primary post
			NodeIterator maker = model.listObjectsOfProperty(p, FOAF.maker);
			if (maker.hasNext()) {
				post = p;
				break;
			}
		/* // don't do this, it gives empty posts.
			// check if the post is a reply of another post. if not, it is the primary post
			ResIterator replies = model.listSubjectsWithProperty(
					SIOC.has_reply, p);
			if (!replies.hasNext()) {
				post = p;
				break;
			}
		*/
		}
		return post;
	}
}