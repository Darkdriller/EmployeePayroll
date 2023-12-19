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

    // UC-7: Ability to add a new employee to the payroll with transactions
    public void addEmployeeToPayroll(EmployeePayrollData employeePayroll) throws EmployeePayrollException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            insertIntoEmployeePayroll(employeePayroll, connection);

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new EmployeePayrollException("Error adding employee to payroll with transaction", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //returns employee ID
    private int insertIntoEmployeePayroll(EmployeePayrollData employeePayroll, Connection connection) throws SQLException {
        String insertQuery = "INSERT INTO employee_payroll (name, phone_number, address, department, " +
                "basic_pay, deductions, taxable_pay, tax, net_pay, start, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        preparedStatement = connection.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, employeePayroll.getName());
        preparedStatement.setString(2, employeePayroll.getPhoneNumber());
        preparedStatement.setString(3, employeePayroll.getAddress());
        preparedStatement.setString(4, employeePayroll.getDepartment());
        preparedStatement.setInt(5, employeePayroll.getBasicPay());
        preparedStatement.setDouble(6, employeePayroll.getDeductions());
        preparedStatement.setDouble(7, employeePayroll.getTaxablePay());
        preparedStatement.setDouble(8, employeePayroll.getTax());
        preparedStatement.setDouble(9, employeePayroll.getNetPay());
        preparedStatement.setString(10, employeePayroll.getStartDate());
        preparedStatement.setString(11, employeePayroll.getGender());

        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected == 0) {
            throw new SQLException("Adding employee to payroll failed, no rows affected.");
        }
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);  // Return the generated employee ID

            } else {
                throw new SQLException("Creating employee failed, no ID obtained.");
            }
        }

    }

    // UC-8: Add Payroll Details when a new Employee is added
    public void addEmployeeToPayrollDetails(EmployeePayrollData employeePayroll) throws EmployeePayrollException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false); // Turn off auto-commit


            // Insert into employee_payroll table
            int emp_id = insertIntoEmployeePayroll(employeePayroll,connection);
            employeePayroll.setId(emp_id);
            // Insert into payroll_details table
            insertIntoPayrollDetails(connection, employeePayroll);

            // Commit the transaction if both inserts are successful
            connection.commit();

        } catch (SQLException e) {
            // Roll back the transaction if an exception occurs
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new EmployeePayrollException("Error adding employee to payroll", e);
        } finally {
            // Set back to auto-commit mode and close the connection
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //inserting all stats in payroll_details table
    private void insertIntoPayrollDetails(Connection connection, EmployeePayrollData employeePayroll) throws SQLException {
        // Calculate derived fields
        double deductions = employeePayroll.getBasicPay() * 0.2;
        double taxablePay = employeePayroll.getBasicPay() - deductions;
        double tax = taxablePay * 0.1;
        double netPay = employeePayroll.getBasicPay() - tax;

        String insertQuery = "INSERT INTO payroll_details (employee_id, deductions, taxable_pay, tax, net_pay) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

        // Set parameters for payroll_details table
        preparedStatement.setInt(1, employeePayroll.getId());
        preparedStatement.setDouble(2, deductions);
        preparedStatement.setDouble(3, taxablePay);
        preparedStatement.setDouble(4, tax);
        preparedStatement.setDouble(5, netPay);
        System.out.println(preparedStatement);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    //display payroll details table in console
    public void display_payroll_details(){
        try {
            // Assuming you have a connection to your database
            Connection connection = getConnection();

            // Assuming you have a Statement to execute the query
            Statement statement = connection.createStatement();

            // Execute the query to fetch all records from payroll_service table
            ResultSet resultSet = statement.executeQuery("SELECT * FROM payroll_details");

            // Iterate through the result set and display each record
            System.out.println("\npayroll_details db is as follows: ");
            while (resultSet.next()) {
                // Assuming you have appropriate column names, update them accordingly
                int column1 = resultSet.getInt(1);
                double column2 = resultSet.getDouble(2);
                double column3 = resultSet.getDouble(3);
                double column4 = resultSet.getDouble(4);
                double column5 = resultSet.getDouble(5);


                // Display the retrieved data
                System.out.println(column1+"\t"+column2+"\t"+column3+"\t"+column4+"\t"+column5);
            }

            // Close resources
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
