package com.netalign.sioc;

import java.util.List;

/**
 * Class: sioc:Container
 * http://rdfs.org/sioc/spec/#term_Container

Container - An area in which content Items are contained.
in-range-of:	sioc:has_container sioc:has_parent sioc:owner_of sioc:parent_of sioc:subscriber_of
in-domain-of:	sioc:container_of sioc:has_owner sioc:has_parent sioc:has_subscriber sioc:parent_of

Container is a high-level concept used to group content Items together. The relationships between a Container and the Items that belong to it are described using sioc:container_of and sioc:has_container properties. A hierarchy of Containers can be defined in terms of parents and children using sioc:has_parent and sioc:parent_of.

Subclasses of Container can be used to further specify typed groupings of Items in online communities. Forum, a subclass of Container and one of the core classes in SIOC, is used to describe an area on a community Site (e.g., a forum or weblog) on which Posts are made. The SIOC Types Ontology Module contains additional, more specific subclasses of sioc:Container. 
 * @author yoavram
 */
public interface ISiocContainer extends IRdfResource {
    /**
     * container_of - An Item that this Container contains. 
     * @return
     */
    List<ISiocPost> getContainerOf();
    void setContainerOf(List<ISiocPost> posts);
    void addContainerOf(ISiocPost post);
    /**
     * has_owner - A User that this Container is owned by. 
     * @return
     */
    ISiocUser getHasOwner();
    void setHasOwner(ISiocUser user);
    /**
     * has_parent - A Container or Forum that this Container or Forum is a child of. 
     * @return
     */
    ISiocContainer getHasParent();
    void setHasParent(ISiocContainer parent);
    /**
     * has_subscriber - A User who is subscribed to this Container. 
     * @return
     */
    List<ISiocUser> getHasSubscriber();
    void setHasSubscriber(List<ISiocUser> users);
    void addSubscriber(ISiocUser user);
    /**
     * parent_of - A child Container or Forum that this Container or Forum is a parent of. 
     * @return
     */
    List<ISiocContainer> getParentOf();
    void setParentOf(List<ISiocContainer> children);
    void addParentOf(ISiocContainer child);
    
    void setSeeAlso(String seeAlso);
    String getSeeAlso();
	void setDescription(String description);
	void setTitle(String title);
	String getDescription();
	String getTitle();
    
}
