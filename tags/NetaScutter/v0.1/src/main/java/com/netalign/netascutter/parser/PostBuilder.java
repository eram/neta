package com.netalign.netascutter.parser;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.vocabulary.DC;
import com.netalign.rdf.vocabulary.*;
import com.netalign.rdf.vocabulary.DCTerms;
import com.netalign.sioc.ISiocPost;


/**
 * Populates an {@link ISiocPost} instance from the properties
 * associated with a <code>sioc:Post</code> resource.
 * 
 * 
 * @author yoavram
 * @see ISiocPost
 */
public class PostBuilder extends AbstractBuilder<ISiocPost> {
 
	public PostBuilder(Resource resource, ISiocPost post) {
        super(resource, post);
    }

    @Override
    public ISiocPost build(boolean deep) {
        //Item properties        
        //_person.setMboxSha1Sum(getProperty(FOAF.mbox_sha1sum));
        element.setAbout(getProperty(SIOC.about));
        element.setContent(getProperty(SIOC.content));
        element.setIpAddress(getProperty(SIOC.ip_address));
        element.setNote(getProperty(SIOC.note));
        element.setTitle(getProperty(DC.title));
        element.setCreated(getProperty(DCTerms.created));
        element.setContentEncoded(getProperty(Content.encoded));
        element.setTopic(getProperties(SIOC.topic));
        if (deep) {
	        element.setCreator(getUser(SIOC.has_creator));
	        element.setMaker(getPerson(FOAF.maker));
	        //element.setModifier(getUsers(SIOC.has_modifier));
	        //element.setContainer(getContainer(SIOC.has_container));
	        element.setNextByDate(getPost(SIOC.next_by_date));
	        element.setPreviousByDate(getPost(SIOC.next_by_date));
	        element.setNextVersion(getPost(SIOC.next_version));
	        element.setPreviousVersion(getPost(SIOC.previous_version));
	        element.setReplyOf(getPost(SIOC.reply_of));
	        // somebody decided to use sioc:reply_to instead of reply_of...
	        if (element.getReplyOf() == null) {
	        	element.setReplyOf(getPost(SIOC.reply_to));
	        }
	        //element.setAttachment(getProperties(SIOC.attachment));
	        element.setHasReply(getPosts(SIOC.has_reply));
	        element.setSiblings(getPosts(SIOC.sibling));
	        }
        

        if (resource.hasProperty(RDFS.seeAlso)) {
            element.setSeeAlso(resource.getProperty(RDFS.seeAlso).getObject().toString());
        }

        return element;
    }
}