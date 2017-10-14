package com.netalign.sioc;

import java.util.List;

/**
 *
 * @author yoavram
 */
public interface IFoafAgent extends IRdfResource {

    String getAimChatId();

    void setAimChatId(String aimChatId);

    String getGender();

    void setGender(String gender);

    String getHomepage();

    void setHomepage(String homepage);
        
    String getIcqChatId();

    void setIcqChatId(String icqChatId);

    String getJabberId();

    void setJabberId(String jabberId);

    String getMbox();

    void setMbox(String mbox);

    String getMboxSha1Sum();

    void setMboxSha1Sum(String mboxSha1Sum);

    String getMsnChatId();

    void setMsnChatId(String msnChatId);

    String getName();

    void setName(String name);

    String getNick();

    void setNick(String nick);

    String getTipJar();

    void setTipJar(String tipJar);

    String getWeblog();

    void setWeblog(String weblog);

    String getYahooChatId();

    void setYahooChatId(String yahooChatId);
    
    List<IFoafOnlineAccount> getHoldsAccount();
    void setHoldsAccount(List<IFoafOnlineAccount> accounts);
    void addHoldsAccount(IFoafOnlineAccount account);

    List<ISiocUsergroup> getGroups();
    void setGroups(List<ISiocUsergroup> groups);
    void addGroups(ISiocUsergroup group);
}
