package com.mindex.challenge.service.impl;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChallengeApplicationTest {

	private String employeeUrl;
	private String compensationUrl;
	private String allCompensationsUrl;
	private String structureUrl;
	private Employee firstLineManager;
	private Compensation firstLineManagerCompensation;
	private Employee newDeveloper1;
	private Compensation newDeveloper1Compensation;
	private Employee newDeveloper2;
	private Compensation newDeveloper2Compensation;
	private Employee secondLineManager;
	private Compensation secondLineManagerCompensation;
	private Date testDate;

	@Autowired
	private EmployeeService employeeService;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;


	@Before
	public void setup() {
		String baseEmployeeURL = "http://localhost:" + port + "/employee";
		String baseCompensationURL = "http://localhost:" + port + "/compensation";
		employeeUrl = baseEmployeeURL + "/";
		structureUrl = baseEmployeeURL + "/structure/{id}";
		compensationUrl = baseCompensationURL + "/{id}";
		allCompensationsUrl = baseCompensationURL + "/compensations";

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

		testDate = new Date();
		newDeveloper1 = createTestEmployee("John", "Doe", "Engineering", "Developer", null);
		System.out.println("newDeveloper1 = " + newDeveloper1);
		newDeveloper1Compensation = createTestCompensation(newDeveloper1, 50000, testDate);
		System.out.println("newDeveloper1Compensation = " + newDeveloper1Compensation);
		newDeveloper2 = createTestEmployee("Jane", "Doe", "Engineering", "Developer", null);
		System.out.println("newDeveloper2 = " + newDeveloper2);
		newDeveloper2Compensation = createTestCompensation(newDeveloper2, 50000, testDate);
		System.out.println("newDeveloper2Compensation = " + newDeveloper2Compensation);
		List<Employee> directs = new ArrayList<>();
		directs.add(newDeveloper1);
		directs.add(newDeveloper2);
		firstLineManager = createTestEmployee("First-line", "Manager", "Engineering", "Manager", directs);
		firstLineManagerCompensation = createTestCompensation( firstLineManager, 100000, testDate);
		directs = new ArrayList<>();
		directs.add(firstLineManager);
		secondLineManager = createTestEmployee("Second-line", "Manager", "Engineering", "Manager", directs);
		secondLineManagerCompensation = createTestCompensation(secondLineManager, 150000, testDate);
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
		Compensation comp = restTemplate.getForEntity(compensationUrl, Compensation.class, newDeveloper2.getEmployeeId()).getBody();
		assertNotNull(comp);
		assertNotNull(comp.getEmployeeId());
		assertEquals(newDeveloper2.getEmployeeId(), comp.getEmployeeId());
		assertEquals(comp.getSalary(), newDeveloper2Compensation.getSalary());
		assertEquals(comp.getEffectiveDate(), newDeveloper2Compensation.getEffectiveDate());
	}

	@Test
	public void testGetAllCompensations() {
		// Get the list of all compensations now
		List<Compensation> allCompensations = restTemplate.getForEntity(allCompensationsUrl, ArrayList.class).getBody();
		assertNotNull(allCompensations);
		System.out.println("test compensations returned:\n" + allCompensations);
	}

	@Test
	public void testChangeSalary() {
		newDeveloper2Compensation.setSalary(75000);
		Date newDate = new Date();
		newDeveloper2Compensation.setEffectiveDate(newDate);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Compensation updatedEmployeeCompensation =
				restTemplate.exchange(compensationUrl,
						HttpMethod.PUT,
						new HttpEntity<Compensation>(newDeveloper2Compensation, headers),
						Compensation.class,
						newDeveloper2Compensation.getEmployeeId()).getBody();

		assertCompensationEquivalence(newDeveloper2Compensation, updatedEmployeeCompensation);
	}

	private Employee createTestEmployee(String first, String last, String dept, String position, List<Employee> directReports) {
		Employee testEmployee = new Employee();
		testEmployee.setFirstName(first);
		testEmployee.setLastName(last);
		testEmployee.setDepartment(dept);
		testEmployee.setPosition(position);
		testEmployee.setDirectReports(directReports);

		System.out.println("Creating test employee: " + testEmployee);

		// Create checks
		Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

		assertNotNull(createdEmployee.getEmployeeId());
		assertEmployeeEquivalence(testEmployee, createdEmployee);

		return createdEmployee;
	}

	private Compensation createTestCompensation(Employee employee, int salary, Date effectiveDate) {

		// Now create the compensation for the employee
		Compensation testCompensation = new Compensation();
		testCompensation.setEmployeeId(employee.getEmployeeId());
		testCompensation.setSalary(salary);
		testCompensation.setEffectiveDate(effectiveDate);

		System.out.println("Creating test compensation: " + testCompensation);

		Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class, testCompensation.getEmployeeId()).getBody();

		// Create checks
		System.out.println("Returned test compensation = " + createdCompensation);
		assertNotNull(createdCompensation.getEmployeeId());
		assertCompensationEquivalence(createdCompensation, testCompensation);

		return createdCompensation;
	}

	private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
		assertEquals(expected.getFirstName(), actual.getFirstName());
		assertEquals(expected.getLastName(), actual.getLastName());
		assertEquals(expected.getDepartment(), actual.getDepartment());
		assertEquals(expected.getPosition(), actual.getPosition());
	}

	private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
		assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
		assertEquals(expected.getSalary(), actual.getSalary());
		assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
	}

}
