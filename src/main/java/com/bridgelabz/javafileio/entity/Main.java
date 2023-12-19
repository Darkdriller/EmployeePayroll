package com.bridgelabz.javafileio.entity;

import java.sql.SQLException;
import java.util.*;

public class Main{
    public static void main(String[] args) throws SQLException {

        EmployeeService payrollService = new EmployeeService();

        List<EmployeePayrollData> payrollData = payrollService.readEmployeePayrollData(EmployeeService.IOService.DB_IO);
        for (EmployeePayrollData emp : payrollData)
            System.out.println(emp);

        try {
            // Update the salary for Employee Merissa
            payrollService.updateEmployeeSalary("Merrissa", 3000000.00, EmployeeService.IOService.DB_IO);

        } catch (EmployeePayrollException e) {
            e.printStackTrace();
        }
    }

}
