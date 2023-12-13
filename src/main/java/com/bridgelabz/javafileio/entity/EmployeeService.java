package com.bridgelabz.javafileio.entity;

import java.io.IOException;

public class EmployeeService {

    public void run() throws IOException {
        System.out.println("Employee Payroll Service");

        //create an instance of EmployeePayrollData
        EmployeePayrollData employee = new EmployeePayrollData();

        employee.readFromConsole();

        System.out.println("\nEmployee Info: ");

        employee.writeToConsole();

    }
}
