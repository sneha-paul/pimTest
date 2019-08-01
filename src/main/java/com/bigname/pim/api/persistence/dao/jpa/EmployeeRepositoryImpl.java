package com.bigname.pim.api.persistence.dao.jpa;

import com.bigname.pim.api.domain.Employee;
import com.m7.xtreme.xcore.persistence.dao.jpa.GenericRepositoryImpl;
import com.m7.xtreme.xcore.util.GenericCriteria;
import com.m7.xtreme.xcore.util.ID;
import org.hibernate.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmployeeRepositoryImpl extends GenericRepositoryImpl<Employee, Criteria> implements EmployeeRepository {
    public EmployeeRepositoryImpl(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        super(jdbcTemplate, Employee.class, entityManager);
    }
}
