package edu.studyup.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.studyup.entity.Event;
import edu.studyup.entity.Location;
import edu.studyup.entity.Student;
import edu.studyup.util.DataStorage;
import edu.studyup.util.StudyUpException;

class EventServiceImplTest {

	EventServiceImpl eventServiceImpl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		eventServiceImpl = new EventServiceImpl();
		//Create Student
		Student student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		student.setEmail("JohnDoe@email.com");
		student.setId(1);
		
		//Create Event1
		Event event = new Event();
		event.setEventID(1);
		event.setDate(new Date());
		event.setName("Event 1");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		List<Student> eventStudents = new ArrayList<>();
		eventStudents.add(student);
		event.setStudents(eventStudents);
		
		DataStorage.eventData.put(event.getEventID(), event);
	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}

	@Test
	void testUpdateEventName_GoodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 1");
		assertEquals("Renamed Event 1", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEvent_WrongEventID_badCase() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		  });
	}
	
	@Test
	void testUpdateEventName_TooLong () {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class,  () -> {
			eventServiceImpl.updateEventName(eventID, "This event name is way too long");
		});
	}
	
	@Test
	void testUpdateEventName_20chars_ShouldPass () throws StudyUpException {
		int eventID = 1;
		//eventServiceImpl.updateEventName(eventID, "01234567890123456789");
		try {
			eventServiceImpl.updateEventName(eventID, "01234567890123456789");
		} catch (StudyUpException e) {
			fail("Should accept names of 20 characters, but gave an error");
		}
		//assertEquals("01234567890123456789", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testGetActiveEvents_DistantFuture_good() throws StudyUpException {
		// create a second event
		Event event2 = new Event();
		event2.setEventID(2);
		// set event date in 2050
		@SuppressWarnings("deprecation")
		Date date2 = new Date(150, 1, 1);
		event2.setDate(date2);
		event2.setName("Event2");
		DataStorage.eventData.put(event2.getEventID(), event2);
		
		assertTrue(eventServiceImpl.getActiveEvents().contains(event2));
		
	}
	
	@Test
	void testGetActiveEvents_DistantPast_bad() throws StudyUpException{
		// create a third event
		Event event3 = new Event();
		event3.setEventID(2);
		// set event date in 1909
		@SuppressWarnings("deprecation")
		Date date3 = new Date(9, 1, 1);
		event3.setDate(date3);
		event3.setName("Event3");
		DataStorage.eventData.put(event3.getEventID(), event3);
		// output should be false, but will be true 
		assertFalse(eventServiceImpl.getActiveEvents().contains(event3));
		
	}
	
	@Test
	void testAddStudentToEvent_good () throws StudyUpException {
		// create a 2nd student
		Student student2 = new Student();
		student2.setFirstName("Jane");
		student2.setLastName("Doe");
		student2.setEmail("JaneDoe@email.com");
		student2.setId(2);
		
		// load event data
		int eventID = 1;
		Event event = eventServiceImpl.addStudentToEvent(student2, eventID);
		assertTrue(event.getStudents().contains(student2));
	}
	
	@Test
	void testAddStudentToEvent_bad () throws StudyUpException {
		// create a 2nd student
		Student student2 = new Student();
		student2.setFirstName("Jane");
		student2.setLastName("Doe");
		student2.setEmail("JaneDoe@email.com");
		student2.setId(2);
		
		int eventID = 1;
		Event event = eventServiceImpl.addStudentToEvent(student2, eventID);
		// create a 3rd student
		Student student3 = new Student();
		student3.setFirstName("Hunter");
		student3.setLastName("Throckmorton");
		student3.setEmail("HunterThrockmorton@email.com");
		student3.setId(3);
		
		event = eventServiceImpl.addStudentToEvent(student3, eventID);
		// output should be false, but will be true
		assertFalse(event.getStudents().contains(student3));
	}
	
	@Test
	void testGetPastEvents_good () throws StudyUpException {
		// create a second event
		Event event2 = new Event();
		event2.setEventID(2);
		// set event date in 1909
		@SuppressWarnings("deprecation")
		Date date2 = new Date(9, 1, 1);
		event2.setDate(date2);
		event2.setName("Event2");
		DataStorage.eventData.put(event2.getEventID(), event2);
		
		assertTrue(eventServiceImpl.getPastEvents().contains(event2));
		
	}
	
	@Test
	void testDeleteEvent_good () throws StudyUpException {
		Event event3 = DataStorage.eventData.get(1);
		Event event2 = eventServiceImpl.deleteEvent(1);
		assertEquals(event2, event3);
	}
	
	@Test
	void testAddStudentToEvent_NullEvent () {
		// create a 2nd student
		Student student2 = new Student();
		student2.setFirstName("Jane");
		student2.setLastName("Doe");
		student2.setEmail("JaneDoe@email.com");
		student2.setId(2);
		
		int eventID = 2;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student2, eventID);
		});
		
	}
	
	@Test 
	void testAddStudentToEvent_NullStudents () throws StudyUpException {
		//Create Event2
		Event event2 = new Event();
		event2.setEventID(2);
		event2.setDate(new Date());
		event2.setName("Event 2");
		Location location = new Location(-122, 37);
		event2.setLocation(location);
		DataStorage.eventData.put(event2.getEventID(), event2);
		
		// create a 2nd student
		Student student2 = new Student();
		student2.setFirstName("Jane");
		student2.setLastName("Doe");
		student2.setEmail("JaneDoe@email.com");
		student2.setId(2);
		
		eventServiceImpl.addStudentToEvent(student2, 2);
	}
	
}