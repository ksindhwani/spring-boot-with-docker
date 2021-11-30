package com.example.personio.interview.modules.hrmanager.models;

public class EmployeeSkipManager {
    private String supervisor;
    private String skipSupervisor;

    public EmployeeSkipManager() {}

    public EmployeeSkipManager(String supervisor, String skipSupervisor) {
        this.supervisor = supervisor;
        this.skipSupervisor = skipSupervisor;
    }
    
    public String getSupervisor() {
        return supervisor;
    }
    
    public String getSkipSupervisor() {
        return skipSupervisor;
    }
    
}
