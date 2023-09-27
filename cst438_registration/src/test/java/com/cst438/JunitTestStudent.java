package com.cst438;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/* 
 * Example of using Junit 
 * Mockmvc is used to test a simulated REST call to the RestController
 */
@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestStudent {
	
	@Autowired
	private MockMvc mvc;
	

	@Autowired
	StudentRepository studentRepository;
	
	@Test
	public void getStudents() throws Exception {
		MockHttpServletResponse response;
			
		// do http GET for getting all students 
		response = mvc.perform(
				MockMvcRequestBuilders
					.get("/students")
					.accept(MediaType.APPLICATION_JSON))
					.andReturn().getResponse();
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		//deserialize response content into array of StudentDTO
		StudentDTO[] dto_list = fromJsonString(response.getContentAsString(), StudentDTO[].class);
		
		//verify that the array contains students		
		assertThat(dto_list).isNotEmpty();
	}
	
	
	
	@Test
	public void addStudent() throws Exception {
	    MockHttpServletResponse response;

	    // Create a Student dto
	    StudentDTO student = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null);
	    
	    //add the student
	    response = mvc.perform(
	            MockMvcRequestBuilders
	                    .post("/students/add")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	    				.content(asJsonString(student)))
	            .andReturn().getResponse();
	    // verify that return status = OK 
	    assertEquals(200, response.getStatus());
	    
	    
	    //get student
	  	int  student_id = Integer.parseInt(response.getContentAsString());
	  	assertTrue(student_id > 0);
	  	response = mvc.perform(
				MockMvcRequestBuilders
				 .get("/students/"+student_id)
				 .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		StudentDTO actual = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertEquals(student.name(), actual.name());
		assertEquals(student.email(), actual.email());
		assertEquals(student.statusCode(), actual.statusCode());
	  	
		
	  	// and delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/students/delete/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
	}

	
	
	
	@Test
	public void updateStudent() throws Exception {
	    MockHttpServletResponse response;

	    // retrieve the student id = 2
	 	response = mvc.perform(
	 			MockMvcRequestBuilders
	 				 .get("/students/2")
	 				 .accept(MediaType.APPLICATION_JSON))
	 				.andReturn().getResponse();
	 		assertEquals(200, response.getStatus());
	 		StudentDTO original = fromJsonString(response.getContentAsString(), StudentDTO.class);
	 		
	 		// modify name, email and statusCode
	 		StudentDTO mod = new StudentDTO(original.student_id(), "new name", "newname@csumb.edu", 1, "balance outstanding");
	 		
	    response = mvc.perform(
	         MockMvcRequestBuilders
	                    .put("/students/update/2")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .content(asJsonString(mod)))
	            .andReturn().getResponse();    
	    // verify that return status = OK 
	    assertEquals(200, response.getStatus());
	    
	    
	 // retrieve again and check updated fields
	 	response = mvc.perform(
	 			MockMvcRequestBuilders
	 				 .get("/students/2")
	 				 .accept(MediaType.APPLICATION_JSON))
	 				.andReturn().getResponse();
	 		assertEquals(200, response.getStatus());
	 		StudentDTO actual = fromJsonString(response.getContentAsString(), StudentDTO.class);
	 		assertEquals(mod, actual);
	}

	
	@Test
	public void deleteStudentNoEnrollments() throws Exception {
		MockHttpServletResponse response;
		
		StudentDTO studentdto = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null);

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/students/add")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(studentdto)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id > 0);
		
		
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/students/delete/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
		
		// another delete should be OK.
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/students/delete/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
	}
	
	
	@Test
	public void deleteStudentWithEnrollment() throws Exception {
		MockHttpServletResponse response;
		
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/students/delete/1"))
				.andReturn().getResponse();
		assertEquals(400, response.getStatus()); // BAD_REQUEST
		assertTrue(response.getErrorMessage().contains("student has enrollments"));
		
		// now do a force delete
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/students/delete/1?force=yes"))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());	
		
	}
	
	
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	private static <T> T fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
