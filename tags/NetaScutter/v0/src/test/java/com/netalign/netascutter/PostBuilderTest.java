/*
 * This file is in the Public Domain
 */
package com.netalign.netascutter;

import com.netalign.rdf.vocabulary.SIOC;
import com.netalign.sioc.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import junit.framework.TestCase;

/**
 * 
 * @author yoavram
 */
public class PostBuilderTest extends TestCase
{
    private Model model;
    private Resource post;
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(PostBuilderTest.class);
    }

    protected void setUp() throws Exception
    {
        model = ModelFactory.createDefaultModel();
        
        post = model.createResource();
        post.addProperty(SIOC.about, "testing PostBuilder");
        post.addProperty(SIOC.content, "testing the post builder is hard work, nothing like testing 1,2,3");
        Resource user = model.createResource("sioc:user", SIOC.User);
        user.addProperty(RDFS.seeAlso, "http://www.netalign.com/siocuser.rdf");
        post.addProperty(SIOC.has_creator, user);
        Resource reply = model.createResource("sioc:post", SIOC.Post);
        reply.addProperty(RDFS.seeAlso, "http://www.netalign.com/siocpost.rdf");
        post.addProperty(SIOC.has_reply, reply);        
        post.addProperty(SIOC.topic, "testing 123");
        post.addProperty(SIOC.topic, "emptiness");
        
    }

    /**
     * Constructor for PersonBuilderTest.
     * @param arg0
     */
    public PostBuilderTest(String arg0)
    {
        super(arg0);
    }

    public void testBuild()
    {
        PostBuilder builder = new PostBuilder(post, new Post());
        ISiocPost p = builder.build();
        
        assertNotNull(p);
        assertEquals("testing PostBuilder", p.getAbout());
        assertEquals("testing the post builder is hard work, nothing like testing 1,2,3", p.getContent());
        assertFalse(p.getHasReply().isEmpty());
        assertNotNull(p.getCreator());
        assertFalse(p.getTopic().isEmpty());
    }

}
