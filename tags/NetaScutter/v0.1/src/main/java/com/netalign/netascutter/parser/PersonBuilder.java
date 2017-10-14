package com.netalign.netascutter.parser;

import com.netalign.sioc.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.*;

/**
 * Populates an {@link IFoafPerson} instance from the properties
 * associated with a <code>foaf:Person</code> resource.
 * 
 * @author yoavram
 */
public class PersonBuilder extends AbstractBuilder<IFoafPerson> {

    public PersonBuilder(Resource resource, IFoafPerson person) {
        super(resource, person);
    }

    @Override
    public IFoafPerson build(boolean deep) {
        //Agent properties        
        String email = getPropertyWithoutPrefix(":", FOAF.mbox);
        element.setMbox(email);
        element.setMboxSha1Sum(getProperty(FOAF.mbox_sha1sum)); //TODO parse multiple mboxs 
        element.setName(getProperty(FOAF.name));
        element.setHomepage(getProperty(FOAF.homepage));
        element.setNick(getProperty(FOAF.nick));
        element.setWeblog(getProperty(FOAF.weblog));
        element.setAimChatId(getProperty(FOAF.aimChatID));
        element.setIcqChatId(getProperty(FOAF.icqChatID));
        element.setJabberId(getProperty(FOAF.jabberID));
        element.setMsnChatId(getProperty(FOAF.msnChatID));
        element.setYahooChatId(getProperty(FOAF.yahooChatID));
        element.setGender(getProperty(FOAF.gender));

        //Person properties
        element.setDepiction(getProperty(FOAF.depiction));
        element.setFirstName(getProperty(FOAF.firstName));
        element.setSurname(getProperty(FOAF.surname));
        element.setFamilyname(getProperty(FOAF.family_name)); 
        element.setGivenname(getProperty(FOAF.givenname)); 
        element.setSchool(getProperty(FOAF.schoolHomepage));
        element.setPhone(getPropertyWithoutPrefix(new String[] {":", "/"}, FOAF.phone));
        element.setTitle(getProperty(FOAF.title));
        element.setWorkHomepage(getProperty(FOAF.workplaceHomepage));
        element.setWorkInfoPage(getProperty(FOAF.workInfoHomepage));
        element.setImg(getProperty(FOAF.img));
        element.setMyersBriggs(getProperty(FOAF.myersBriggs));

        if (resource.hasProperty(RDFS.seeAlso)) {
            element.setSeeAlso(resource.getProperty(RDFS.seeAlso).getObject().toString());
        }                
        return element;
    }
}