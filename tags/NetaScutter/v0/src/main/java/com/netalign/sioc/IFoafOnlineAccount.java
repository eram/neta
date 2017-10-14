package com.netalign.sioc;

/**
 * Class: foaf:OnlineAccount
 * http://xmlns.com/foaf/spec/#term_OnlineAccount
Online Account - An online account.
Status: 	unstable
in-range-of:	foaf:holdsAccount
in-domain-of:	foaf:accountServiceHomepage foaf:accountName

A foaf:OnlineAccount represents the provision of some form of online service, by some party (indicated indirectly via a foaf:accountServiceHomepage) to some foaf:Agent. The foaf:holdsAccount property of the agent is used to indicate accounts that are associated with the agent.

See foaf:OnlineChatAccount for an example. Other sub-classes include foaf:OnlineEcommerceAccount and foaf:OnlineGamingAccount.

[#]

[back to top]

 * @author yoavram
 */
public interface IFoafOnlineAccount extends IRdfResource {
    /**
     * account service homepage - Indicates a homepage of the service provide for this online account. 
     * @return
     */
    IFoafDocument getAccountServiceHomepage();
    void setAccountServiceHomepage(IFoafDocument document);
    /**
     * account name - Indicates the name (identifier) associated with this online account. 
     * @return
     */
    String getAccountName();
    void setAccountName(String name);
}
