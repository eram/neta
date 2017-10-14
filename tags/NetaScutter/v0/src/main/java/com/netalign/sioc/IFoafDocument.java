package com.netalign.sioc;

import java.util.List;

/**
 * Class: foaf:Document
 * http://xmlns.com/foaf/spec/#term_Document
Document - A document.
Status: 	testing
in-range-of:	foaf:homepage foaf:weblog foaf:openid foaf:tipjar foaf:workplaceHomepage foaf:workInfoHomepage foaf:schoolHomepage foaf:interest foaf:publications foaf:isPrimaryTopicOf foaf:page foaf:accountServiceHomepage
in-domain-of:	foaf:sha1 foaf:topic foaf:primaryTopic

The foaf:Document class represents those things which are, broadly conceived, 'documents'.

The foaf:Image class is a sub-class of foaf:Document, since all images are documents.

We do not (currently) distinguish precisely between physical and electronic documents, or between copies of a work and the abstraction those copies embody. The relationship between documents and their byte-stream representation needs clarification (see foaf:sha1 for related issues). 
 * @author yoavram
 */
public interface IFoafDocument extends IRdfResource {
    String getSha1();
    void setSha1(String sha1);
    
    List<String> getTopic();
    void setTopic(List<String> topics);
    void addTopic(String topic);
    
    Object getPrimaryTopic();
    void setPrimaryTopic(Object topic);    
}
