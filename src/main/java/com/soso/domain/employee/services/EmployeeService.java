package com.soso.domain.employee.services;

import com.soso.domain.employee.dao.EmployeeDAO;
import com.soso.domain.employee.dto.AttendanceHistoryDTO;
import com.soso.domain.employee.dto.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class EmployeeService {

    
    @Autowired
    private EmployeeDAO employeeDAO;

    
    public List<EmployeeDTO> getEmployeeList(int businessSeq) {
        
        return employeeDAO.selectEmployeeList(businessSeq);
    }

    
    public void registerEmployee(EmployeeDTO employee) {
        
        employeeDAO.insertEmployee(employee);
    }

    
    @Transactional
    public void processCheckIn(int employeeSeq) {
        
        EmployeeDTO emp = employeeDAO.selectEmployeeBySeq(employeeSeq);
        if (emp == null) {
            throw new RuntimeException("존재하지 않는 직원입니다.");
        }

        
        LocalDate today = LocalDate.now();
        String todayStr = today.toString(); 

        
        AttendanceHistoryDTO exist = employeeDAO.selectTodayAttendance(employeeSeq, todayStr);
        if (exist != null) {
            throw new RuntimeException("이미 오늘 출근 처리된 직원입니다.");
        }

        
        LocalTime now = LocalTime.now();
        String nowStr = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        
        String status = "정상";
        if (emp.getWorkStartTime() != null) {
            
            String startStr = emp.getWorkStartTime();
            if (startStr.length() == 5) startStr += ":00";
            
            
            LocalTime scheduledStart = LocalTime.parse(startStr);
            
            
            if (now.isAfter(scheduledStart.plusMinutes(1))) {
                status = "지각";
            }
        }

        
        AttendanceHistoryDTO attendance = new AttendanceHistoryDTO();
        attendance.setEmployeeSeq(employeeSeq);
        attendance.setWorkDate(todayStr);
        attendance.setActualStartTime(nowStr);
        attendance.setAttendanceStatus(status);
        
        attendance.setMemo(status.equals("지각") ? "정해진 시간보다 늦게 출근함" : null);

        employeeDAO.insertAttendance(attendance);
        
        
        employeeDAO.updateEmployeeStatus(employeeSeq, "WORK");
    }

    
    @Transactional
    public void processCheckOut(int employeeSeq) {
        
        EmployeeDTO emp = employeeDAO.selectEmployeeBySeq(employeeSeq);
        if (emp == null) {
            throw new RuntimeException("존재하지 않는 직원입니다.");
        }

        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        
        AttendanceHistoryDTO exist = employeeDAO.selectTodayAttendance(employeeSeq, todayStr);
        if (exist == null) {
            throw new RuntimeException("출근 기록이 없어 퇴근 처리를 할 수 없습니다.");
        }

        
        if (exist.getActualEndTime() != null) {
            throw new RuntimeException("이미 퇴근 처리 완료된 직원입니다.");
        }

        
        LocalTime now = LocalTime.now();
        String nowStr = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        
        
        String status = exist.getAttendanceStatus();
        if (emp.getWorkEndTime() != null && "정상".equals(status)) {
            String endStr = emp.getWorkEndTime();
            if (endStr.length() == 5) endStr += ":00";
            
            
            LocalTime scheduledEnd = LocalTime.parse(endStr);
            
            
            if (now.isBefore(scheduledEnd)) {
                status = "조퇴";
            }
        }

        
        exist.setActualEndTime(nowStr);
        exist.setAttendanceStatus(status);
        exist.setMemo(status.equals("조퇴") ? "정해진 시간보다 일찍 퇴근함" : exist.getMemo());

        employeeDAO.updateAttendanceCheckOut(exist);
        
        
        employeeDAO.updateEmployeeStatus(employeeSeq, "LEAVE");
    }

    
    public List<AttendanceHistoryDTO> getAttendanceHistory(int employeeSeq, String yearMonth) {
        
        return employeeDAO.selectAttendanceHistoryList(employeeSeq, yearMonth);
    }
}
