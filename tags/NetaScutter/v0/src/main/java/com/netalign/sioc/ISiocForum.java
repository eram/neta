package com.netalign.sioc;

import java.util.List;

/**
 * Class: sioc:Forum
 * http://rdfs.org/sioc/spec/#term_Forum

Forum - A discussion area on which Posts or entries are made.
sub-class-of: 	sioc:Container
in-range-of:	sioc:host_of sioc:moderator_of
in-domain-of:	sioc:has_host sioc:has_moderator

Forums can be thought of as channels or discussion area on which Posts are made. A Forum can be linked to the Site that hosts it. Forums will usually discuss a certain topic or set of related topics, or they may contain discussions entirely devoted to a certain community group or organisation. A Forum will have a moderator who can veto or edit posts before or after they appear in the Forum.

Forums may have a set of subscribed Users who are notified when new Posts are made. The hierarchy of Forums can be defined in terms of parents and children, allowing the creation of structures conforming to topic categories as defined by the Site administrator. Examples of Forums include mailing lists, message boards, Usenet newsgroups and weblogs.

The SIOC Types Ontology Module defines come more specific subclasses of sioc:Forum. 
 * @author yoavram
 */
public interface ISiocForum extends ISiocContainer {
    /**
     * has_host - The Site that hosts this Forum. 
     * @return
     */
    ISiocSite getHasHost();
    void setHasHost(ISiocSite host);
    /**
     * has_moderator - A User who is a moderator of this Forum. 
     * @return
     */
    List<ISiocUser> getHasModerator();
    void setHasModerator(List<ISiocUser> users);
    void addHasModerator(ISiocUser user);
}
