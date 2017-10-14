package com.netalign.netascutter;


import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.netalign.netascutter.interfaces.*;
import com.netalign.rdf.vocabulary.*;
import com.netalign.sioc.*;

import static org.easymock.EasyMock.*;

public class RESTPersonAggregatorTest {
	private IRESTConnection restMock;
	private IUrlListener listenerMock;
	private IFoafPerson personMock;
	private IFoafPerson newFriendMock;
	private IFoafPerson oldFriendMock;
	private IConverter converterMock;
	private List<IFoafPerson> friendList;
	private String personUrl;
	private String oldfriendUrl;
	private String newfriendUrl;
	
	
	private RESTPersonAggregator agg;	 
	
	@Before
	public void setUp() throws Exception {
		restMock = createStrictMock(IRESTConnection.class);
		listenerMock = createStrictMock(IUrlListener.class);
		
		converterMock = createMock(IConverter.class);
				
		personMock = createMock("person", IFoafPerson.class);
		newFriendMock = createMock("newFriend", IFoafPerson.class);
		oldFriendMock = createMock("oldFriend", IFoafPerson.class);
		
		friendList = new ArrayList<IFoafPerson>();
		friendList.add(newFriendMock);
		friendList.add(oldFriendMock);
		oldfriendUrl = "http://www.netalign.com/test?person=newfriend&url=url";
		newfriendUrl = "http://www.netalign.com/test?person=newfriend&url=url";
		personUrl = "http://www.netalign.com/test?person=person&url=url";
		
		agg = new RESTPersonAggregator();		
		agg.setListener(listenerMock);
		agg.setRestCon(restMock);
		agg.setConverter(converterMock);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAdd() {
		System.out.println("Add");
		Map<String,String> map = new HashMap<String,String>();
		String rand = UUID.randomUUID().toString();
		String rand2 = UUID.randomUUID().toString();
		String rand3 = UUID.randomUUID().toString();
		String fieldName = UUID.randomUUID().toString();

		// expect any times:
		expect(newFriendMock.getMboxSha1Sum()).andReturn(rand);
		expectLastCall().anyTimes(); 
		expect(newFriendMock.getName()).andReturn(rand);
		expectLastCall().anyTimes();   
		expect(newFriendMock.getSeeAlso()).andReturn(newfriendUrl);
		expectLastCall().anyTimes();
		expect(oldFriendMock.getMboxSha1Sum()).andReturn(rand2);
		expectLastCall().anyTimes();   
		expect(oldFriendMock.getName()).andReturn(rand2);
		expectLastCall().anyTimes();   
		expect(oldFriendMock.getSeeAlso()).andReturn(oldfriendUrl);
		expectLastCall().anyTimes();   
		expect(personMock.getMboxSha1Sum()).andReturn(rand3);
		expectLastCall().anyTimes();   
		expect(personMock.getName()).andReturn(rand3);
		expectLastCall().anyTimes();  
		expect(personMock.getSeeAlso()).andReturn(Constants.EMPTY_STRING);
		expectLastCall().anyTimes();  
		expect(converterMock.getFieldName(FOAF.mbox_sha1sum)).andReturn(fieldName);
		expectLastCall().anyTimes();
		
		// get the friends
		expect(personMock.getFriends()).andReturn(friendList);
		// for each friend 	
			// new friend				
		expect(restMock.getUserId(fieldName, rand)).andReturn(Constants.ILLEGAL_ID);
				// creating new user  
		expect(converterMock.convertPerson(newFriendMock, newfriendUrl)).andReturn(map);
		expect(restMock.createUser(map)).andReturn(2); 
				// sending url to listener
		listenerMock.addURL(newfriendUrl);
			// old friend		
		expect(restMock.getUserId(fieldName, rand2)).andReturn(3);
			// sending url to listener		
		listenerMock.addURL(oldfriendUrl);
		// end foreach friend
		
		// person now		
		expect(restMock.getUserId(fieldName, rand3)).andReturn(Constants.ILLEGAL_ID);
			// creating user				
		expect(converterMock.convertPerson(personMock, personUrl)).andReturn(map);
		expect(restMock.createUser(map)).andReturn(4); 
		
		// adding friends uid (2,3) to friends list of person (4)
		// new friend
		expect(restMock.checkBuddies(4, 2)).andReturn(false); // not friend
		restMock.addBuddy(4, 2);
		expect(restMock.checkBuddies(4, 2)).andReturn(true); // now they are buddies!
		// old friend
		expect(restMock.checkBuddies(4, 3)).andReturn(true); // already buddies
		
		// replay mock objects
		replay(personMock);
		replay(newFriendMock);
		replay(oldFriendMock);
		replay(restMock);
		replay(listenerMock);
		replay(converterMock);
		
		// start the test
		agg.add(personMock, personUrl);
		
		verify(restMock);
	}

}
