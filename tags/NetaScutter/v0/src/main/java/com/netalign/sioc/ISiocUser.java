package com.netalign.sioc;

import java.util.List;

/**
 * Class: sioc:User
 * http://rdfs.org/sioc/spec/#term_User

User - A User account in an online community site.
sub-class-of: 	foaf:OnlineAccount
in-range-of:	sioc:has_administrator sioc:has_creator sioc:has_member sioc:has_moderator sioc:has_modifier sioc:has_owner sioc:has_subscriber
in-domain-of:	sioc:account_of sioc:administrator_of sioc:avatar sioc:creator_of sioc:email sioc:email_sha1 sioc:member_of sioc:moderator_of sioc:modifier_of sioc:owner_of sioc:subscriber_of

A User is an online account of a member of an online community. It is connected to Items and Posts that a User creates or edits, to Containers and Forums that it is subscribed to or moderates and to Sites that it administers. Users can be grouped for purposes of allowing access to certain Forums or enhanced community site features (weblogs, webmail, etc.).

A foaf:Person will normally hold a registered User account on a Site (through the property foaf:holdsAccount), and will use this account to create content and interact with the community. The foaf:Person can hold multiple sioc:User accounts.

sioc:User describes properties of an online account, and is used in combination with a foaf:Person (using the property sioc:account_of) which describes information about the individual itself. 
 * @author yoavram
 */
public interface ISiocUser extends IFoafOnlineAccount {
    /**
     * account_of - Refers to the foaf:Agent or foaf:Person who owns this sioc:User online account. 
     * @return
     */
    IFoafAgent getAccountOf();
    void setAccountOf(IFoafAgent agent);    
    /**
     * administrator_of - A Site that the User is an administrator of. 
     * @return
     */
    List<ISiocSite> getAdministratorOf();
    void setAdministratorOf(List<ISiocSite> sites); 
    void addAdministratorOf(ISiocSite site);
    /**
     * avatar - An image or depiction used to represent this User. 
     * @return
     */
    String getAvatar();
    void setAvatar(String avatar);
    /**
     * creator_of - A resource that the User is a creator of. 
     * @return
     */
    List<ISiocPost> getCreatorOf();
    void setCreatorOf(List<ISiocPost> items);  
    void addCreatorOf(ISiocPost items);
    /**
     * email - An electronic mail address of the User. 
     * Whereas a foaf:Person can hold multiple e-mail addresses, a sioc:User account is usually associated with a primary e-mail address represented using sioc:email or sioc:email_sha1.

        Unlike foaf:mbox / foaf:mbox_sha1sum for foaf:Person, this property is not an inverse functional property as one e-mail address can be associated with multiple unique sioc:User accounts that should not be smushed together. 
     * @return
     */
    String getEmail();
    void setEmail(String email);
    /**
     * email_sha1 - An electronic mail address of the User, encoded using SHA1. 
     * Remember to include the "mailto:" prefix, but no trailing whitespace, when computing a sioc:email_sha1 property. 
     * @return
     */
    String getEmailSha1();
    void setEmailSha1(String emailsha1);
    /**
     * member_of - A Usergroup that this User is a member of. 
     * @return
     */
    List<ISiocUsergroup> getMemberOf();
    void setMemberOf(List<ISiocUsergroup> usergroups);
    void addMemberOf(ISiocUsergroup usergroup);
    /**
     * moderator_of - A Forum that User is a moderator of. 
     * @return
     */
    List<ISiocForum> getModeratorOf();
    void setModeratorOf(List<ISiocForum> forums);
    void addModeratorOf(ISiocForum forum);
     /**
     * modifier_of - An Item that this User has modified. 
     * @return
     */
    List<ISiocPost> getModifierOf();
    void setModifierOf(List<ISiocPost> items);   
    void addModifierOf(ISiocPost items);
    /**
     * owner_of - A Container owned by a particular User, for example, a weblog or image gallery. 
     * @return
     */
    List<ISiocContainer> getOwnerOf();
    void setOwnerOf(List<ISiocContainer> containers);
    void addOwnerOf(ISiocContainer container);
     /**
     * subscriber_of - A Container that a User is subscribed to. 
     * @return
     */
    List<ISiocContainer> getSubscriberOf();
    void setSubscriberOf(List<ISiocContainer> containers);
    void addSubscriberOf(ISiocContainer container);
    
    void setSeeAlso(String seeAlso);
    String getSeeAlso();
}
