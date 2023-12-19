package com.bridgelabz.javafileio.entity;

import java.sql.SQLException;
import java.util.*;

public class Main{
    public static void main(String[] args) throws SQLException {


        EmployeePayrollDBService payrollService = EmployeePayrollDBService.getInstance();
        try {
            // UC-7: Add a new employee to the payroll using transactions
            EmployeePayrollData newEmployee = new EmployeePayrollData();
            newEmployee.setName("DJS");
            newEmployee.setPhoneNumber("12345678");
            newEmployee.setAddress("Vadodara");
            newEmployee.setDepartment("CSE");
            newEmployee.setBasicPay(90000);
            newEmployee.setDeductions(1000.0);
            newEmployee.setTaxablePay(45000.0);
            newEmployee.setTax(500.0);
            newEmployee.setNetPay(44500.0);
            newEmployee.setStartDate("2022-01-01");
            newEmployee.setGender("M");

            payrollService.addEmployeeToPayroll(newEmployee);
            System.out.println("New employee added successfully: ");

            for(EmployeePayrollData it:payrollService.readData()){
                System.out.println(it);
            }

        } catch (EmployeePayrollException e) {
            e.printStackTrace();
        }
    }
}
