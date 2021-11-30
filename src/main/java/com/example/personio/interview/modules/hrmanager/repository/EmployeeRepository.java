package com.example.personio.interview.modules.hrmanager.repository;

import java.util.List;

import com.example.personio.interview.modules.hrmanager.models.Employee;
import com.example.personio.interview.modules.hrmanager.models.EmployeeManagerDbModel;
import com.example.personio.interview.modules.hrmanager.models.EmployeeSkipManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    public List<Employee> findByEmpName(String empName);

    @Query(value = "SELECT "
                + " new com.example.personio.interview.modules.hrmanager.models.EmployeeManagerDbModel(man.empName AS manager ,emp.empName AS suboridinate) "
                + " FROM Employee emp "
                + " INNER JOIN Employee man ON emp.managerId = man.empId")
    public List<EmployeeManagerDbModel> getAllManagerSubordinates();

    @Query(value = "SELECT * from Employee emp where emp.manager_id is NULL", nativeQuery = true)
    public List<Employee> getEmployeeWithManagerIdNull();

    @Query(value = "SELECT "
                + " new com.example.personio.interview.modules.hrmanager.models.EmployeeSkipManager(man.empName AS manager,skip_man.empName AS skip_manager) "
                + " FROM Employee emp "
                + " INNER JOIN Employee man ON emp.managerId = man.empId "
                + " INNER JOIN Employee skip_man ON man.managerId = skip_man.empId "
                + " WHERE emp.empName = ?1")
    public List<EmployeeSkipManager> getEmployeeManagerAndSkipManager(String empName);
}