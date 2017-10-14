package com.netalign.netascutter.interfaces;

import java.util.List;
import com.netalign.sioc.*;

/**
 * @author ldodds / yoavram
 * 
 */
public interface IFOAFGraph {
	/**
	 * Find the primary person in the IFOAFGraph.
	 * 
	 * This version of the findPrimaryPerson method is only suitable for calling
	 * on graphs that contain a single FOAF document. For more general graphs
	 * that include triples from multiple documents (e.g. as the result of a
	 * scutter), then use the alternate version of this method, specifying the
	 * baseURI of the document being queried.
	 * 
	 * @return the primary Person or null if not found
	 * @see IFOAFGraph#findPrimaryPerson(String)
	 */
	IFoafPerson findPrimaryPerson();

	ISiocPost findPrimaryPost();

	/**
	 * Finds the primary person in the IFOAFGraph.
	 * 
	 * <p>
	 * Find the primary person associated with a FOAF document from a specific
	 * URI. The algorithm first attempts to see whether the primary person is
	 * explicitly labelled (i.e. foaf:primaryTopic of a
	 * foaf:PersonalProfileDocument). Otherwise it looks for the author
	 * (foaf:maker, then dc:creator) of that document.
	 * </p>
	 * 
	 * @param base
	 *            the URI of the document
	 * @return the primary Person or null if not found
	 */
	IFoafPerson findPrimaryPerson(String base);

	ISiocPost findPrimaryPost(String base);

	/**
	 * List all people mentioned in this graph.
	 * 
	 * <p>
	 * Produces a list of Person objects, one for every foaf:Person in the
	 * graph, regardless of relationship or origin.
	 * </p>
	 * 
	 * @return a List of Person objects. May be empty.
	 */
	List<IFoafPerson> findAllPeople();

	List<ISiocPost> findAllPosts();

	List<ISiocContainer> findAllContainers();

	/**
	 * Find people by a particular FOAF property
	 * 
	 * @param propertyURI
	 *            the URI of the property
	 * @param value
	 *            the value of the property to match on
	 * @return a list of people with this property and value
	 */
	List<IFoafPerson> findPersonByProperty(String propertyURI, String value);

	List<ISiocPost> findPostByProperty(String propertyURI, String value);

	/**
	 * Find people that have a particular FOAF property
	 * 
	 * @param propertyURI
	 *            the URI of the property
	 * @return a list of people with this property
	 */
	List<IFoafPerson> findPersonWithProperty(String propertyURI);

	List<ISiocPost> findPostWithProperty(String propertyURI);

	/**
	 * Smush this graph to de-duplicate the data.
	 */
	void smush();

}