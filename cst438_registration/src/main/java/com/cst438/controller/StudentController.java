package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import com.google.common.base.Optional; //for Optional parameter "force"
@RestController
@CrossOrigin 
public class StudentController {

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	GradebookService gradebookService;
	
	
	
	//get / list all students
	@GetMapping("/students")
	public StudentDTO[] getStudents() {
		System.out.println("/students called.");
		
		// find all students and put into list
		Iterable<Student> studentsIterable = studentRepository.findAll();
		List<Student> studentList = new ArrayList<>();
		
		//add all students into a List
		for(Student s : studentsIterable) {
			studentList.add(s);
		}
		
		if(studentList.isEmpty()) {
			return new StudentDTO[0];
		} else {
			//uses a method to create StudentDTO[] array, using the list
			StudentDTO[] allStudents = createStudentsDTO(studentList);
			return allStudents;
		}
 	}
	
	
	//get student by email
		@GetMapping("/students/search")
		public StudentDTO getStudentByEmail(@RequestParam("email") String email) {
		    System.out.println("/students/search called.");

		    if (email != null) {
		        Student s = studentRepository.findByEmail(email);
		        if (s != null) {
		        	StudentDTO student = createStudent(s);
		            return student;
		        }
		    }
		    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found by email: " + email);
		}
	
	
	// creates StudentDTO[] using the list, and using a method to insert each student
	public StudentDTO[] createStudentsDTO(List<Student> studentList) {
		StudentDTO[] result = new StudentDTO[studentList.size()];
		for(int i = 0; i < studentList.size(); i++) {
			StudentDTO dto = createStudent(studentList.get(i));
			result[i] = dto;
		}
		return result;
	}
	
	
	// method to insert each student
	public StudentDTO createStudent(Student s) {
		StudentDTO dto = new StudentDTO(s.getStudent_id(), s.getStatusCode(), s.getName(), s.getEmail(), s.getStatus());
		return dto;
	}
	
	
	//add a new student
	@PostMapping("/students/add")
	@Transactional
	public void addStudent(@RequestBody Student student) {
		System.out.println("/students/add called.");
		Student newStudent = new Student();
	
		 if(student != null && student.getEmail() != null && student.getName() != null && student.getStatus() != null) {
			 System.out.println("student info");
			 studentRepository.save(student);
		 } else {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student e-mail, name, or status invalid");
		}
	}
	
	
	//update the student status
	@PutMapping("/students/update")
	@Transactional //?
	public void updateStatus(@RequestParam("email") String email, @RequestParam("statusCode") int statusCode, @RequestParam("statusMsg") String statusMsg) {
		System.out.println("/students/update called.");
		Student foundStudent = studentRepository.findByEmail(email);
		
		if(foundStudent != null) {
			foundStudent.setStatusCode(statusCode);
			foundStudent.setStatus(statusMsg);
			studentRepository.save(foundStudent);
		} else {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student e-mail / status message invalid"+email);
		}
	}
	
	
	//delete a student
	//delete /course/12389?force=yes
	@DeleteMapping("/students/delete/{email}")
	@Transactional
	public void deleteStudent(@PathVariable String email, @RequestParam(required = false, name = "force") Optional<String> force) {
		System.out.println("/students/delete called.");
		Student foundStudent = studentRepository.findByEmail(email);
		
		if (foundStudent != null) {
			studentRepository.delete(foundStudent);
		} else {
		    throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student e-mail invalid"+email);
		}
	}
	
}
