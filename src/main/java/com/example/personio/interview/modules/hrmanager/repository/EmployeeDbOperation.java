package com.example.personio.interview.modules.hrmanager.repository;

import java.util.Date;
import java.util.List;

import com.example.personio.interview.modules.hrmanager.models.Employee;
import com.example.personio.interview.modules.hrmanager.models.EmployeeManagerDbModel;
import com.example.personio.interview.modules.hrmanager.models.EmployeeSkipManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDbOperation implements IEmployeeDbOperation{

    public EmployeeRepository employeeRepository;
    
    @Autowired
    public EmployeeDbOperation(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeByName(String empName) {
        
        List<Employee> empList = employeeRepository.findByEmpName(empName);
        if(empList == null || empList.size() == 0) {
            return null;
        }
        return empList.get(0);
    }

    public int saveEmployee(String empName) {
        Employee emp =  new Employee(empName);
        return employeeRepository.save(emp).getEmpId();
    }

    public int saveEmployee(String empName, int managerId) {
        Employee emp =  new Employee(empName, managerId);
        return employeeRepository.save(emp).getEmpId();
    }

    public int updateEmployeeManager(Employee emp, int managerId) {
        emp.setManagerId(managerId);
        emp.setUpdatedAt(new Date());
        return employeeRepository.save(emp).getEmpId();

    }

    @Override
    public List<EmployeeManagerDbModel> getAllManagerSubordinates() {
        return employeeRepository.getAllManagerSubordinates();
    }

    @Override
    public Employee getTopHierarchyEmployee() {
        List<Employee> empList = employeeRepository.getEmployeeWithManagerIdNull();
        if (empList != null && empList.size() > 0)
            return empList.get(0);
        return null;
    }

    @Override
    public EmployeeSkipManager getEmployeeManagerAndSkipManager(String empName) {
        List<EmployeeSkipManager> empSkipManager = employeeRepository.getEmployeeManagerAndSkipManager(empName);
        if (empSkipManager != null && empSkipManager.size() > 0)
            return empSkipManager.get(0);
       return null;
    }
}
