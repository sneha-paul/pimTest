package com.bigname.pim.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.m7.xtreme.xcore.domain.JpaEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.LinkedHashMap;
import java.util.Map;
@Entity
@Table(name = "EMPLOYEE")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employee extends JpaEntity<Employee> {
    private String employeeId;
    private String firstName;
    private String lastName;

    public Employee(String employeeId, String firstName, String lastName) {
        super(employeeId);
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public  Employee(){
        super("");
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
        setExternalId(employeeId);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    protected void setExternalId() {
        this.employeeId = getExternalId();
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("firstName", getFirstName());
        map.put("lastName", getLastName());
        map.putAll(getBasePropertiesMap());
        return map;
    }

    @Override
    public Employee merge(Employee employee) {
        for (String group : employee.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(employee.getExternalId());
                    this.setFirstName(employee.getFirstName());
                    this.setLastName(employee.getLastName());
                    mergeBaseProperties(employee);
                    break;
            }
        }
        return this;
    }
}
