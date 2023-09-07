package com.mindex.challenge;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChallengeApplicationTest {

	private String employeeUrl;
	private String employeeIdUrl;
	private String employeeCompUrl;
	private String structureUrl;
	private Employee firstLineManager;
	private Employee newDeveloper1;
	private Employee newDeveloper2;
	private Employee secondLineManager;
	private String testDate;

	@Autowired
	private EmployeeService employeeService;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;


	@Before
	public void setup() {
		String baseURL = "http://localhost:" + port + "/employee";
		employeeUrl = baseURL + "/";
		employeeIdUrl = baseURL + "/{id}";
		structureUrl = baseURL + "/structure/{id}";
		employeeCompUrl = baseURL + "/salary/{id}";

		/**
		 * Now set up our test employees with the following simple organizational structure:
	     *
	     * 					SecondLineManager
	     * 							|
	     * 					FirstLineManager
	     * 							|
	     * 			+-------------------------------+
	     * 			|								|
	     * 		NewDeveloper1				NewDeveloper2
	     */

		testDate = new Date().toString();
		newDeveloper1 = createTestEmployee("John", "Doe", "Engineering", "Developer", "50000", testDate, null);
		newDeveloper2 = createTestEmployee("Jane", "Doe", "Engineering", "Developer", "50000", testDate, null);
		List<Employee> directs = new ArrayList<>();
		directs.add(newDeveloper1);
		directs.add(newDeveloper2);
		firstLineManager = createTestEmployee("First-line", "Manager", "Engineering", "Manager", "100000", testDate, directs);
		directs = new ArrayList<>();
		directs.add(firstLineManager);
		secondLineManager = createTestEmployee("Second-line", "Manager", "Engineering", "Manager", "150000", testDate, directs);
	}

	@Test
	public void testRootReportingStructure() {
		ReportingStructure report = restTemplate.getForEntity(structureUrl, ReportingStructure.class, secondLineManager.getEmployeeId()).getBody();
		assertNotNull(report);
		assertNotNull(report.getEmployee());
		Employee checkSecondLineManager = report.getEmployee();
		assertEquals(secondLineManager.getEmployeeId(), checkSecondLineManager.getEmployeeId());

		// The second-line manager should have three employees in his organization.
		assertEquals(3, report.getNumberOfReports());
		// ...and have only the first-line manager as a direct report.
		List<Employee> secondLineReports = secondLineManager.getDirectReports();
		assertEquals(1, secondLineReports.size());
		assertEquals(firstLineManager.getEmployeeId(), secondLineReports.get(0).getEmployeeId());

		Employee checkFirstLineManager = report.getEmployee().getDirectReports().get(0);
		assertEquals(firstLineManager.getEmployeeId(), checkFirstLineManager.getEmployeeId());

		// The first-line manager should have two employees reporting to him.
		List<Employee> directReports = firstLineManager.getDirectReports();
		assertNotNull(directReports);
		assertEquals(2, directReports.size());
		// Verify that the first direct is newDeveloper1 and the second is newDeveloper2.
		assertEquals(newDeveloper1.getEmployeeId(), directReports.get(0).getEmployeeId());
		assertEquals(newDeveloper2.getEmployeeId(), directReports.get(1).getEmployeeId());
	}

	@Test
	public void testGetSalary() {
		Compensation comp = restTemplate.getForEntity(employeeCompUrl, Compensation.class, newDeveloper2.getEmployeeId()).getBody();
		assertNotNull(comp);
		assertNotNull(comp.getEmployeeId());
		assertEquals(newDeveloper2.getEmployeeId(), comp.getEmployeeId());
		assertEquals(newDeveloper2.getSalary(), comp.getSalary());
		assertEquals(newDeveloper2.getEffectiveDate(), comp.getEffectiveDate());
	}

	@Test
	public void testChangeSalary() {
		newDeveloper2.setSalary("75000");
		String newDate = new Date().toString();
		newDeveloper2.setEffectiveDate(newDate);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Employee updatedEmployee =
				restTemplate.exchange(employeeCompUrl,
						HttpMethod.PUT,
						new HttpEntity<Employee>(newDeveloper2, headers),
						Employee.class,
						newDeveloper2.getEmployeeId()).getBody();

		assertEmployeeEquivalence(newDeveloper2, updatedEmployee);
	}

	private Employee createTestEmployee(String first, String last, String dept, String position, String salary, String date, List<Employee> directReports) {
		Employee testEmployee = new Employee();
		testEmployee.setFirstName(first);
		testEmployee.setLastName(last);
		testEmployee.setDepartment(dept);
		testEmployee.setPosition(position);
		testEmployee.setSalary(salary);
		testEmployee.setEffectiveDate(date);
		testEmployee.setDirectReports(directReports);

		// Create checks
		Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

		/****
		 * SETH: When I run this locally I get an Employee object back with every field set to null! I don't think
		 * that the call is actually reaching the controller.
		 */

		assertNotNull(createdEmployee.getEmployeeId());
		assertEmployeeEquivalence(testEmployee, createdEmployee);

		return createdEmployee;
	}

	private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
		assertEquals(expected.getFirstName(), actual.getFirstName());
		assertEquals(expected.getLastName(), actual.getLastName());
		assertEquals(expected.getDepartment(), actual.getDepartment());
		assertEquals(expected.getPosition(), actual.getPosition());
		assertEquals(expected.getSalary(), actual.getSalary());
		assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
	}

}
