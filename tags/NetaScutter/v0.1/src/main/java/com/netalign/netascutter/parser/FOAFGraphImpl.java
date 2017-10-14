package com.netalign.netascutter.parser;

import com.netalign.netascutter.utils.ExtendedSelector;
import com.netalign.netascutter.utils.ModelUtils;
import com.netalign.sioc.*;

import java.util.*;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.netalign.netascutter.interfaces.IFOAFGraph;
import com.netalign.rdf.vocabulary.*;

/**
 * @author ldodds (person) / yoavram (post, container)
 *  
 */
public class FOAFGraphImpl implements IFOAFGraph {

    private Model model;

    public FOAFGraphImpl(Model model) {
        this.model = model;
    }

    /** 
     * @see com.ldodds.foaf.IFOAFGraph#findAllPeople()
     */
    @Override
    public List<IFoafPerson> findAllPeople() {
        return findPeopleUsingSelector(new SimpleSelector(null, RDF.type, FOAF.Person));
    }

    @Override
    public List<ISiocPost> findAllPosts() {
        return findPostsUsingSelector(new SimpleSelector(null, RDF.type, SIOC.Post));
    }
    
    @Override
    public List<ISiocContainer> findAllContainers() {        	
    	List<ISiocContainer> list = findContainersUsingSelector(new SimpleSelector(null, RDF.type, SIOC.Container));
    	list.addAll(findContainersUsingSelector(new SimpleSelector(null, RDF.type, SIOC.Forum)));        
    	list.addAll(findContainersUsingSelector(new SimpleSelector(null, RDF.type, SIOC.Thread)));
    	return list;
    }

    private List<IFoafPerson> getFriends(Resource person) {
        //TODO this sholud be done inside the PersonBuilder really...
        List<IFoafPerson> friends = new ArrayList<IFoafPerson>();
        /* // for some reason this original code doesn't work, yoavram
        for ( StmtIterator iterator = _model.listStatements(ModelUtils
        .getKnowsSelector(person)); iterator.hasNext(); )
        {
        Statement statement = (Statement) iterator.next();
        Resource resource = (Resource) statement.getObject();
        friends.add(new PersonBuilder(resource, new Person()).build());
        }*/
        // this is my new code - yoavram - might be a Jena2 implementation vs former Jena1 impl.
        StmtIterator iter = person.listProperties(FOAF.knows);
        while (iter.hasNext()) {
            Statement statement = iter.nextStatement();
            Resource rs = (Resource) statement.getObject();
            friends.add(new PersonBuilder(rs, new Person()).build());
        }
        return friends;
    }

    /**
     * @see IFOAFGraph#findPrimaryPerson(String)
     */
    @Override
    public IFoafPerson findPrimaryPerson(String base) {
        Resource primary = PrimaryPersonSniffer.findPrimaryResource(model,
                base, true);
        if (primary == null) {
            return null;
        }

        PersonBuilder b = new PersonBuilder(primary, new Person());
        IFoafPerson p = b.build();
        p.setFriends(getFriends(primary));
        return p;
    }

    @Override
    public ISiocPost findPrimaryPost(String base) {
        Resource primary = PrimaryPostSniffer.findPrimaryResource(model,
                base, true);
        if (primary == null) {
            return null;
        }

        ISiocPost p = new PostBuilder(primary, new Post()).build();
        return p;
    }
    	
    /**
     * @see IFOAFGraph#findPrimaryPerson()
     */
    @Override
    public IFoafPerson findPrimaryPerson() {
        return findPrimaryPerson("");
    }

    @Override
    public ISiocPost findPrimaryPost() {
        return findPrimaryPost("");
    }

    /** 
     * @see com.ldodds.foaf.IFOAFGraph#findPersonWithProperty(java.lang.String)
     */
    @Override
    public List<IFoafPerson> findPersonWithProperty(String propertyURI) {
        Property property = model.createProperty(propertyURI);
        Selector selector = new SimpleSelector(null, property, (RDFNode) null);
        return findPeopleUsingSelector(selector);
    }

    @Override
    public List<ISiocPost> findPostWithProperty(String propertyURI) {
        Property property = model.createProperty(propertyURI);
        Selector selector = new SimpleSelector(null, property, (RDFNode) null);
        return findPostsUsingSelector(selector);
    }

    /** 
     * @see com.ldodds.foaf.IFOAFGraph#findPersonByProperty(java.lang.String, java.lang.String)
     */
    @Override
    public List<IFoafPerson> findPersonByProperty(String propertyURI, String value) {
        Property property = model.createProperty(propertyURI);
        Selector selector = new ExtendedSelector(property, value);
        return findPeopleUsingSelector(selector);
    }

    @Override
    public List<ISiocPost> findPostByProperty(String propertyURI, String value) {
        Property property = model.createProperty(propertyURI);
        Selector selector = new ExtendedSelector(property, value);
        return findPostsUsingSelector(selector);
    }
    



    private List<IFoafPerson> findPeopleUsingSelector(Selector selector) {
        List<IFoafPerson> people = new ArrayList<IFoafPerson>();
        for (StmtIterator iterator = model.listStatements(selector); iterator.hasNext();) {
            Statement statement = (Statement) iterator.next();
            Resource subject = statement.getSubject();
            Person person = new Person();
            people.add(new PersonBuilder(subject, person).build());
            person.setFriends(getFriends(subject));
        }
        return people;
    }

    private List<ISiocPost> findPostsUsingSelector(Selector selector) {
        List<ISiocPost> posts = new ArrayList<ISiocPost>();
        for (StmtIterator iterator = model.listStatements(selector); iterator.hasNext();) {
            Statement statement = (Statement) iterator.next();
            Resource subject = statement.getSubject();
            Post post = new Post();
            posts.add(new PostBuilder(subject, post).build());
        }
        return posts;
    }
    
    private List<ISiocContainer> findContainersUsingSelector(Selector selector) {
        List<ISiocContainer> containers = new ArrayList<ISiocContainer>();
        for (StmtIterator iterator = model.listStatements(selector); iterator.hasNext();) {
            Statement statement = (Statement) iterator.next();
            Resource subject = statement.getSubject();  
            if (ModelUtils.isSiocForum(subject)) {
            	containers.add(new ForumBuilder(subject, new Forum()).build());
            } else {
            	containers.add(new ContainerBuilder(subject, new Container()).build());
            }
        }
        return containers;
    }

    /** 
     * @see com.ldodds.foaf.IFOAFGraph#smush()
     */
    @Override
    public void smush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}