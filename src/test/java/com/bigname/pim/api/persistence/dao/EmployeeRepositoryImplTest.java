/*
package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Employee;
import com.bigname.pim.api.persistence.dao.jpa.EmployeeDAO;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.Criteria;
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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;

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

            //findByExternalId
            Optional<Employee> employee1 = employeeDAO.findById(ID.EXTERNAL_ID(employeeDTO.getEmployeeId()), false);
            Assert.assertTrue(employee1.isPresent());

            //Getting employee by id
            employee1 = employeeDAO.findById(ID.EXTERNAL_ID(employeeDTO.getEmployeeId()));
            Assert.assertTrue(employee1.isPresent());
            employee1 = employeeDAO.findById(ID.INTERNAL_ID(employeeDTO.getId()));
            Assert.assertTrue(employee1.isPresent());

            //Getting employee by externalId
            Employee result = employeeDAO.findOne(Criteria.where("externalId").eq(employeesData.get(0).get("externalId"))).orElse(null);
            Assert.assertEquals(result.getFirstName() , "joseph");

            //findByIdAndActiveIn
            employee1  = employeeDAO.findById(ID.INTERNAL_ID(employeeDTO.getId()), false);
            Assert.assertTrue(employee1.isPresent());
            Assert.assertEquals(employee1.orElse(null).getFirstName(), employeeDTO.getFirstName());

            //findByExternalIdAndActiveIn
            employee1  = employeeDAO.findById(ID.EXTERNAL_ID(employeeDTO.getEmployeeId()), false);
            Assert.assertTrue(employee1.isPresent());
            Assert.assertEquals(employee1.orElse(null).getFirstName(), employeeDTO.getFirstName());

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
            Employee employeeDetails = employeeDAO.findById(ID.EXTERNAL_ID(employeesData.get(0).get("externalId")), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(employeeDetails));
            employeeDetails.setFirstName("thomas");
            employeeDetails.setGroup("DETAILS");
            employeeDAO.save(employeeDetails);

            Employee employee = employeeDAO.findById(ID.EXTERNAL_ID(employeeDetails.getEmployeeId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(employee));
            Map<String, Object> diff = employeeDTO.diff(employee);
            Assert.assertEquals(diff.get("firstName"), "thomas");
        });
    }
    @Test
    public void retrieveEmployeesTest() {
        List<Map<String, Object>> employeesData = new ArrayList<>();
        employeesData.add(CollectionsUtil.toMap("externalId", "employee1", "firstName", "sruthi", "lastName", "P", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee2", "firstName", "joseph", "lastName", "S", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee3", "firstName", "arun", "lastName", "N", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee4", "firstName", "anu", "lastName", "M", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee5", "firstName", "veena", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee6", "firstName", "thomas", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee7", "firstName", "sruthi", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee8", "firstName", "anitha", "lastName", "C", "active", "Y"));
        employeesData.add(CollectionsUtil.toMap("externalId", "employee9", "firstName", "sruthi", "lastName", "C", "active", "Y"));
        employeesData.forEach(employeeData -> {
            Employee employeeDTO = new Employee((String) employeeData.get("externalId"), (String) employeeData.get("firstName"), (String) employeeData.get("lastName"));
            employeeDTO.setActive((String) employeeData.get("active"));
            employeeDAO.create(employeeDTO);
        });

        //Getting employees as list
        List<Employee> result = employeeDAO.findAll(Criteria.where("active").eq("Y").and("lastName").eq("C").and("firstName").eq("sruthi"));
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals(result.get(0).getFirstName() , "sruthi");

        //find all Employees as Page
        Page<Employee> employees = employeeDAO.findAll("lastName", "C", PageRequest.of(0, employeesData.size() ,null), false);
        Assert.assertEquals(employees.getContent().size(), 5);
        Assert.assertEquals(employees.getContent().get(0).getLastName(), "C");

        //findByActiveIn
        List<Employee> employeeList = employeeDAO.findAll(false);
        //Assert.assertEquals(employeeList.size(), employeesData.size()); - TODO

        //findByActiveIn
        Page<Employee> employeePage = employeeDAO.findAll(PageRequest.of(1, employeesData.size(), Sort.by("firstName")), false);
        Assert.assertEquals(employeesData.size(), employeePage.getSize());

        //findByExternalIdStartingWith
//        List<Employee> employeeList1 = employeeDAO.findByExternalIdStartingWith((String)employeesData.get(0).get("externalId"), false);
//        Assert.assertEquals(employeeList1.size(), 1);
//        Assert.assertEquals(employeeList1.get(0).getEmployeeId(),employeesData.get(0).get("externalId") );
//
//        //findByExternalIdInAndActiveIn return as list
//        String[] externalIds = {employeesData.get(0).get("externalId").toString(), employeesData.get(1).get("externalId").toString(), employeesData.get(2).get("externalId").toString()};
//        employeeList1 = employeeDAO.findByIdIn(Arrays.stream(externalIds).map(ID::EXTERNAL_ID).collect(Collectors.toList()), false);
//        Assert.assertEquals(employeeList1.size(), externalIds.length);
//
//        //findByExternalIdInAndActiveIn return as Page
//        employeePage = employeeDAO.findByIdIn(Arrays.stream(externalIds).map(ID::EXTERNAL_ID).collect(Collectors.toList()), PageRequest.of(0, employeesData.size()), false);
//        Assert.assertEquals(employeePage.getContent().size(), externalIds.length);
//
//        //findByExternalIdNotInAndActiveIn
//        employeePage = employeeDAO.findByIdNotIn(Arrays.stream(externalIds).map(ID::EXTERNAL_ID).collect(Collectors.toList()), PageRequest.of(0, employeesData.size()), false);
//        Assert.assertEquals(employeePage.getContent().size(), employeesData.size() - externalIds.length);
//
//        //findByIdNotInAndActiveIn
//        Long[] ids = {employeeList.get(0).getId(), employeeList.get(1).getId(), employeeList.get(2).getId()};
//        employeePage = employeeDAO.findByIdNotIn(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()),PageRequest.of(0, employeesData.size()), false);
//        Assert.assertEquals(employeePage.getContent().size(), employeesData.size() - ids.length);
//
//        //findByIdInAndActiveIn return as page
//        employeePage = employeeDAO.findByIdIn(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), PageRequest.of(0, employeesData.size()), false);
//        Assert.assertEquals(employeePage.getContent().size(), ids.length);
//
//        //findByIdInAndActiveIn return as list
//        employeeList1 = employeeDAO.findByIdIn(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), false);
//        Assert.assertEquals(employeeList1.size(), ids.length);
//
//        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size()), false).getTotalElements(), employeesData.size());
//        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size() - 1), false).getTotalElements(), employeesData.size());
//        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size() - 1), false).getContent().size(), employeesData.size() - 1);
//        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
//        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(1, employeesData.size() - 1), false).getContent().size(), 1);
//        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size() - 1), false).getTotalPages(), 2);
//
//        employeeDAO.deleteAll();
//
//        employeesData = new ArrayList<>();
//        employeesData.add(CollectionsUtil.toMap("externalId", "employee1", "firstName", "joseph", "lastName", "P", "active", "N"));
//        employeesData.add(CollectionsUtil.toMap("externalId", "employee2", "firstName", "sruthi", "lastName", "S", "active", "N"));
//        employeesData.add(CollectionsUtil.toMap("externalId", "employee3", "firstName", "arun", "lastName", "N", "active", "N"));
//        employeesData.add(CollectionsUtil.toMap("externalId", "employee4", "firstName", "anu", "lastName", "M", "active", "Y"));
//        employeesData.add(CollectionsUtil.toMap("externalId", "employee5", "firstName", "veena", "lastName", "C", "active", "Y"));
//        employeesData.add(CollectionsUtil.toMap("externalId", "employee6", "firstName", "thomas", "lastName", "C", "active", "Y"));
//        employeesData.add(CollectionsUtil.toMap("externalId", "employee7", "firstName", "anju", "lastName", "C", "active", "Y"));
//        employeesData.add(CollectionsUtil.toMap("externalId", "employee8", "firstName", "anitha", "lastName", "C", "active", "Y"));
//        employeesData.add(CollectionsUtil.toMap("externalId", "employee9", "firstName", "archana", "lastName", "C", "active", "Y"));
//
//        int[] activeCount = {0}, inactiveCount = {0};
//
//        employeesData.forEach(employeeData -> {
//            Employee employeeDTO = new Employee((String) employeeData.get("externalId"), (String) employeeData.get("firstName"), (String) employeeData.get("lastName"));
//            employeeDTO.setActive((String) employeeData.get("active"));
//            if("Y".equals(employeeData.get("active"))) {
//                activeCount[0] ++;
//            } else {
//                inactiveCount[0] ++;
//            }
//            employeeDAO.create(employeeDTO);
//        });
//        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size()), true).getTotalElements(), activeCount[0]);
//        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size()), false, true).getTotalElements(), inactiveCount[0]);
//        Assert.assertEquals(employeeDAO.findAll(PageRequest.of(0, employeesData.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
    }

    @After
    public void tearDown() throws Exception {
        employeeDAO.deleteAll();
    }
}*/
