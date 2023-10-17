package com.cst438.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@Service
@ConditionalOnProperty(prefix = "gradebook", name = "service", havingValue = "rest")
@RestController
public class GradebookServiceREST implements GradebookService {

	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Value("${gradebook.url}")
	private String gradebook_url;
	
// HTTP message sender	
//	Complete the TODO for the enrollStudent method.
//	When a student adds a class, send POST message to Gradebook back end using EnrollmentDTO.    
//	Use the method restTemplate.postForObject( URL, Object,  EnrollmentDTO.class);

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		System.out.println("Start Message "+ student_email +" " + course_id); 
	
		// TODO use RestTemplate to send message to gradebook service
		
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO(0, student_email, student_name, course_id);

		EnrollmentDTO response = 
                restTemplate.postForObject(
                		gradebook_url + "/enrollment/", 
                    enrollmentDTO, 
                    EnrollmentDTO.class);
		
		if (response != null) {
	        System.out.println("Enrollment successful: " + response.toString());
	    } else {
	        System.out.println("Enrollment failed.");
	    }
	}
	
	
// HTTP message receiver
//	Complete the TODO for the updateCourseGrade method. 
//	Receive FinalGradeDTO data and update database with final grades.
	
	/*
	 * endpoint for final course grades
	 */
	@PutMapping("/course/{course_id}")
	@Transactional
	public void updateCourseGrades( @RequestBody FinalGradeDTO[] grades, @PathVariable("course_id") int course_id) {
		System.out.println("Grades received "+grades.length);
		
		//TODO update grades in enrollment records with grades received from gradebook service
		
		for (FinalGradeDTO gradeDTO : grades) {
			//Get email & grade from grades[]
			String studentEmail = gradeDTO.studentEmail();
	        String grade = gradeDTO.grade();
	        
	        //Find enrolled student from repository using email & course ID 
	        Enrollment enrollment = new Enrollment();
	        enrollment = enrollmentRepository.findByEmailAndCourseId(studentEmail, course_id);
	        
	        if (enrollment != null) {
	            // Update  & save the grade in the enrollments repository
	            enrollment.setCourseGrade(grade);
	            enrollmentRepository.save(enrollment);
	            System.out.println("Final grade was updated for student " + studentEmail);
	        } else {
	            System.out.println("Unable to enter grade for student " + studentEmail);
	        }
		}
	}
}
