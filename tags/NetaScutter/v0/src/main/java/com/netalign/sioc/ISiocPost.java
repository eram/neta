package com.netalign.sioc;

import java.util.List;

/**
 * Class: sioc:Post
 * http://rdfs.org/sioc/spec/#term_Post

Post - An article or message that can be posted to a Forum.
sub-class-of: 	sioc:Item

A Post is an article or message posted by a User to a Forum. A series of Posts may be threaded if they share a common subject and are connected by reply or by date relationships. Posts will have content and may also have attached files, which can be edited or deleted by the Moderator of the Forum that contains the Post.

The SIOC Types Ontology Module describes some additional, more specific subclasses of sioc:Post. 
 * @author yoavram
 */
public interface ISiocPost extends ISiocItem {
	String getTitle();
	void setTitle(String title);
	
	IFoafPerson getMaker();
	void setMaker(IFoafPerson maker);
	
	String getCreated();
	void setCreated(String created);
	
	String getContentEncoded();
	void setContentEncoded(String content);
	
	String getSeeAlso();
    void setSeeAlso(String seeAlso);
    /**
     * about - Specifies that this Item is about a particular resource, e.g., a Post describing a book, hotel, etc. 
     * @return
     */    
    String getAbout();
    void setAbout(String about);    
    /**
     * attachment - The URI of a file attached to an Item. 
     * @return
     */
    List<String> getAttachment();
    void setAttachment(List<String> attaches);    
    void addAttachment(String attach);  
    /**
     * content - The content of the Item in plain text format. 
     * @return
     */
    String getContent();
    void setContent(String content);
    /**
     * has_container - The Container to which this Item belongs. 
     * @return
     */
    ISiocContainer getContainer();
    void setContainer(ISiocContainer container);
    /**
     * has_modifier - A User who modified this Item.
     * @return
     */ 
    List<ISiocUser> getModifier();
    void setModifier(List<ISiocUser> modifier); 
    void addModifier(ISiocUser modifier);
     /**
     * has_creator - This is the User who made this resource. 
     * @return
     */ 
    ISiocUser getCreator();
    void setCreator(ISiocUser creator);
    /**
     * has_reply - Points to an Item or Post that is a reply or response to this Item or Post. 
     * @return
     */
    List<ISiocPost> getHasReply(); 
    void setHasReply(List<ISiocPost> replies);
    void addHasReply(ISiocPost reply); 
    /**
     * ip_address - The IP address used when creating this Item. This can be associated with a creator. Some wiki articles list the IP addresses for the creator or modifiers when the usernames are absent.
     * @return
     */
    String getIpAddress();
    void setIpAddress(String ipAddress);
    /**
     * next_by_date - Next Item or Post in a given Container sorted by date. 
     * @return
     */
    ISiocPost getNextByDate();
    void setNextByDate(ISiocPost nextItem);
    /**
     * next_version - Links to the next revision of this Item or Post. 
     * @return
     */
    ISiocPost getNextVersion();
    void setNextVersion(ISiocPost nextVersion);
    /**
     * note - A note associated with this Item, for example, if it has been edited by a User. 
     * @return
     */
    String getNote();
    void setNote(String note);
    /**
     * previous_by_date - Previous Item or Post in a given Container sorted by date. 
     * @return
     */
    ISiocPost getPreviousByDate();
    void setPreviousByDate(ISiocPost prevItem);
    /**
     * previous_version - Links to a previous revision of this Item or Post. 
     * @return
     */
    ISiocPost getPreviousVersion();
    void setPreviousVersion(ISiocPost prevVersion);
    /**
     * reply_of - Links to an Item or Post which this Item or Post is a reply to.  
     * @return
     */
    ISiocPost getReplyOf();
    void setReplyOf(ISiocPost replyOf);    
     /**
     * sibling - An Item may have a sibling or a twin that exists in a different Container, but the siblings may differ in some small way (for example, language, category, etc.). The sibling of this Item should be self-describing (that is, it should contain all available information).
     * @return
     */
    List<ISiocPost> getSiblings(); 
    void setSiblings(List<ISiocPost> siblings);
    void addSibling(ISiocPost sibling); // needed here?    
    /**
     * topic - A topic of interest, linking to the appropriate URI, e.g., in the Open Directory Project or of a SKOS category. 
     * @return
     */
    List<String>	getTopic();
    void setTopic(List<String> topics);
    void addTopic(String topic);
}
