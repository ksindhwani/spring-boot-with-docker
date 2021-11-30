package com.example.personio.interview.modules.hrmanager.models;

public class EmployeeManagerDbModel {

    private String manager;
    private String subordinate;

    public EmployeeManagerDbModel(){}

    public EmployeeManagerDbModel(String manager, String subordinate) {
        this.manager = manager;
        this.subordinate = subordinate;
    }

    public String getManager() {
        return manager;
    }

    public String getSubordinate() {
        return subordinate;
    }
}
