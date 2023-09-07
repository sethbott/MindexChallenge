# Notes about the coding challenge

I really hope it was a simple case of my misunderstanding, running or setting up something incorrectly,
but I had a problem with your challenge. Adding the `ReportingStructure` object and associated controller/implementation
code to retrieve the entire org structure for an employee was no problem, but the code to update an employee's
compensation seems to result in _**another**_ copy of the employee, with the requested updates, instead of updating the
existing one.

I struggled with this last night for quite a while but could not find a problem with my very small amount of added code
for this.

So... I decided to check out your original code, and to see what happens to the data when an employee is updated.
Starting with a fresh checkout of your repo, I simply added another read/verification sequence in
EmployeeServiceImplTest.java, after line 77:
```		
// Read the updated employee again to verify the changes.
readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
assertEmployeeEquivalence(createdEmployee, readEmployee);
```
(I also changed the `LOG.debug` invocation on line 32 of EmployeeServiceImpl.java to say "Reading" instead of "Creating"
and the LOG.debug invocation on line 33 of EmployeeController.java to say "update" instead of "create" -- simple
copy/paste errors, I would assume. :))

When the second read is called, the error ocrrus; see the following log excerpt:
```
2023-09-07 11:57:15.422 DEBUG 52404 --- [o-auto-1-exec-4] c.m.c.service.impl.EmployeeServiceImpl   : Reading employee with id [f41bfb89-f9a2-4a4b-a4e0-2d260c300750]
2023-09-07 11:57:15.443 ERROR 52404 --- [o-auto-1-exec-4] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.springframework.dao.IncorrectResultSizeDataAccessException: Query { "$java" : Query: { "employeeId" : "f41bfb89-f9a2-4a4b-a4e0-2d260c300750"}, Fields: {}, Sort: {} } returned non unique result.] with root cause
```

You'll see that I added a [GET] "/employees" endpoint to return the complete list of employees. I added that as
something I could call from Insomnia to confirm that the _second_ copy of the updated employee was added after the REST
call to update an employee's compensation completed.

In any case, I hope that I just did something silly to cause the second copy of the employee to be created; as I said, 
I spent quite a bit of time on this last night and again today without figuring out the issue, and so decided that I
should send you what I have.
(I would definitely appreciate learning what I might be missing!)
-----    
Here are the endpoints that I added:

```
* GET REPORTING STRUCTURE
    * HTTP Method: GET 
    * URL: localhost:8080/employee/structure/{id}
    * RESPONSE: ReportingStructure
* GET EMPLOYEE COMPENSATION
    * HTTP Method: GET 
    * URL: localhost:8080/employee/salary/{id}
    * RESPONSE: Compensation
* UPDATE EMPLOYEE COMPENSATION
    * HTTP Method: PUT 
    * URL: localhost:8080/employee/salary/{id} 
    * PAYLOAD: Compensation
    * RESPONSE: Compensation
* GET ALL EMPLOYEES
    * HTTP Method: GET 
    * URL: localhost:8080/employees
    * RESPONSE: List<Employee>
```
Note that I added `salary` and `effectiveDate` fields to the `Employee` object. Normally, I would add a (1:1, in this
case) child table linked by the `employeeId` as a foreign key -- with an Entity and a separate Repository to access it,
but I couldn't get that to work with the in-memory MongoDB provided, sorry, so the result is that I'm updating the
`Employee` object directly.

I've certainly done child tables, Entity and Repository objects and all that before with a real Oracle or PostgreSQL DB,
but I was getting an NPE on the `compensationRepository.insert` call in `DataBootstrap.java`. I've left the code in that
class but commented out so you can see where I was going with it. Because I couldn't get past that issue, I kept the
compensation fields on the `Employee` object.

Thank you!

Seth Eilbott  
seth.eilbott@gmail.com  
512-626-5247