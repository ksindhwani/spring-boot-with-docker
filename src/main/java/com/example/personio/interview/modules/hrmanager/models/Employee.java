package com.example.personio.interview.modules.hrmanager.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "employee")
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},allowGetters = true)
public class Employee implements Serializable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Integer empId;

    @Column(name = "emp_name")
    private String empName;

    @Column(name = "manager_id", nullable = true)
    private Integer managerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @CreationTimestamp
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    public Employee(){}
    public Employee(String empName)  {
        this.empName = empName;
    }

    public Employee(String empName, int managerId) {
        this.empName = empName;
        this.managerId = managerId;
    }

    public Integer getEmpId() {
        return empId;
    }
    public void setEmpId(Integer empId) {
        this.empId = empId;
    }
    public String getEmpName() {
        return empName;
    }
    public void setEmpName(String empName) {
        this.empName = empName;
    }
    public Integer getManagerId() {
        return managerId;
    }
    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}