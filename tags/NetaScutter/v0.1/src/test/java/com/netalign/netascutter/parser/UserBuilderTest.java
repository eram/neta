/*
 * This file is in the Public Domain
 */
package com.netalign.netascutter.parser;

import com.netalign.netascutter.parser.User;
import com.netalign.netascutter.parser.UserBuilder;
import com.netalign.rdf.vocabulary.*;
import com.netalign.sioc.*;
import com.hp.hpl.jena.rdf.model.*;

import junit.framework.TestCase;

/**

 * @author ldodds
 */
public class UserBuilderTest extends TestCase
{
    private Model model;
    private Resource user;
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(UserBuilderTest.class);
    }

    protected void setUp() throws Exception
    {
        model = ModelFactory.createDefaultModel();
        
        user = model.createResource();
        user.addProperty(FOAF.accountName, "accountname");
        user.addProperty(SIOC.avatar, "avatar");
        user.addProperty(SIOC.email, "test@netalign.com");
    }

    /**
     * Constructor for PersonBuilderTest.
     * @param arg0
     */
    public UserBuilderTest(String arg0)
    {
        super(arg0);
    }

    public void testBuild()
    {
    	UserBuilder builder = new UserBuilder(user, new User());
        ISiocUser u = builder.build();
        
        assertNotNull(u);
        assertEquals(u.getAccountName(), "accountname");
        assertEquals(u.getAvatar(), "avatar");
        assertEquals(u.getEmail(), "test@netalign.com");        
        
    }

}
