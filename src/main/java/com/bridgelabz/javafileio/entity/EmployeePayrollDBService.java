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
            EmployeePayrollData employeePayroll =  mapResultSetToEmployeePayroll(resultSet);
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
            return  mapResultSetToEmployeePayroll(resultSet);
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

    // UC-5: Retrieve employees who have joined in a particular date range
    public List<EmployeePayrollData> retrieveEmployeesByDateRange(String startDate, String endDate) throws EmployeePayrollException {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();

        try {
            Connection connection = getConnection();
            String query = "SELECT * FROM employee_payroll WHERE start BETWEEN ? AND ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, startDate);
            preparedStatement.setString(2, endDate);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                EmployeePayrollData employeePayroll = mapResultSetToEmployeePayroll(resultSet);
                employeePayrollList.add(employeePayroll);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new EmployeePayrollException("Error retrieving employees by date range", e);
        }

        return employeePayrollList;
    }

    // UC-6: Ability to find sum, average, min, max and number of male and female employees
    public void doEmployeeStatistics() throws EmployeePayrollException {
        try {
            Connection connection = getConnection();
            String query = "SELECT gender, COUNT(id) as count, " +
                    "SUM(basic_pay) as sum_salary, AVG(basic_pay) as avg_salary, " +
                    "MIN(basic_pay) as min_salary, MAX(basic_pay) as max_salary " +
                    "FROM employee_payroll GROUP BY gender";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("\n Gender"+"\t"+"Number of Employees"+"\t"+"Total Salary"+"\t"+"Average Salary"+"\t"+"\"Minimum Salary"+"\t"+"Maximum Salary");

            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                int count = resultSet.getInt("count");
                double sumSalary = resultSet.getDouble("sum_salary");
                double avgSalary = resultSet.getDouble("avg_salary");
                double minSalary = resultSet.getDouble("min_salary");
                double maxSalary = resultSet.getDouble("max_salary");

                System.out.println(gender+"\t\t"+count+"\t\t"+sumSalary+"\t\t"+avgSalary+"\t\t"+minSalary+"\t\t"+maxSalary);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new EmployeePayrollException("Error performing statistics", e);
        }
    }

    private EmployeePayrollData mapResultSetToEmployeePayroll(ResultSet resultSet) throws SQLException {
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
    }
}
