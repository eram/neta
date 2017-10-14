package com.netalign.netascutter;

import com.hp.hpl.jena.rdf.model.*;
import com.netalign.sioc.*;
import java.util.*;

/**
 * The <code>AbstractBuilder</code> abstract class implements a few methods identical to all builders 
 * of implementations of the {@link IRdfResource} interface.
 * The class is used to build an object from a Jena {@link Resource}, extracting the properties of the resource
 * to fields of the Java class
 * <p>
 * Implementations such as <code>FooBuilder</code> (the <code>Foo</code> class thus implements the 
 * <code>IRdfResource</code> interface) should extend <code>AbstractBuilder&ltFoo&gt</code> and implement the 
 * <code>build()</code> method.
 * 
 * @author yoavram
 * @see IRdfResource
 * @see Resource
 * @see PersonBuilder
 * @see PostBuilder
 * @see UserBuilder
 * @see ForumBuilder
 */
public abstract class AbstractBuilder<E extends IRdfResource> {	
    protected Resource resource;
    protected E element;

    public AbstractBuilder(Resource resource, E element) {
        this.resource = resource;
        this.element = element;
        if (resource.getURI() != null) {
        	element.setURI(resource.getURI());
        }
    }
    
    /**
     * Builds an object from the resource given at the construction.
     * @return the object built
     */
    public abstract E build();

    protected String getProperty(Property property) {
    	if (resource.hasProperty(property)) {
            RDFNode n = resource.getProperty(property).getObject();            
            if (n.isLiteral()) {
            	return ((Literal) n.as(Literal.class)).getLexicalForm(); // this way we don't get "Yoav Ram@he" - toString will give us the lang tag
            } else {
            	return n.toString(); 
            }
        }
        return Constants.EMPTY_STRING;
    }

    protected List<String> getProperties(Property property) {
        if (resource.hasProperty(property)) {
            List<String> list = new ArrayList<String>();
            StmtIterator it = resource.listProperties(property);
            while (it.hasNext()) {
                list.add(it.nextStatement().getObject().toString());
            }
            return list;
        }
        return Collections.emptyList();
    }

    protected ISiocContainer getContainer(Property property) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected ISiocUser getUser(Property property) {
        ISiocUser user = null;
        if (resource.hasProperty(property)) {
            Resource r = (Resource) resource.getProperty(property).getObject();
            if (r != null) {
                user = new UserBuilder(r, new User()).build();
            }
        }
        return user; //TODO return empty item
    }
    protected IFoafPerson getPerson(Property property) {
    	IFoafPerson person = null;
        if (resource.hasProperty(property)) {
            Resource r = (Resource) resource.getProperty(property).getObject();
            if (r != null) {
                person = new PersonBuilder(r, new Person()).build();
            }
        }
        return person; //TODO return empty item
    }
    

    protected List<ISiocUser> getUsers(Property property) {
        if (resource.hasProperty(property)) {
            List<ISiocUser> list = new ArrayList<ISiocUser>();
            StmtIterator it = resource.listProperties(property);
            while (it.hasNext()) {
                Resource r = (Resource) it.nextStatement().getObject();
                ISiocUser user = new UserBuilder(r, new User()).build();
                if (user != null) {
                    list.add(user);
                }
            }
            if (!list.isEmpty()) {
                return list;
            }
        }
        return Collections.emptyList();
    }

    protected IFoafAgent getAgent(Property property) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected ISiocPost getPost(Property property) {
        ISiocPost post = null;
        if (resource.hasProperty(property)) {
            Resource r = (Resource) resource.getProperty(property).getObject();
            if (r != null) {
                post = new PostBuilder(r, new Post()).build();
            }
        }
        return post; //TODO return empty post
    }

    protected List<ISiocPost> getPosts(Property property) {
        if (resource.hasProperty(property)) {
            List<ISiocPost> list = new ArrayList<ISiocPost>();
            StmtIterator it = resource.listProperties(property);
            while (it.hasNext()) {
                Resource r = (Resource) it.nextStatement().getObject();
                ISiocPost post = new PostBuilder(r, new Post()).build();
                if (post != null) {
                    list.add(post);
                }
            }
            if (!list.isEmpty()) {
                return list;
            }
        }
        return Collections.emptyList();
    }

    protected String getPropertyWithoutPrefix(String separator, Property property) {
        String p = getProperty(property);
        if (p == null || p.isEmpty()) {
            return Constants.EMPTY_STRING;
        }
        return p.substring(p.indexOf(separator) + 1);
    }
    
    protected String getPropertyWithoutPrefix(String[] separators, Property property) {
    	String p = getProperty(property);
        if (p == null || p.isEmpty()) {
            return Constants.EMPTY_STRING;
        }
        int start = 0;
        int i = 0;
    	for (String separator : separators) {    		
        	while ( (i = p.indexOf(separator, start)) > -1 ) {
        		start = i + 1;
        	}
        }
    	return p.substring(start);    	
    }            
}
