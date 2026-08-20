package com.soso.domain.employee.dto;


public class AttendanceHistoryDTO {
    
    
    private int attendanceSeq;
    
    
    private int employeeSeq;
    
    
    private String workDate;
    
    
    private String actualStartTime;
    
    
    private String actualEndTime;
    
    
    private String attendanceStatus;
    
    
    private String memo;
    
    
    private String createdAt;
    
    
    private String empName;

    
    public AttendanceHistoryDTO() {}

    
    public int getAttendanceSeq() { return attendanceSeq; }
    
    
    public void setAttendanceSeq(int attendanceSeq) { this.attendanceSeq = attendanceSeq; }
    
    
    public int getEmployeeSeq() { return employeeSeq; }
    
    
    public void setEmployeeSeq(int employeeSeq) { this.employeeSeq = employeeSeq; }
    
    
    public String getWorkDate() { return workDate; }
    
    
    public void setWorkDate(String workDate) { this.workDate = workDate; }
    
    
    public String getActualStartTime() { return actualStartTime; }
    
    
    public void setActualStartTime(String actualStartTime) { this.actualStartTime = actualStartTime; }
    
    
    public String getActualEndTime() { return actualEndTime; }
    
    
    public void setActualEndTime(String actualEndTime) { this.actualEndTime = actualEndTime; }
    
    
    public String getAttendanceStatus() { return attendanceStatus; }
    
    
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
    
    
    public String getMemo() { return memo; }
    
    
    public void setMemo(String memo) { this.memo = memo; }
    
    
    public String getCreatedAt() { return createdAt; }
    
    
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    
    public String getEmpName() { return empName; }
    
    
    public void setEmpName(String empName) { this.empName = empName; }
}
