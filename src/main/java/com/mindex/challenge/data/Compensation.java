package com.mindex.challenge.data;

import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

public class Compensation {
    @MongoId
    private String employeeId;
    private Integer salary;
    private Date effectiveDate;

    public Compensation() {
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public Integer getSalary() { return salary; }
    public void setSalary(Integer salary) { this.salary = salary; }

    public Date getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(Date effectiveDate) { this.effectiveDate = effectiveDate; }

    @Override
    public String toString() {
        return "Compensation{" +
                "employeeId='" + employeeId + '\'' +
                ", salary='" + salary + '\'' +
                ", effectiveDate='" + effectiveDate + '\'' +
                '}';
    }
}
