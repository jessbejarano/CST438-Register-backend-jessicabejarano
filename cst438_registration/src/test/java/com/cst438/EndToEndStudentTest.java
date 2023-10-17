package com.cst438;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

@SpringBootTest
public class EndToEndStudentTest {
	
	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/jessicaren/Downloads/chromedriver-mac-arm64";

	public static final String URL = "http://localhost:3000";

	public static final String TEST_USER_EMAIL = "jesstest@csumb.edu";

	public static final int TEST_COURSE_ID = 40442; 

	public static final String TEST_SEMESTER = "2021 Fall";

	public static final int SLEEP_DURATION = 1000; // 1 second.
	
	WebDriver driver;
	
	@BeforeEach
	public void testSetup() throws Exception {
             // if you are not using Chrome, 
             // the following lines will be different. 
		System.setProperty(
                 "webdriver.chrome.driver", 
                 CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(ops);


		driver.get(URL);
        // must have a short wait to allow time for the page to download 
		Thread.sleep(SLEEP_DURATION);
	}
	
	
	@Test
	public void addStudentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			
			//Get button & click form :
	        WebElement addStudentButton = driver.findElement(By.id("addStudentButton"));
	        addStudentButton.click();

	        //Fill the form:
	        WebElement nameInput = driver.findElement(By.id("name")); 
	        WebElement emailInput = driver.findElement(By.id("email")); 
	        WebElement codeInput = driver.findElement(By.id("code")); 
	        WebElement statusInput = driver.findElement(By.id("status")); 
	        WebElement submitButton = driver.findElement(By.id("add")); 
	        
	        nameInput.sendKeys("Jessica Bee");        
	        emailInput.sendKeys(TEST_USER_EMAIL);       
	        codeInput.sendKeys("0");
	        statusInput.sendKeys("Active");
	        Thread.sleep(SLEEP_DURATION);
	        
	        //Click "add" button" line 70 AddStudent.js frontend
	        submitButton.click();
	        Thread.sleep(SLEEP_DURATION);

			
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}
	}
	
	@Test
	public void updateStudentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			
			//Get button & click form :
	        WebElement addStudentButton = driver.findElement(By.id("updateStudentButton"));
	        addStudentButton.click();	
			
	        //Fill the form:
	        WebElement idInput = driver.findElement(By.id("id")); 
	        WebElement nameInput = driver.findElement(By.id("name")); 
	        WebElement emailInput = driver.findElement(By.id("email")); 
	        WebElement codeInput = driver.findElement(By.id("code")); 
	        WebElement statusInput = driver.findElement(By.id("status")); 
	        WebElement submitButton = driver.findElement(By.id("edit")); 
	        
	        idInput.sendKeys("123");
	        nameInput.sendKeys("Jessica Bee");        
	        emailInput.sendKeys(TEST_USER_EMAIL);       
	        codeInput.sendKeys("0");
	        statusInput.sendKeys("Active");
	        Thread.sleep(SLEEP_DURATION);
	        
	        //Click "edit" button" line 73 EditStudent.js frontend
	        submitButton.click();
	        Thread.sleep(SLEEP_DURATION);
	        
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}
	}
	
	
	@Test
	public void deleteStudentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			//Get delete button & click form : AdminHome.js line 178
		    WebElement addStudentButton = driver.findElement(By.id("deleteStudent"));
		    addStudentButton.click();		
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}
		
	}
	
}
