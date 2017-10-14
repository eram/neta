package com.netalign.netascutter.parser;

import com.netalign.netascutter.Constants;
import com.netalign.netascutter.utils.SHA1Encryptor;
import com.netalign.sioc.*;

import java.util.*;

/**
 * A simple implementation for the {@link ISiocUser} interface, overriding methods only.
 * @author yoavram
 */
public class User implements ISiocUser {
	private String uri = Constants.EMPTY_STRING;
    private IFoafAgent agent=null;
    private List<ISiocSite> sites = Collections.emptyList();
    private String avatar = Constants.EMPTY_STRING;
    private List<ISiocPost> creations= Collections.emptyList();
    private String email= Constants.EMPTY_STRING;
    private String emailsha1= Constants.EMPTY_STRING;
    private List<ISiocUsergroup> usergroups = Collections.emptyList();
    private String accountName= Constants.EMPTY_STRING;
    private String seeAlso= Constants.EMPTY_STRING;

    @Override
    public IFoafAgent getAccountOf() {
        return agent;
    }

    @Override
    public void setAccountOf(IFoafAgent agent) {
        this.agent = agent;
    }

    @Override
    public List<ISiocSite> getAdministratorOf() {
        return sites;
    }

    @Override
    public void setAdministratorOf(List<ISiocSite> sites) {
        this.sites = sites;
    }

    @Override
    public void addAdministratorOf(ISiocSite site) {
        this.sites.add(site);
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public List<ISiocPost> getCreatorOf() {
        return creations;
    }

    @Override
    public void setCreatorOf(List<ISiocPost> items) {
        this.creations = items;
    }

    @Override
    public void addCreatorOf(ISiocPost item) {
        creations.add(item);
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEmailSha1() {
        if ( !emailsha1.isEmpty() )
        {
            return emailsha1;
        }
        if ( email.isEmpty() )
        {
            return email;
        }

        return new SHA1Encryptor().encrypt("mailto:" + email);
    }

    @Override
    public void setEmailSha1(String emailsha1) {
        this.emailsha1 = emailsha1;
    }

    @Override
    public List<ISiocUsergroup> getMemberOf() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMemberOf(List<ISiocUsergroup> usergroups) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addMemberOf(ISiocUsergroup usergroup) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ISiocForum> getModeratorOf() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModeratorOf(List<ISiocForum> forums) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addModeratorOf(ISiocForum forum) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ISiocPost> getModifierOf() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setModifierOf(List<ISiocPost> items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addModifierOf(ISiocPost items) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ISiocContainer> getOwnerOf() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setOwnerOf(List<ISiocContainer> containers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addOwnerOf(ISiocContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ISiocContainer> getSubscriberOf() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSubscriberOf(List<ISiocContainer> containers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addSubscriberOf(ISiocContainer container) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IFoafDocument getAccountServiceHomepage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAccountServiceHomepage(IFoafDocument document) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAccountName() {
        return accountName;
    }

    @Override
    public void setAccountName(String name) {
        this.accountName = name;
    }

    @Override
    public void setSeeAlso(String seeAlso) {
        this.seeAlso = seeAlso;
    }

    @Override
    public String getSeeAlso() {
        return seeAlso;
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
