package com.example.personio.interview.modules.hrmanager.services;

import java.util.List;
import java.util.Map;

import com.example.personio.interview.exceptions.PersonioBadRequestException;
import com.example.personio.interview.exceptions.UnableToFetchDataFromDbException;
import com.example.personio.interview.exceptions.UnableToSaveInDbException;
import com.example.personio.interview.modules.hrmanager.models.Employee;
import com.example.personio.interview.modules.hrmanager.models.EmployeeManagerDbModel;
import com.example.personio.interview.modules.hrmanager.models.EmployeeSkipManager;
import com.example.personio.interview.modules.hrmanager.models.processors.EmployeeProcessor;
import com.example.personio.interview.modules.hrmanager.repository.EmployeeDbOperation;
import com.example.personio.interview.utils.httputils.Response;
import com.example.personio.interview.utils.httputils.ResponseGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HRManagerService 
{
    public static final String EMPTY_EMPLOYEE_NAME = "Employee Name parameter (empName) is empty or null in the Request";
    public static final String UNABLE_TO_SAVE = "Unable to save employee hierarchy in Datbase";
    private static final String UNABLE_TO_FETCH_DATA_FROM_DB = "Unable to fetch data from Database ";

    private EmployeeDbOperation employeeDbOperation;
    private EmployeeProcessor processor;
    private ResponseGenerator responseGenerator;

    @Autowired
    public HRManagerService(EmployeeDbOperation employeeDbOperation, EmployeeProcessor processor,
            ResponseGenerator responseGenerator) {
        this.employeeDbOperation = employeeDbOperation;
        this.processor = processor;
        this.responseGenerator = responseGenerator;
    }

    public String getEmployeesHierarchy() {
        return getEmployeesJsonHierarchy();
    }

    public EmployeeSkipManager getEmployeeManagerAndSkipManager(String empName){
        if (empName == null || empName.isEmpty()) {
            throw new PersonioBadRequestException(
                new Exception(EMPTY_EMPLOYEE_NAME), null, EMPTY_EMPLOYEE_NAME);
        }
        return employeeDbOperation.getEmployeeManagerAndSkipManager(empName);
    }

    public Response saveEmployees(String employeesJson) {
        processor.checkForMultipleRoots(employeesJson);
        Map<String,String> empManagerMap = processor.createEmpManagerMap(employeesJson);
        processor.checkForLoops(empManagerMap);
        saveEmployees(empManagerMap);
        return responseGenerator.successResponse(HttpStatus.CREATED);
    }

    private String getEmployeesJsonHierarchy() {
        try {
            Employee topManager = getTopHierarchyEmployee();
            if(topManager == null) {
                return "";
            }
            List<String> empNames = getAllEmployeesNames();
            Map<String,List<String>> empManagerMap = getEmployeeManagerMap();
            return processor.createEmployeeJsonHierarchy(topManager,empNames,empManagerMap);
        } catch (Exception  ex) {
            throw new UnableToFetchDataFromDbException(ex, null, UNABLE_TO_FETCH_DATA_FROM_DB);
        }
    }

    private Employee getTopHierarchyEmployee() {
        return employeeDbOperation.getTopHierarchyEmployee();
    }

    private List<String> getAllEmployeesNames() {
        List<Employee> empList = employeeDbOperation.getAllEmployees();
        return processor.getAllEmployeesNames(empList);
    }

    private Map<String, List<String>> getEmployeeManagerMap() {
        List<EmployeeManagerDbModel> managerSubordinateList = employeeDbOperation.getAllManagerSubordinates();
        return processor.getEmployeeManagerMap(managerSubordinateList);
    }

    private void saveEmployees(Map<String, String> empManagerMap) {
        empManagerMap.forEach((employee,manager) -> saveEmployee(employee,manager));
    }

    private void saveEmployee(String empName, String managerName) {
        int managerId = 0;

        try {
            Employee emp = employeeDbOperation.getEmployeeByName(empName);
            Employee manager = employeeDbOperation.getEmployeeByName(managerName);
            if(manager == null) {
                managerId = employeeDbOperation.saveEmployee(managerName);
            } else {
                managerId = manager.getEmpId();
            }
            
            if(emp == null) {
                employeeDbOperation.saveEmployee(empName,managerId);
            } else {
                employeeDbOperation.updateEmployeeManager(emp,managerId);
            }
        } catch (Exception ex) {
            throw new UnableToSaveInDbException(ex, null, UNABLE_TO_SAVE);
        }
    }
}