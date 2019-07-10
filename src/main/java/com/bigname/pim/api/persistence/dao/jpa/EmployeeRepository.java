package com.bigname.pim.api.persistence.dao.jpa;

import com.bigname.pim.api.domain.Employee;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.hibernate.Criteria;

public interface EmployeeRepository extends GenericRepository<Employee, Criteria> {
}
