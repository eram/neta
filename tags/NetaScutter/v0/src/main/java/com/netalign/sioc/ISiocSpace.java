package com.netalign.sioc;

import java.util.List;

/**
 * Class: sioc:Space
 http://rdfs.org/sioc/spec/#term_Space
Space - A Space is a place where data resides, e.g., on a website, desktop, fileshare, etc.
in-range-of:	sioc:has_space sioc:usergroup_of
in-domain-of:	sioc:has_usergroup sioc:space_of

A Space is defined as being a place where data resides. It can be the location for a set of Containers of content Items, e.g., on a Site, personal desktop, shared filespace, etc. Any data object that resides on a particular Space can be linked to it using the sioc:has_space property. 
 * @author yoavram
 */
public interface ISiocSpace extends IRdfResource {
    /**
     * has_usergroup - Points to a Usergroup that has certain access to this Space. 
     * @return
     */
    ISiocUsergroup getHasUsergroup();
    void setHasUsergroup(ISiocUsergroup usergroup);
    /**
     * space_of - A resource which belongs to this data Space. 
     * @return
     */
    List<Object> getSpaceOf();
    void setSpaceOf(List<Object> objects);
    void addSpaceOf(Object object);
}
