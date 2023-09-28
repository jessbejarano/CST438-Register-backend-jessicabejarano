package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; //for Optional parameter "force"

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
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

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
	
	
	//get student by id
		@GetMapping("/students/{id}")
		public StudentDTO getStudent(@PathVariable("id") int id) {
		    System.out.println("/students/id called.");
		    
		    Student s = studentRepository.findById(id).orElse(null);

		    if(s != null) {
		    	StudentDTO studentDTO = createStudent(s);
		        return studentDTO;
		    }
		    
		    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found by email: " + id);
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
		StudentDTO dto = new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatusCode(), s.getStatus());
		return dto;
	}
	
	
	//add a new student
	@PostMapping("/students/add")
	@Transactional
	public int addStudent(@RequestBody StudentDTO student) {
		System.out.println("/students/add called.");
		
		Student check = studentRepository.findByEmail(student.email());
		if(check != null) { //student already exists
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student email already exists "+student.email());
		}
		
		Student newStudent = new Student();
	
		newStudent.setEmail(student.email());
		newStudent.setName(student.name());
		newStudent.setStatusCode(student.statusCode());
		newStudent.setStatus(student.status());
		studentRepository.save(newStudent);
		
		// return the database generated student_id 
		return newStudent.getStudent_id();
	}
	
	
	//update the student status
	@PutMapping("/students/update/{id}")
	public void updateStudent(@PathVariable("id") int id, @RequestBody StudentDTO sdto) {
		System.out.println("/students/update called.");
		
		Student foundStudent = studentRepository.findById(id).orElse(null);
		
		if(foundStudent == null) {
			throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "student not found "+id);
		}
		
		if(!foundStudent.getEmail().equals(sdto.email())) {
			// update name, email.  new email must not exist in database
			Student check = studentRepository.findByEmail(sdto.email());
			if(check != null) { // error.  email exists.
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student email already exists "+sdto.email());
			}
		}
		
		if(sdto.email() != null) {
			foundStudent.setEmail(sdto.email());
		}
		if(sdto.name() != null) {
			foundStudent.setName(sdto.name());
		}
		if (sdto.statusCode() >= 0) {
		    foundStudent.setStatusCode(sdto.statusCode());
		}

		if(sdto.status() != null) {
			foundStudent.setStatus(sdto.status());
		}
		studentRepository.save(foundStudent);
	}
	
	
	//delete a student
	//delete /course/12389?force=yes
	@DeleteMapping("/students/delete/{id}")
	public void deleteStudent(@PathVariable("id") int id, @RequestParam("force") Optional<String> force) {
		System.out.println("/students/delete called.");
		
		Student foundStudent = studentRepository.findById(id).orElse(null);
		
		if (foundStudent != null) {
			// are there enrollments?
			List<Enrollment> list = enrollmentRepository.findByStudentId(id);
			if (list.size()>0 && force.isEmpty()) {
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student has enrollments");
			} else {
				studentRepository.delete(foundStudent);
			} 
		} else { //student DNE
		    return;
		}
	
	}
}
