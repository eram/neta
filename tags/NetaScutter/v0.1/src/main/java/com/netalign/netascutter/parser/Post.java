package com.netalign.netascutter.parser;

import java.util.Collections;
import java.util.List;

import com.netalign.netascutter.Constants;
import com.netalign.sioc.*;

/**
 * A simple implementation for the {@link ISiocPost} interface, overriding methods only.
 * @author yoavram
 */
public class Post implements ISiocPost {
	private String uri = Constants.EMPTY_STRING;
	//SIOC
	private String about = Constants.EMPTY_STRING;    
    private String content = Constants.EMPTY_STRING;
    private String note = Constants.EMPTY_STRING;
    private String ipAddress = Constants.EMPTY_STRING;
    
    private ISiocContainer container=null;    
    private ISiocUser creator=null;
        
    private ISiocPost nextByDate=null;
    private ISiocPost nextByVersion=null;
    private ISiocPost prevByDate=null;    
    private ISiocPost prevByVersion=null;
    private ISiocPost replyOf=null;
    
    private List<String> topics= Collections.emptyList();
    private List<String> attachments= Collections.emptyList();
    private List<ISiocPost> siblings= Collections.emptyList();
    private List<ISiocPost> replies= Collections.emptyList();
    private List<ISiocUser> modifiers= Collections.emptyList();
    
	// RDFS
    private String seeAlso= Constants.EMPTY_STRING;
    //DCTERMS
	private String created= Constants.EMPTY_STRING;           
	//FOAF
	private IFoafPerson maker=null;
	//DC
	private String title = Constants.EMPTY_STRING;
	//Content
	private String contentEncoded;
	
	@Override
	public String getTitle() {
		return title ;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;		
	}

	@Override
	public IFoafPerson getMaker() {
		return maker;
	}

	@Override
	public void setMaker(IFoafPerson maker) {
		this.maker = maker;		
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
    public String getAbout() {
        return about;
    }

    @Override
    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public List<String> getAttachment() {
        return attachments;
    }

    @Override
    public void setAttachment(List<String> attaches) {
        this.attachments = attaches;
    }

    @Override
    public void addAttachment(String attach) {
        this.attachments.add(attach);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public ISiocContainer getContainer() {
        return container;
    }

    @Override
    public void setContainer(ISiocContainer container) {
        this.container = container;
    }

    @Override
    public List<ISiocUser> getModifier() {
        return modifiers;
    }

    @Override
    public void setModifier(List<ISiocUser> modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public void addModifier(ISiocUser modifier) {
        this.modifiers.add(modifier);
    }

    @Override
    public ISiocUser getCreator() {
        return creator;
    }

    @Override
    public void setCreator(ISiocUser creator) {
        this.creator=creator;
    }

    @Override
    public List<ISiocPost> getHasReply() {
        return replies;
    }

    @Override
    public void setHasReply(List<ISiocPost> replies) {
        this.replies = replies;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public ISiocPost getNextByDate() {
        return nextByDate;
    }

    @Override
    public void setNextByDate(ISiocPost nextItem) {
        this.nextByDate = nextItem;
    }

    @Override
    public ISiocPost getNextVersion() {
        return nextByVersion;
    }

    @Override
    public void setNextVersion(ISiocPost nextItem) {
        this.nextByVersion = nextItem;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }   

	@Override
	public void addTopic(String topic) {
		this.topics.add(topic);
		
	}

	@Override
	public List<String> getTopic() {
		return topics;
	}

	@Override
	public void setTopic(List<String> topics) {
		this.topics=topics;		
	}

	@Override
	public void addHasReply(ISiocPost reply) {
		replies.add(reply);		
	}

	@Override
	public void addSibling(ISiocPost sibling) {
		this.siblings.add(sibling);
		
	}

	@Override
	public String getCreated() {
		return created;
	}

	@Override
	public void setCreated(String created) {
		this.created = created;		
	}

	@Override
	public String getContentEncoded() {
		return contentEncoded;
	}

	@Override
	public void setContentEncoded(String content) {
		this.contentEncoded = content;	
	}

	@Override
	public ISiocPost getPreviousByDate() {
		return prevByDate;
	}

	@Override
	public ISiocPost getPreviousVersion() {
		return prevByVersion;
	}

	@Override
	public ISiocPost getReplyOf() {
		return replyOf;
	}

	@Override
	public List<ISiocPost> getSiblings() {
		return siblings;
	}

	@Override
	public void setPreviousByDate(ISiocPost prevItem) {
		this.prevByDate = prevItem;		
	}

	@Override
	public void setPreviousVersion(ISiocPost prevVersion) {
		this.prevByVersion = prevVersion;
	}

	@Override
	public void setReplyOf(ISiocPost replyOf) {
		this.replyOf = replyOf;
	}

	@Override
	public void setSiblings(List<ISiocPost> siblings) {
		this.siblings = siblings;
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
