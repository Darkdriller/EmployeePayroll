package com.bridgelabz.javafileio.entity;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Dhruv
 * @project EmployeePayroll
 */
public class EmployeePayrollDBService {

    // Singleton instance
    private static EmployeePayrollDBService instance;

    // Cached PreparedStatement
    private static PreparedStatement preparedStatement;

    // Private constructor for Singleton
    public EmployeePayrollDBService() {
        // Initialize the database connection and PreparedStatement
        initializeDatabase();
    }

    // Method to get the singleton instance
    public static EmployeePayrollDBService getInstance() {
        if (instance == null) {
            instance = new EmployeePayrollDBService();
        }
        return instance;
    }
    private void initializeDatabase() {
        try {
            Connection connection = this.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM employee_payroll WHERE name = ?");
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }
    public List<EmployeePayrollData> readData() throws SQLException {
        String sql = "SELECT * FROM employee_payroll;";
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM employee_payroll");
            // Populate EmployeePayroll object and add to the list

            while (resultSet.next()) {
            EmployeePayrollData employeePayroll = new EmployeePayrollData();
            employeePayroll.setId(resultSet.getInt("id"));
            employeePayroll.setName(resultSet.getString("name"));
            employeePayroll.setPhoneNumber(resultSet.getString("phone_number"));
            employeePayroll.setAddress(resultSet.getString("address"));
            employeePayroll.setDepartment(resultSet.getString("department"));
            employeePayroll.setBasicPay(resultSet.getInt("basic_pay"));
            employeePayroll.setDeductions(resultSet.getDouble("deductions"));
            employeePayroll.setTaxablePay(resultSet.getDouble("taxable_pay"));
            employeePayroll.setTax(resultSet.getDouble("tax"));
            employeePayroll.setNetPay(resultSet.getDouble("net_pay"));
            employeePayroll.setStartDate(resultSet.getString("start"));
            employeePayroll.setGender(resultSet.getString("gender"));
            employeePayrollList.add(employeePayroll);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    // Retrieve employee details from the database
    public EmployeePayrollData retrieveEmployeeDetails(String employeeName) throws EmployeePayrollException {
        try {
            // Set the name parameter in the PreparedStatement
            preparedStatement.setString(1, employeeName);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if employee exists
            if (!resultSet.next()) {
                throw new EmployeePayrollException("Employee not found: " + employeeName);
            }

            // Reuse the ResultSet to populate the EmployeePayroll object
            EmployeePayrollData employeePayroll = new EmployeePayrollData();
            employeePayroll.setId(resultSet.getInt("id"));
            employeePayroll.setName(resultSet.getString("name"));
            employeePayroll.setPhoneNumber(resultSet.getString("phone_number"));
            employeePayroll.setAddress(resultSet.getString("address"));
            employeePayroll.setDepartment(resultSet.getString("department"));
            employeePayroll.setBasicPay(resultSet.getInt("basic_pay"));
            employeePayroll.setDeductions(resultSet.getDouble("deductions"));
            employeePayroll.setTaxablePay(resultSet.getDouble("taxable_pay"));
            employeePayroll.setTax(resultSet.getDouble("tax"));
            employeePayroll.setNetPay(resultSet.getDouble("net_pay"));
            employeePayroll.setStartDate(resultSet.getString("start"));
            employeePayroll.setGender(resultSet.getString("gender"));

            return employeePayroll;
        } catch (SQLException e) {
            throw new EmployeePayrollException("Error retrieving employee payroll data", e);
        }
    }

    //UC3
    public void updateEmployeeSalary(String employeeName, double newSalary) throws EmployeePayrollException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = this.getConnection();
            // Update salary in the database
            String updateQuery = "UPDATE employee_payroll SET basic_pay = ? WHERE name = ?";
            preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setDouble(1, newSalary);
            preparedStatement.setString(2, employeeName);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new EmployeePayrollException("Employee not found: " + employeeName);
            }
            System.out.println("\nEmployee "+ employeeName+" salary updated successfully" );

            List<EmployeePayrollData> payrollData = readData();
            for (EmployeePayrollData it : payrollData){
                if(it.getName().equals(employeeName))
                    System.out.println(it);
            }

        } catch (SQLException e) {
            throw new EmployeePayrollException("Error updating employee salary", e);
        } finally {
            // Close resources in the finally block
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Connection getConnection() throws SQLException{
        String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?allowPublicKeyRetrieval=true&useSSL=false";
        String userName = "root";
        String password = "toor";
        Connection con;
        System.out.println("Connecting to Database: " + jdbcURL);
        con = DriverManager.getConnection(jdbcURL, userName,password);
        System.out.println("Connection Successfull !" + con);
        return con;
    }
}
