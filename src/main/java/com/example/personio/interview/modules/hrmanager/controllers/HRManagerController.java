package com.example.personio.interview.modules.hrmanager.controllers;

import com.example.personio.interview.modules.hrmanager.models.EmployeeSkipManager;
import com.example.personio.interview.modules.hrmanager.services.HRManagerService;
import com.example.personio.interview.utils.httputils.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HRManagerController 
{
    private HRManagerService hrManagerService;
    @Autowired
    public HRManagerController(HRManagerService hrManagerService) {
        this.hrManagerService = hrManagerService;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/employees")
    @ResponseStatus(code = HttpStatus.OK)
    public String getEmployeesHierarchy() {
        return hrManagerService.getEmployeesHierarchy();
    }

    @GetMapping("/employees/{empName}/manager")
    @ResponseStatus(code = HttpStatus.OK)
    public EmployeeSkipManager getEmployeeManagerAndSkipManager(@PathVariable("empName") String empName) {
       return hrManagerService.getEmployeeManagerAndSkipManager(empName);
        
    }

    @PostMapping("/employees")
    @ResponseStatus(code = HttpStatus.CREATED)

    public ResponseEntity<?> createEmployees(@RequestBody String employeesJson) {
        Response response = hrManagerService.saveEmployees(employeesJson);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}