package com.netalign.netascutter;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.netalign.rdf.vocabulary.*;
import com.netalign.sioc.*;

/**
 * Populates an {@link ISiocForum} instance from the properties
 * associated with a <code>sioc:forum</code> resource.
 * 
 * @author yoavram
 */
public class ForumBuilder extends AbstractBuilder<ISiocForum> {
		
	public ForumBuilder(Resource resource, ISiocForum forum) {
		super(resource, forum);
	}

	@Override
	public ISiocForum build() {
		element.setContainerOf(getPosts(SIOC.container_of));
		//element.setHasHost(host) // not implemented as ISiocSite has no implementations
		element.setHasModerator(getUsers(SIOC.has_moderator));
		element.setHasOwner(getUser(SIOC.has_owner));
		//element.setHasParent(parent); // not implemented 
		element.setHasSubscriber(getUsers(SIOC.has_subscriber));
		//element.setParentOf(children); // not implemented
		
		if (resource.hasProperty(RDFS.seeAlso)) {
            element.setSeeAlso(resource.getProperty(RDFS.seeAlso).getObject().toString());
        }
		
		return element;
	}
}
