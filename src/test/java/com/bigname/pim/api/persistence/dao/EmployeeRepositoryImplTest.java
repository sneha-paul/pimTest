package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Employee;
import com.bigname.pim.api.persistence.dao.jpa.EmployeeDAO;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.ID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class EmployeeRepositoryImplTest {

    @Autowired
    private EmployeeDAO employeeDAO;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void createEmployeeTest() {
        //creating employees
        List<Map<String, Object>> employeesData = new ArrayList<>();
        employeesData.add(CollectionsUtil.toMap("externalId", "employee1", "firstName", "joseph", "lastName", "P", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee2", "firstName", "sruthi", "lastName", "S", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee3", "firstName", "arun", "lastName", "N", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee4", "firstName", "anu", "lastName", "M", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee5", "firstName", "veena", "lastName", "C", "active", "Y"));
        employeesData.forEach(employeeData -> {
            Employee employeeDTO = new Employee((String)employeeData.get("externalId"), (String) employeeData.get("firstName"), (String)employeeData.get("lastName"));
            employeeDTO.setActive((String)employeeData.get("active"));
            Employee employee1 = employeeDAO.create(employeeDTO);
            Assert.assertTrue(employee1.diff(employeeDTO).isEmpty());
        });
    }
    @Test
    public void retrieveEmployeeTest() {
        //creating employees
        List<Map<String, Object>> employeesData = new ArrayList<>();
        employeesData.add(CollectionsUtil.toMap("externalId", "employee1", "firstName", "joseph", "lastName", "P", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee2", "firstName", "sruthi", "lastName", "S", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee3", "firstName", "arun", "lastName", "N", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee4", "firstName", "anu", "lastName", "M", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee5", "firstName", "veena", "lastName", "C", "active", "Y"));
        employeesData.forEach(employeeData -> {
            Employee employeeDTO = new Employee((String)employeeData.get("externalId"), (String) employeeData.get("firstName"), (String)employeeData.get("lastName"));
            employeeDTO.setActive((String)employeeData.get("active"));
            employeeDAO.create(employeeDTO);
            Optional<Employee> employee1 = employeeDAO.findByExternalId(employeeDTO.getEmployeeId());
            Assert.assertTrue(employee1.isPresent());

            //Getting employee by id
            employee1 = employeeDAO.findById(ID.EXTERNAL_ID(employeeDTO.getEmployeeId()));
            Assert.assertTrue(employee1.isPresent());
            employee1 = employeeDAO.findById(ID.INTERNAL_ID(employeeDTO.getId()));
            Assert.assertTrue(employee1.isPresent());

            //Getting employee as list
            Employee result = employeeDAO.findOne(CollectionsUtil.toMap("externalId", employeesData.get(0).get("externalId"))).orElse(null);
            Assert.assertEquals(result.getFirstName() , "joseph");
        });
    }
    @Test
    public void updateEmployeeTest() {
        //creating employees
        List<Map<String, Object>> employeesData = new ArrayList<>();
        employeesData.add(CollectionsUtil.toMap("externalId", "employee1", "firstName", "joseph", "lastName", "P", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee2", "firstName", "sruthi", "lastName", "S", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee3", "firstName", "arun", "lastName", "N", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee4", "firstName", "anu", "lastName", "M", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee5", "firstName", "veena", "lastName", "C", "active", "Y"));
        employeesData.forEach(employeeData -> {
            Employee employeeDTO = new Employee((String) employeeData.get("externalId"), (String) employeeData.get("firstName"), (String) employeeData.get("lastName"));
            employeeDTO.setActive((String) employeeData.get("active"));
            employeeDAO.create(employeeDTO);

            //updating employee
            Employee employeeDetails = employeeDAO.findByExternalId(employeesData.get(0).get("externalId").toString()).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(employeeDetails));
            employeeDetails.setFirstName("thomas");
            employeeDetails.setGroup("DETAILS");
            employeeDAO.save(employeeDetails);

            Employee employee = employeeDAO.findByExternalId(employeeDetails.getEmployeeId()).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(employee));
            Map<String, Object> diff = employeeDTO.diff(employee);
            Assert.assertEquals(diff.get("firstName"), "thomas");
        });
    }
    @Test
    public void retrieveEmployeesTest() {
        List<Map<String, Object>> employeesData = new ArrayList<>();
        employeesData.add(CollectionsUtil.toMap("externalId", "employee1", "firstName", "joseph", "lastName", "P", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee2", "firstName", "sruthi", "lastName", "S", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee3", "firstName", "arun", "lastName", "N", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee4", "firstName", "anu", "lastName", "M", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee5", "firstName", "veena", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee6", "firstName", "thomas", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee7", "firstName", "anju", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee8", "firstName", "anitha", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee9", "firstName", "archana", "lastName", "C", "active", "Y"));
        employeesData.forEach(employeeData -> {
            Employee employeeDTO = new Employee((String) employeeData.get("externalId"), (String) employeeData.get("firstName"), (String) employeeData.get("lastName"));
            employeeDTO.setActive((String) employeeData.get("active"));
            employeeDAO.create(employeeDTO);
        });

        //Getting employees as list
        List<Employee> result = employeeDAO.findAll(CollectionsUtil.toMap("externalId", employeesData.get(0).get("externalId")));
        Assert.assertEquals(result.get(0).getFirstName() , "joseph");

        //find all Employees as Page
        Page<Employee> employees = employeeDAO.findAll("firstName", "joseph", PageRequest.of(0, employeesData.size() ,null), false);
        Assert.assertEquals(employeesData.get(0).get("firstName"), employees.getContent().get(0).getFirstName());


        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size()), false).getTotalElements(), employeesData.size());
        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size() - 1), false).getTotalElements(), employeesData.size());
        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size() - 1), false).getContent().size(), employeesData.size() - 1);
        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(1, employeesData.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size() - 1), false).getTotalPages(), 2);

        employeeDAO.deleteAll();

        employeesData = new ArrayList<>();
        employeesData.add(CollectionsUtil.toMap("externalId", "employee1", "firstName", "joseph", "lastName", "P", "active", "N"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee2", "firstName", "sruthi", "lastName", "S", "active", "N"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee3", "firstName", "arun", "lastName", "N", "active", "N"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee4", "firstName", "anu", "lastName", "M", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee5", "firstName", "veena", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee6", "firstName", "thomas", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee7", "firstName", "anju", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee8", "firstName", "anitha", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee9", "firstName", "archana", "lastName", "C", "active", "Y"));

        int[] activeCount = {0}, inactiveCount = {0};

        employeesData.forEach(employeeData -> {
            Employee employeeDTO = new Employee((String) employeeData.get("externalId"), (String) employeeData.get("firstName"), (String) employeeData.get("lastName"));
            employeeDTO.setActive((String) employeeData.get("active"));
            if("Y".equals(employeeData.get("active"))) {
                activeCount[0] ++;
            } else {
                inactiveCount[0] ++;
            }
            employeeDAO.create(employeeDTO);
        });
        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
    }

    @After
    public void tearDown() throws Exception {
        employeeDAO.deleteAll();
    }
}