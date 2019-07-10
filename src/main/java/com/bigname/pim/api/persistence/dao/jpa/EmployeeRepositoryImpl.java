package com.bigname.pim.api.persistence.dao.jpa;

import com.bigname.pim.api.domain.Employee;
import com.m7.xtreme.xcore.persistence.dao.jpa.GenericRepositoryImpl;
import org.hibernate.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;

public class EmployeeRepositoryImpl extends GenericRepositoryImpl<Employee, Criteria> implements EmployeeRepository {
    public EmployeeRepositoryImpl(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        super(jdbcTemplate, Employee.class, entityManager);
    }
}
