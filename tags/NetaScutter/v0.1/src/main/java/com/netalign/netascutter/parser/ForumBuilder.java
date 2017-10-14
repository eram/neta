package com.netalign.netascutter.parser;

import com.hp.hpl.jena.rdf.model.Resource;
import com.netalign.rdf.vocabulary.*;
import com.netalign.sioc.*;

/**
 * Populates an {@link ISiocForum} instance from the properties
 * associated with a <code>sioc:forum</code> resource.
 * The class uses {@link ContainerBuilder} to build the properties that are inherited 
 * from ISiocContainer.
 * 
 * @author yoavram
 * @see Forum
 * @see ISiocForum
 * @see ISiocContainer
 * @see ContainerBuilder
 */
public class ForumBuilder extends AbstractBuilder<ISiocForum> {
		
	public ForumBuilder(Resource resource, ISiocForum forum) {
		super(resource, forum);
	}

	@Override
	public ISiocForum build(boolean deep) {
		element = (ISiocForum)new ContainerBuilder(resource, element).build();
		if (deep) {
			//element.setHasHost(host) // not implemented as ISiocSite has no implementations		
			element.setHasModerator(getUsers(SIOC.has_moderator));
			}
				
		return element;
	}
}
