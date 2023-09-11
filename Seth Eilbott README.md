# Notes about the coding challenge

From what I can tell, I discovered a simple error with the code; specifically, because the `Employee` object does not
specify the `@MongoId` annotation on the `employeeId` field, MongoDB doesn't know that the field is the primary key.
As a result, when the repository `save` method is called, _a new_ object is created, rather than the existing object
getting updated. This actually hit me for quite a while, as I based my `Compensation` object on that and couldn't
figure out the issue, so the previous version of the code that I pushed still has the problem. With this commit, I've
fixed that problem with the `Employee` and `Compensation` records by adding the annotation for the ID. 
 
With this commit, I've also figured out my silly NPE (a missing `@Autowired` directive, _d'oh!_) and now have the
`Compensation` data totally separate from the `Employee` data.

For the `ReportingStructure` object and associated controller/implementation. I assumed that you wanted that structure
to have each `Employee` record filled out completely, while the direct reports are not filled out (other than their IDs)
when you directly retrieve an enmployee who's a manager.

While I was working on figuring out the problem with the new records being created, I added a [GET] "/employee/employees"
endpoint to return the complete list of employees, and a  [GET] "/compensation/compensations" endpoint to return the
complete list of compensations.

-----    
Here are the endpoints that I added:

```
* GET REPORTING STRUCTURE
    * HTTP Method: GET 
    * URL: localhost:8080/employee/structure/{id}
    * RESPONSE: ReportingStructure
* GET ALL EMPLOYEES
    * HTTP Method: GET 
    * URL: localhost:8080/employee/employees
    * RESPONSE: List<Employee>
* CREATE EMPLOYEE COMPENSATION
    * HTTP Method: POST 
    * URL: localhost:8080/compensation/{id}
    (Because the compensation is tied to an employee, I need the employeeId on the CREATE.) 
    * PAYLOAD: Compensation
    * RESPONSE: Compensation
* GET EMPLOYEE COMPENSATION
    * HTTP Method: GET 
    * URL: localhost:8080/compensation/{id}
    * RESPONSE: Compensation
* UPDATE EMPLOYEE COMPENSATION
    * HTTP Method: PUT 
    * URL: localhost:8080/compensation/{id} 
    * PAYLOAD: Compensation
    * RESPONSE: Compensation
* GET ALL COMPENSATIONS
    * HTTP Method: GET 
    * URL: localhost:8080/compensation/compensations
    * RESPONSE: List<Compensation>
```
-----

Thank you!

Seth Eilbott  
seth.eilbott@gmail.com