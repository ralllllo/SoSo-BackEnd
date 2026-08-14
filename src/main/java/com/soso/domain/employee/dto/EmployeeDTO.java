package com.soso.domain.employee.dto;


public class EmployeeDTO {
    
    
    private int employeeSeq;
    
    
    private int businessSeq;
    
    
    private int storeSeq;
    
    
    private String empName;
    
    
    private String phone;
    
    
    private String workStartTime;
    
    
    private String workEndTime;
    
    
    private String status;

    
    public EmployeeDTO() {}

    
    public int getEmployeeSeq() { return employeeSeq; }
    
    
    public void setEmployeeSeq(int employeeSeq) { this.employeeSeq = employeeSeq; }
    
    
    public int getBusinessSeq() { return businessSeq; }
    
    
    public void setBusinessSeq(int businessSeq) { this.businessSeq = businessSeq; }
    
    
    public int getStoreSeq() { return storeSeq; }
    
    
    public void setStoreSeq(int storeSeq) { this.storeSeq = storeSeq; }
    
    
    public String getEmpName() { return empName; }
    
    
    public void setEmpName(String empName) { this.empName = empName; }
    
    
    public String getPhone() { return phone; }
    
    
    public void setPhone(String phone) { this.phone = phone; }
    
    
    public String getWorkStartTime() { return workStartTime; }
    
    
    public void setWorkStartTime(String workStartTime) { this.workStartTime = workStartTime; }
    
    
    public String getWorkEndTime() { return workEndTime; }
    
    
    public void setWorkEndTime(String workEndTime) { this.workEndTime = workEndTime; }
    
    
    public String getStatus() { return status; }
    
    
    public void setStatus(String status) { this.status = status; }
}
