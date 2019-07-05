package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Employee;
import com.m7.xtreme.xcore.persistence.dao.jpa.GenericDAO;


public interface EmployeeDAO extends GenericDAO<Employee>, EmployeeRepository {
}
