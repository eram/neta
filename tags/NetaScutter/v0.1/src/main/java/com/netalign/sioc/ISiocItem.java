package com.netalign.sioc;


/**
 * Class: sioc:Item
 * http://rdfs.org/sioc/spec/#term_Item
 * Item - A content Item that can be posted to or created within a Container.
 * sub-class-of: 	foaf:Document
 * in-range-of:	sioc:container_of sioc:has_reply sioc:modifier_of sioc:next_by_date sioc:next_version sioc:previous_by_date sioc:previous_version sioc:reply_of sioc:sibling
 * in-domain-of:	sioc:about sioc:attachment sioc:content sioc:has_container sioc:has_modifier sioc:has_reply sioc:ip_address sioc:next_by_date sioc:next_version sioc:note sioc:previous_by_date sioc:previous_version sioc:reply_of sioc:sibling
 *
 * Item is a high-level concept for content items. It has subclasses that further specify different types of Items. One of these subclasses (which plays an important role in SIOC) is sioc:Post, used to describe articles or messages created within online community Sites. The SIOC Types Ontology Module describes additional, more specific subclasses of sioc:Item.
 *
 * Items can be contained within Containers. 
 * 
 * @author yoavram
 */
public interface ISiocItem extends IRdfResource {
    
}
