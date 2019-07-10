package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Employee;
import com.bigname.pim.api.persistence.dao.jpa.EmployeeDAO;
import com.bigname.pim.api.persistence.dao.mongo.CategoryDAO;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PimUtil;
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
    public void crudTest() {
        List<Map<String, Object>> employeesData = new ArrayList<>();
        employeesData.add(CollectionsUtil.toMap("employeeId", "employee1", "firstName", "joseph", "lastName", "S"));
        employeesData.add(CollectionsUtil.toMap("employeeId", "employee2", "firstName", "sruthi", "lastName", "S"));
        employeesData.forEach(employeeData -> {
            Employee employee = new Employee((String)employeeData.get("employeeId"), (String) employeeData.get("firstName"), (String)employeeData.get("lastName"));
            Employee employee1 = employeeDAO.create(employee);
                });

        //findById
        Employee employee_1 = employeeDAO.findById(ID.EXTERNAL_ID(employeesData.get(0).get("employeeId"))).orElse(null);
        Assert.assertEquals(employeesData.get(0).get("employeeId"), employee_1.getEmployeeId());

         //find all Employees as list
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("externalId", "employee1");
        Assert.assertEquals(employeesData.get(0).get("employeeId"), employeeDAO.findAll(criteria).get(0).getEmployeeId());

        //findOne
        Map<String, Object> input = new HashMap<>();
        criteria.put("firstName", "sruthi");
        Assert.assertEquals(employeesData.get(1).get("firstName"), employeeDAO.findOne(input).orElse(null).getFirstName());

        //find all Employees as Page
       // Page<Employee> employees = employeeDAO.findAll("firstName", "sruthi", PageRequest.of(0, 10 ,null), false);

        //findByActiveIn return as list
        Assert.assertEquals(employeesData.size(), employeeDAO.findByActiveIn(PimUtil.getActiveOptions(false)).size());

        //findByActiveIn return as page
        Assert.assertEquals(employeesData.size(), employeeDAO.findByActiveIn(PimUtil.getActiveOptions(false), PageRequest.of(1, employeesData.size(), Sort.by("firstName"))).getSize());

        //findByIdNotInAndActiveIn
        String[] ids = {employeesData.get(0).get("employeeId").toString(), employeesData.get(1).get("employeeId").toString()};
        List<ID> ids1 = Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList());
        //ids1.add(ID.EXTERNAL_ID(employeesData.get(0).get("employeeId")));
       // employeeDAO.findByIdNotInAndActiveIn(ids1.stream().map(e -> e.getId()).collect(Collectors.toList()), PimUtil.getActiveOptions(false), PageRequest.of(1, employeesData.size(), Sort.by("firstName")));

        //findByExternalIdNotInAndActiveIn
        // employeeDAO.findByExternalIdNotInAndActiveIn(ids1.stream().map(e -> e.getId()).collect(Collectors.toList()), PimUtil.getActiveOptions(false), PageRequest.of(1, employeesData.size(), Sort.by("firstName")));

        //findByIdInAndActiveIn
        //employeeDAO.findByIdInAndActiveIn(ids1.stream().map(e -> e.getId()).collect(Collectors.toList()), PimUtil.getActiveOptions(false), PageRequest.of(1, employeesData.size(), Sort.by("firstName")));

        //findByIdInAndActiveIn (list)
        //employeeDAO.findByIdInAndActiveIn(ids1.stream().map(e -> e.getId()).collect(Collectors.toList()), PimUtil.getActiveOptions(false));

        //findByExternalIdInAndActiveIn
        //employeeDAO.findByExternalIdInAndActiveIn(ids1.stream().map(e -> e.getId()).collect(Collectors.toList()), PimUtil.getActiveOptions(false), PageRequest.of(1, employeesData.size(), Sort.by("firstName")));

        //findByExternalIdInAndActiveIn (list)
       // employeeDAO.findByExternalIdInAndActiveIn(ids1.stream().map(e -> e.getId()).collect(Collectors.toList()), PimUtil.getActiveOptions(false));

        //findByExternalId
       // employeeDAO.findByExternalId(ID.EXTERNAL_ID(employeesData.get(0).get("employeeId")));

        //findByExternalIdStartingWith
        //employeeDAO.findByExternalIdStartingWith(ID.EXTERNAL_ID(employeesData.get(0).get("employeeId")));

        //findByIdAndActiveIn
       // employeeDAO.findByIdAndActiveIn(ID.EXTERNAL_ID(employeesData.get(0).get("employeeId")), PimUtil.getActiveOptions(false));

        //findByExternalIdAndActiveIn
       // employeeDAO.findByExternalIdAndActiveIn(ID.EXTERNAL_ID(employeesData.get(0).get("employeeId")), PimUtil.getActiveOptions(false));
    }

    @After
    public void tearDown() throws Exception {
     employeeDAO.deleteAll();
    }
}