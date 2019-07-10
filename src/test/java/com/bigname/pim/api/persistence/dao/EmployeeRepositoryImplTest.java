package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Employee;
import com.bigname.pim.api.persistence.dao.jpa.EmployeeDAO;
import com.bigname.pim.api.persistence.dao.mongo.CategoryDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class EmployeeRepositoryImplTest {

   /* @PersistenceContext
    protected EntityManager entityManager;*/
    @Autowired
    private EmployeeDAO employeeDAO;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void crudTest() {
        Employee employee = new Employee("Samuel", "Joseph", "Wurzelbacher");
        employeeDAO.create(employee);
        /*EntityManagerFactory emf = Persistence.createEntityManagerFactory("emp");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(employee);
        em.getTransaction().commit();
        em.close();
        emf.close();*/

        /*Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put("employeeId", "emp1");
        employeeDAO.findOne(employeeMap);*/
    }

    @After
    public void tearDown() throws Exception {
    }
}