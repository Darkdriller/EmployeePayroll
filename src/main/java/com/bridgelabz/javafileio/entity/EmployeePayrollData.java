package com.bridgelabz.javafileio.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EmployeePayrollData {


    private int  id;
    private String employeeName;
    private long salary;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public void readFromConsole() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter the Employee ID: ");
        id = Integer.parseInt(reader.readLine());

        System.out.println("Enter the Employee Name: ");
        employeeName = reader.readLine();

        System.out.println("Enter the Employee Salary: ");
        salary = Long.parseLong(reader.readLine());
    }

    public void writeToConsole(){
        System.out.println("Employee id: " + id);
        System.out.println("Employee Name: " + employeeName);
        System.out.println("Employee Salary: " + salary);
    }
}
