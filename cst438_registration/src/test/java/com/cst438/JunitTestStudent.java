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

	    // Create a Student object
	    Student student = new Student();
	    student.setEmail("johndoe@example.com");
	    student.setName("john");
	    student.setStatus("active");

	    // Convert the Student object to JSON
	    String requestBody = asJsonString(student);

	    response = mvc.perform(
	            MockMvcRequestBuilders
	                    .post("/students/add")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(requestBody) // Set the JSON data in the request body
	                    .accept(MediaType.APPLICATION_JSON))
	            .andReturn().getResponse();

	    // verify that return status = OK 
	    assertEquals(200, response.getStatus());
	}

	
	
	
	@Test
	public void updateStudent() throws Exception {
	    MockHttpServletResponse response;

	    response = mvc.perform(
	            MockMvcRequestBuilders
	                    .put("/students/update")
	                    .param("email", "johndoe@example.com")
	                    .param("statusCode", "2")
	                    .param("statusMsg", "hello")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON))
	            .andReturn().getResponse();    

	    // verify that return status = OK 
	    assertEquals(200, response.getStatus());
	}

	
	@Test
	public void deleteStudent() throws Exception {
		// Insert a student
		Student studentToInsert = new Student();
		studentToInsert.setEmail("johndoe@example.com");
		studentToInsert.setName("John Doe");
		studentToInsert.setStatus("active");
		studentRepository.save(studentToInsert);
		
		MockHttpServletResponse response;
	    
	    //delete the student
	    response = mvc.perform(
				MockMvcRequestBuilders
			      .delete("/students/delete/johndoe@example.com"))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
		
		//verify that student was actually deleted
		response = mvc.perform(
				MockMvcRequestBuilders
					.get("/students")
					.accept(MediaType.APPLICATION_JSON))
					.andReturn().getResponse();
		assertEquals(200, response.getStatus());
				
				
		//deserialize response content into array of StudentDTO
		StudentDTO[] dto_list = fromJsonString(response.getContentAsString(), StudentDTO[].class);
		
		//verify student no longer exists with a boolean flag
		boolean found = false;
		for (StudentDTO dto : dto_list) {
			if (dto.email().equals("johndoe@example.com")) found=true;
		}
		assertFalse(found);
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
