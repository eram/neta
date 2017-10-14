package com.netalign.sioc;

import java.util.List;

/**
 * Class: sioc:Usergroup

Usergroup - A set of User accounts whose owners have a common purpose or interest. Can be used for access control purposes.
in-range-of:	sioc:has_usergroup sioc:member_of
in-domain-of:	sioc:has_member sioc:usergroup_of

A Usergroup is a set of members or Users of a community who have a common Role, purpose or interest. While a group of Users may be a single community that is linked to a certain Forum, they may also be a set of Users who perform a certain Role, for example, moderators or administrators. 
 * @author yoavram
 */
public interface ISiocUsergroup extends IRdfResource {

    /**
     * has_member - A User who is a member of this Usergroup. 
     * @return
     */
    List<ISiocUser> getHasMemeber();
    void setHasMember(List<ISiocUser> users);
    void addHasMember(ISiocUser user);    
    /**
     * usergroup_of - A Space that the Usergroup has access to. 
     * @return
     */
    ISiocSpace getUsergroupOf();
    void setUsergroupOf(ISiocSpace space);
}
