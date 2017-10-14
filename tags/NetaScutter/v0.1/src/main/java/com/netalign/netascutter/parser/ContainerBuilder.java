/**
 * 
 */
package com.netalign.netascutter.parser;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.netalign.rdf.vocabulary.SIOC;
import com.netalign.sioc.*;

/**
 * Populates an {@link ISiocContainer} instance from the properties
 * associated with a <code>sioc:container</code> resource.
 * 
 * @author yoavram
 * @see ISiocContainer
 * @see Container
 */
public class ContainerBuilder extends AbstractBuilder<ISiocContainer> {

	public ContainerBuilder(Resource resource, ISiocContainer forum) {
		super(resource, forum);
	}

	@Override
	public ISiocContainer build(boolean deep) {
        element.setTitle(getProperty(DC.title));
        element.setDescription(getProperty(DC.description));
		if (deep) {
			element.setContainerOf(getPosts(SIOC.container_of));
			element.setHasOwner(getUser(SIOC.has_owner));
			element.setHasParent(getContainer(SIOC.has_parent)); 
			element.setHasSubscriber(getUsers(SIOC.has_subscriber));
			element.setParentOf(getContainers(SIOC.parent_of));
			}
		if (resource.hasProperty(RDFS.seeAlso)) {
            element.setSeeAlso(resource.getProperty(RDFS.seeAlso).getObject().toString());
        }
		
		return element;
	}
}
