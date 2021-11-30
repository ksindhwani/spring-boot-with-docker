package com.example.personio.interview.modules.hrmanager.repository;

import java.util.List;

import com.example.personio.interview.modules.hrmanager.models.Employee;
import com.example.personio.interview.modules.hrmanager.models.EmployeeManagerDbModel;
import com.example.personio.interview.modules.hrmanager.models.EmployeeSkipManager;

public interface IEmployeeDbOperation {
    public List<Employee> getAllEmployees();

    public Employee getTopHierarchyEmployee();
    public Employee getEmployeeByName(String empName);

    public List<EmployeeManagerDbModel> getAllManagerSubordinates();

    public EmployeeSkipManager getEmployeeManagerAndSkipManager(String empName);
}
