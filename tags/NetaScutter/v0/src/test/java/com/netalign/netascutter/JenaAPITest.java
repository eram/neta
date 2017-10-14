package com.netalign.netascutter;

import org.junit.*;
import static org.junit.Assert.*;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.*;

public class JenaAPITest {
	private static JenaReader reader;
	private static Model modelPostWithMaker;
	private static Model modelPerson;
	private static String urlPostWithMaker;
	private static String urlPerson;
	private static Model modelPersonWithKnows;
	private static String urlPersonWithKnows;
		
	@BeforeClass
    public static void setUpClass() throws Exception {
		reader = new JenaReader();
		modelPostWithMaker = ModelFactory.createMemModelMaker().createDefaultModel();
		modelPerson = ModelFactory.createMemModelMaker().createDefaultModel();
		urlPostWithMaker  = "http://lukav.com/wordpress/index.php?sioc_type=post&sioc_id=180";
		urlPerson = "http://lukav.com/wordpress/index.php?sioc_type=user&sioc_id=10";
		modelPersonWithKnows= ModelFactory.createMemModelMaker().createDefaultModel();
		urlPersonWithKnows = "http://192.168.123.4/yoavram.foaf";
		
		reader.read(modelPostWithMaker, urlPostWithMaker);
    	reader.read(modelPerson, urlPerson);
    	reader.read(modelPersonWithKnows, urlPersonWithKnows);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {        
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void findPrimaryPersonTest() {
    	System.out.println("findPrimaryPersonTest");
    	    	
    	Resource r = JenaAPI.findPrimaryPerson(modelPerson, urlPerson);
    	//System.out.println(r.toString());
    	assertNotNull("primary person resource is null", r);       	
    }
    
    @Test
    public void findPrimaryPersonTestFails() {
    	System.out.println("findPrimaryPersonTestFails");
    	    	
    	Resource r = JenaAPI.findPrimaryPerson(modelPostWithMaker, urlPostWithMaker);
    	//System.out.println(r.toString());
    	assertNull("primary person resource should be null", r);    	
    }
    
    @Test
    public void findPrimaryPersonWithKnowsTest() {
    	System.out.println("findPrimaryPersonTest");
    	    	
    	Resource r = JenaAPI.findPrimaryPerson(modelPersonWithKnows, urlPersonWithKnows);
    	//System.out.println(r.toString());
    	assertNotNull("primary person resource is null", r);    	
    }
    
    @Test
    public void findPrimaryUserTest() {
    	System.out.println("findPrimaryUserTest");
    	    	
    	Resource r = JenaAPI.findPrimaryUser(modelPerson, urlPerson);
    	//System.out.println(r.toString());
    	assertNotNull("primary user resource is null", r);    	
    }
    
    @Test
    public void findPrimaryUserTestFails() {
    	System.out.println("findPrimaryUserTestFails");
    	    	
    	Resource r = JenaAPI.findPrimaryUser(modelPostWithMaker, urlPostWithMaker);
    	//System.out.println(r.toString());
    	assertNull("primary user resource should be null", r);    	
    }
    
    @Test
    public void findPrimaryPostTest() {
    	System.out.println("findPrimaryPostTest");
    	    	
    	Resource r = JenaAPI.findPrimaryPost(modelPostWithMaker, urlPostWithMaker);
    	//System.out.println(r.toString());
    	assertNotNull("primary post resource is null", r);    	
    }
    
    @Test
    public void findPrimaryPostTestFails() {
    	System.out.println("findPrimaryPostTestFails");
    	    	
    	Resource r = JenaAPI.findPrimaryPost(modelPerson, urlPerson);
    	//System.out.println(r.toString());
    	assertNull("primary post resource should be null", r);    	
    }    
    
    @Test
    public void findPrimaryTopicTest() {
    	System.out.println("findPrimaryTopicTest");
    	    	
    	Resource r = JenaAPI.findPrimaryTopic(modelPostWithMaker, urlPostWithMaker);
    	//System.out.println(r.toString());
    	assertNotNull("primary topic resource is null", r);
    	
    }

    @Test
    public void findMakerTest() {
    	System.out.println("findMakerTest");
    	    	
    	Resource r = JenaAPI.findMaker(modelPostWithMaker, urlPostWithMaker);
    	//System.out.println(r.toString());
    	assertNotNull("maker resource is null", r);
    	
    }
    
    @Test
    public void findCreatorTest() {
    	System.out.println("findCreatorTest");
    	    	
    	Resource r = JenaAPI.findCreator(modelPostWithMaker, urlPostWithMaker);
    	//System.out.println(r.toString());
    	assertNotNull("creator resource is null", r);
    }
}
