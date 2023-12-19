package com.bridgelabz.javafileio.entity;

public class EmployeePayrollData{

    private int id;
    private String name;
    private String phoneNumber;
    private String address;
    private String department;
    private int basicPay;
    private double deductions;
    private double taxablePay;
    private double tax;
    private double netPay;
    private String startDate;
    private char gender;


    public EmployeePayrollData() {
        // Default constructor
    }

    public EmployeePayrollData(int id, String name, String phoneNumber, String address, String department,
                           int basicPay, double deductions, double taxablePay, double tax,
                           double netPay, String startDate, char gender) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.department = department;
        this.basicPay = basicPay;
        this.deductions = deductions;
        this.taxablePay = taxablePay;
        this.tax = tax;
        this.netPay = netPay;
        this.startDate = startDate;
        this.gender = gender;
    }

    // Getters and setters...

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBasicPay(int bp) {
        this.basicPay = bp;
    }

    public void setPhoneNumber(String num) {
        this.phoneNumber = num;
    }

    public void setTax(Double t) {
        this.tax = t;
    }

    public void setTaxablePay(Double t) {
        this.taxablePay = t;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setGender(String gender) {
        if (gender != null)
            this.gender = gender.charAt(0);

    }

    public void setNetPay(Double t) {
        this.netPay = t;
    }

    public void setDeductions(Double t) {
        this.deductions = t;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", department='" + department + '\'' +
                ", basicPay=" + basicPay +
                ", deductions=" + deductions +
                ", taxablePay=" + taxablePay +
                ", tax=" + tax +
                ", netPay=" + netPay +
                ", start=" + startDate +
                ", gender='" + gender + '\'' +
                '}';
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getDepartment() {
        return department;
    }

    public int getBasicPay() {
        return basicPay;
    }

    public double getDeductions() {
        return deductions;
    }

    public double getTaxablePay() {
        return taxablePay;
    }

    public double getTax() {
        return tax;
    }

    public double getNetPay() {
        return netPay;
    }

    public String getGender() {
        return String.valueOf(gender);
    }
}