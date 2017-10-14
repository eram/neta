package com.netalign.netascutter.interfaces;

import java.util.Map;

import com.hp.hpl.jena.rdf.model.Resource;
import com.netalign.sioc.IFoafPerson;
import com.netalign.sioc.ISiocPost;

/**
 * The <code>IConverter</code> interface allows a user to convert several SIOC interfaces to a Map of 
 * key-value entries where keys hold name of class fields and values hold values of class fields.
 * <p>
 * The converter may be used, as in the case of the implementation {@link DrupalRESTMapper} to convert
 * between the SIOC types to a Map that can be send on an HTTP POST or PUT request.
 * 
 * @author yoavram
 * @see DrupalRESTMapper
 * @see IFoafPerson
 * @see ISiocPost
 *
 */
public interface IConverter {

	/**
	 * Convert an {@link IFoafPerson} object to a map of strings.
	 * <br>Conversion is basically extraction of all the person fields to key-value entries, and
	 * addition of some other fields, depending on implementation.           
	 * @param person    an {@link IFoafPerson} to convert
	 * @param urlStr    the URL of the RDF file this person was extracted from
	 * @return          a map of strings with key-value entries of the person fields and values
	 */
	Map<String, String> convertPerson(IFoafPerson person, String urlStr);
	/**
	 * Convert an {@link ISiocPost} object to a map of strings.
	 * This post is considered and actual post, unlike the <i>convertComment</i> method.
	 * Conversion is basically extraction of all the post fields to key-value entries, and
	 * addition of some other fields, depending on implementation.             
	 * @param person    an {@link ISiocPost} to convert
	 * @param urlStr    the URL of the RDF file this post was extracted from
	 * @return          a map of strings with key-value entries of the post fields and values
	 */
	Map<String, String> convertPost(ISiocPost post, String urlStr);
	/**
	 * Convert an {@link ISiocPost} object to a map of strings.
	 * This post is considered to be a <i>comment</i>, or a reply of another post, unlike
	 * the <i>conertPost</i> method that treats the post as an actual post.
	 * Conversion is basically extraction of all the post fields to key-value entries, and
	 * addition of some other fields, depending on implementation.             
	 * @param person    an {@link ISiocPost} to convert
	 * @param urlStr    the URL of the RDF file this post was extracted from
	 * @return          a map of strings with key-value entries of the post fields and values
	 */
	Map<String, String> convertComment(ISiocPost post, String urlStr);
	/**
	 * Returns the key that is given to a specific {@link Resource}.
	 * <br>If left without implementation should return an empty String and NOT null.
	 * @param res a JENA Model {@link Resource}
	 * @return a String with the name of the key this Resource will get.
	 */
	String getFieldName(Resource res);
}