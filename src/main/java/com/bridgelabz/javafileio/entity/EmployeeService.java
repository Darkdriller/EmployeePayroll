package com.bridgelabz.javafileio.entity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    public enum IOService {CONSOLE_IO, FILE_IO, DB_IO, REST_IO}

    public List<EmployeePayrollData> employeePayrollList;

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) throws SQLException {
        if(ioService.equals(IOService.DB_IO)){
                this.employeePayrollList = new EmployeePayrollDBService().readData();
        }
        return employeePayrollList;
    }
}
