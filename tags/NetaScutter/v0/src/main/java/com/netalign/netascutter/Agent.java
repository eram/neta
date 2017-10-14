/*
 * This file is in the Public Domain
 */
package com.netalign.netascutter;

import com.netalign.sioc.*;

import java.util.*;

import com.netalign.netascutter.utils.SHA1Encryptor;

/**
 * A simple implementation for the {@link ISiocPost} interface, overriding methods only.
 * 
 * @author ldodds
 * @see IFoafAgent
 */
public class Agent implements IFoafAgent {
	private String uri = Constants.EMPTY_STRING;   
    private String tipJar = Constants.EMPTY_STRING;
    private String yahooChatId = Constants.EMPTY_STRING;
    private String icqChatId = Constants.EMPTY_STRING;
    private String mbox = Constants.EMPTY_STRING;
    private String mboxSha1Sum = Constants.EMPTY_STRING;
    private String aimChatId = Constants.EMPTY_STRING;
    private String gender = Constants.EMPTY_STRING;
    private String jabberId = Constants.EMPTY_STRING;
    private String weblog = Constants.EMPTY_STRING;
    private String msnChatId = Constants.EMPTY_STRING;
    private String homepage = Constants.EMPTY_STRING;
    private String name = Constants.EMPTY_STRING;
    private String nick = Constants.EMPTY_STRING;
    
    private List<ISiocUsergroup> groups = Collections.emptyList();
    
    public Agent()
    {       
    }
    
    /**
     * @return Returns the aimChatId.
     */
    @Override
    public String getAimChatId()
    {
        return aimChatId;
    }
    /**
     * @param aimChatId The aimChatId to set.
     */
    @Override
    public void setAimChatId(String aimChatId)
    {
        this.aimChatId = aimChatId;
    }
    /**
     * @return Returns the gender.
     */
    @Override
    public String getGender()
    {
        return gender;
    }
    /**
     * @param gender The gender to set.
     */
    @Override
    public void setGender(String gender)
    {
    	this.gender = gender;
    }
    /**
     * @return Returns the homepage.
     */
    @Override
    public String getHomepage()
    {
        return homepage;
    }
    /**
     * @param homepage The homepage to set.
     */
    @Override
    public void setHomepage(String homepage)
    {
    	this.homepage = homepage;
    }
    /**
     * @return Returns the icqChatId.
     */
    @Override
    public String getIcqChatId()
    {
        return icqChatId;
    }
    /**
     * @param icqChatId The icqChatId to set.
     */
    @Override
    public void setIcqChatId(String icqChatId)
    {
    	this.icqChatId = icqChatId;
    }
    /**
     * @return Returns the jabberId.
     */
    @Override
    public String getJabberId()
    {
        return jabberId;
    }
    /**
     * @param jabberId The jabberId to set.
     */
    @Override
    public void setJabberId(String jabberId)
    {
    	this.jabberId = jabberId;
    }
    /**
     * @return Returns the mbox.
     */
    @Override
    public String getMbox()
    {
        return mbox;
    }
    /**
     * @param mbox The mbox to set.
     */
    @Override
    public void setMbox(String mbox)
    {
    	this.mbox = mbox;
    }
    
    @Override
    public String getMboxSha1Sum()
    {
        if ( !mboxSha1Sum.isEmpty() )
        {
            return mboxSha1Sum;
        }

        if ( mbox.isEmpty() )
        {
            return mbox;
        }

        return new SHA1Encryptor().encrypt("mailto:" + mbox);
    }

    /**
     * @param mboxSha1Sum The mboxSha1Sum to set.
     */
    @Override
    public void setMboxSha1Sum(String mboxSha1Sum)
    {
    	this.mboxSha1Sum = mboxSha1Sum;
    }
    /**
     * @return Returns the msnChatId.
     */
    @Override
    public String getMsnChatId()
    {
        return msnChatId;
    }
    /**
     * @param msnChatId The msnChatId to set.
     */
    @Override
    public void setMsnChatId(String msnChatId)
    {
    	this.msnChatId = msnChatId;
    }
    /**
     * @return Returns the name.
     */
    @Override
    public String getName()
    {
        return name;
    }
    /**
     * @param name The name to set.
     */
    @Override
    public void setName(String name)
    {
    	this.name = name;
    }
    /**
     * @return Returns the nick.
     */
    @Override
    public String getNick()
    {
        return nick;
    }
    /**
     * @param nick The nick to set.
     */
    @Override
    public void setNick(String nick)
    {
    	this.nick = nick;
    }
    /**
     * @return Returns the tipJar.
     */
    @Override
    public String getTipJar()
    {
        return tipJar;
    }
    /**
     * @param tipJar The tipJar to set.
     */
    @Override
    public void setTipJar(String tipJar)
    {
    	this.tipJar = tipJar;
    }
    /**
     * @return Returns the weblog.
     */
    @Override
    public String getWeblog()
    {
        return weblog;
    }
    /**
     * @param weblog The weblog to set.
     */
    @Override
    public void setWeblog(String weblog)
    {
    	this.weblog = weblog;
    }
    /**
     * @return Returns the yahooChatId.
     */
    @Override
    public String getYahooChatId()
    {
        return yahooChatId;
    }
    /**
     * @param yahooChatId The yahooChatId to set.
     */
    @Override
    public void setYahooChatId(String yahooChatId)
    {
    	this.yahooChatId = yahooChatId;
    }
       
    /**
     * @return Returns the groups.
     */
    @Override
    public List<ISiocUsergroup> getGroups()
    {
        return groups;
    }
    /**
     * @param groups The groups to set.
     */
    @Override
    public void setGroups(List<ISiocUsergroup> groups)
    {
    	this.groups = groups;
    }
    
    @Override
    public void addGroups(ISiocUsergroup group) {
    	this.groups.add(group);
    }
    
    @Override
    public List<IFoafOnlineAccount> getHoldsAccount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHoldsAccount(List<IFoafOnlineAccount> accounts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addHoldsAccount(IFoafOnlineAccount account) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public void setURI(String id) {
		this.uri = id;
	}
}
