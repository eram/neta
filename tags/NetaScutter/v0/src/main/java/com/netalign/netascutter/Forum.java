package com.netalign.netascutter;

import java.util.*;

import com.netalign.sioc.*;

/**
 * A simple implementation for the {@link ISiocForum} interface, overriding methods only.
 * @author yoavram
 *
 */
public class Forum implements ISiocForum {
	private ISiocSite host;
	private List<ISiocUser> moderators = Collections.emptyList();
	private ISiocContainer parent;
	private List<ISiocPost> posts = Collections.emptyList();
	private ISiocUser owner;
	private List<ISiocUser> subscribers = Collections.emptyList();
	private List<ISiocContainer> children = Collections.emptyList();
	private String seeAlso = Constants.EMPTY_STRING;
	private String uri = Constants.EMPTY_STRING;
	
	@Override
	public void addHasModerator(ISiocUser user) {
		moderators.add(user);
	}
	@Override
	public ISiocSite getHasHost() {
		return host;
	}
	@Override
	public List<ISiocUser> getHasModerator() {
		return moderators;
	}
	@Override
	public void setHasHost(ISiocSite host) {
		this.host = host;		
	}
	@Override
	public void setHasModerator(List<ISiocUser> users) {
		this.moderators = users;		
	}
	@Override
	public void addContainerOf(ISiocPost post) {
		this.posts.add(post);		
	}
	@Override
	public void addParentOf(ISiocContainer child) {
		this.children.add(child);		
	}
	@Override
	public void addSubscriber(ISiocUser user) {
		this.subscribers.add(user);		
	}
	@Override
	public List<ISiocPost> getContainerOf() {
		return posts;
	}
	@Override
	public ISiocUser getHasOwner() {
		return owner;
	}
	@Override
	public ISiocContainer getHasParent() {
		return parent;
	}
	@Override
	public List<ISiocUser> getHasSubscriber() {
		return subscribers;
	}
	@Override
	public List<ISiocContainer> getParentOf() {
		return children;	}
	@Override
	public void setContainerOf(List<ISiocPost> posts) {
		this.posts = posts;		
	}
	@Override
	public void setHasOwner(ISiocUser user) {
		this.owner = user;		
	}
	@Override
	public void setHasParent(ISiocContainer parent) {
		this.parent = parent;		
	}
	@Override
	public void setHasSubscriber(List<ISiocUser> users) {
		this.subscribers = users;		
	}
	@Override
	public void setParentOf(List<ISiocContainer> children) {
		this.children = children;		
	}
	@Override
	public String getSeeAlso() {
		return seeAlso;
	}
	@Override
	public void setSeeAlso(String seeAlso) {
		this.seeAlso = seeAlso;		
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
