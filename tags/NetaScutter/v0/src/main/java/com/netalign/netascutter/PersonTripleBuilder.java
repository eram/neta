package com.netalign.netascutter;

import com.netalign.netascutter.utils.ModelUtils;
import com.netalign.sioc.IFoafPerson;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.rdf.vocabulary.*;

/**
 * Populates a Jena Model with data taken from <code>Person</code> bean
 * instances.
 * 
 * @author ldodds
 */
public class PersonTripleBuilder {

    private Model _model;
    private boolean _encryptEmails;

    public PersonTripleBuilder(Model model) {
        this(model, true);
    }

    public PersonTripleBuilder(Model model, boolean encryptEmails) {
        _model = model;
        _encryptEmails = encryptEmails;
    }

    public Resource addPerson(IFoafPerson person) {
        Resource p = makePerson();

        //resources        
        conditionallyAddProperty(p, FOAF.depiction, person.getDepiction(),
                false);
        conditionallyAddProperty(p, FOAF.homepage, person.getHomepage(), false);
        conditionallyAddProperty(p, FOAF.schoolHomepage, person.getSchool(), false);
        conditionallyAddProperty(p, FOAF.workplaceHomepage, person.getWorkHomepage(), false);
        conditionallyAddProperty(p, FOAF.workInfoHomepage, person.getWorkInfoPage(),
                false);
        conditionallyAddProperty(p, FOAF.phone, "tel:", person.getPhone(), false);
        conditionallyAddProperty(p, FOAF.depiction, person.getDepiction(),
                false);
        conditionallyAddProperty(p, FOAF.weblog, person.getWeblog(), false);

        //literals
        conditionallyAddProperty(p, FOAF.gender, person.getGender(), true);
        conditionallyAddProperty(p, FOAF.title, person.getTitle(), true);
        conditionallyAddProperty(p, FOAF.firstName, person.getFirstName(), true);
        conditionallyAddProperty(p, FOAF.surname, person.getSurname(), true);
        conditionallyAddProperty(p, FOAF.nick, person.getNick(), true);
        conditionallyAddProperty(p, FOAF.name, person.getName(), true);

        conditionallyAddProperty(p, FOAF.aimChatID, person.getAimChatId(), true);
        conditionallyAddProperty(p, FOAF.icqChatID, person.getIcqChatId(), true);
        conditionallyAddProperty(p, FOAF.jabberID, person.getJabberId(), true);
        conditionallyAddProperty(p, FOAF.msnChatID, person.getMsnChatId(), true);
        conditionallyAddProperty(p, FOAF.yahooChatID, person.getYahooChatId(), true);

        conditionallyAddProperty(p, FOAF.mbox_sha1sum, person.getMboxSha1Sum(), true);

        if (person.getSeeAlso() != null && !person.getSeeAlso().isEmpty()) {
            _model.add(p, RDFS.seeAlso, person.getSeeAlso());
        }

        //manufacture some extra properties, to populate as much as possible
        if (person.getName() == null &&
                (person.getFirstName() != null && person.getSurname() != null)) {
            conditionallyAddProperty(p, FOAF.name, person.getFirstName() + " " + person.getSurname(), true);
        }
        if (!_encryptEmails) {
            conditionallyAddProperty(p, FOAF.mbox, "mailto:", person.getMbox(), false);
        }
        return p;
    }

    private void conditionallyAddProperty(Resource resource, Property property,
            String prefix, String propertyValue, boolean literal) {
        if (propertyValue != null) {
            conditionallyAddProperty(resource, property,
                    prefix + propertyValue, literal);
        }
    }

    private void conditionallyAddProperty(Resource resource, Property property,
            String propertyValue, boolean literal) {
        if (propertyValue != null && !propertyValue.isEmpty()) {
            RDFNode node = (literal ? (RDFNode) makeLiteral(propertyValue)
                    : (RDFNode) makeResource(propertyValue));
            _model.add(resource, property, node);
        }
    }

    public void addFriend(Resource person, IFoafPerson friend) {
        Resource friendResource = addPerson(friend);
        _model.add(person, FOAF.knows, friendResource);
    }

    /**
     * TODO where should this method really live?!
     */
    public void removeFriends(Resource person) {
        for (StmtIterator iterator = _model.listStatements(ModelUtils.getKnowsSelector(person)); iterator.hasNext();) {
            Statement statement = (Statement) iterator.next();
            Resource resource = (Resource) statement.getObject();
            resource.removeProperties();
            iterator.remove();
        }
    }

    /**
     * @see com.ldodds.foafamatic.model.ExtendedModel#makePerson()
     */
    private Resource makePerson() {
        Resource _person = _model.createResource();
        _person.addProperty(RDF.type, FOAF.Person);
        return _person;
    }

    private Literal makeLiteral(String value) {
        return _model.createLiteral(value);
    }

    private Resource makeResource(String value) {
        return _model.createResource(value);
    }
}