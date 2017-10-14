package com.netalign.netascutter.parser;

import com.netalign.sioc.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.*;

/**
 * Populates a {@link ISiocUser} instance from the properties
 * associated with a <code>sioc:User</code> resource.
 *  
 * @author yoavram
 * @see ISiocUser
 */
public class UserBuilder extends AbstractBuilder<ISiocUser> {

    public UserBuilder(Resource resource, ISiocUser user) {
        super(resource, user);
    }

    @Override
    public ISiocUser build(boolean deep) {
        //Agent properties        
        element.setAccountName(getProperty(FOAF.accountName));
        //element.setAccountOf(getAgent(SIOC.account_of)); 
        element.setAvatar(getProperty(SIOC.avatar));
        element.setEmail(getProperty(SIOC.email));
        element.setEmailSha1(getProperty(SIOC.email_sha1));            

        if (deep) {
        	element.setCreatorOf((getPosts(SIOC.creator_of))) ;
        }
        
        if (resource.hasProperty(RDFS.seeAlso)) {
            element.setSeeAlso(resource.getProperty(RDFS.seeAlso).getObject().toString());
        }

        return element;
    }
}