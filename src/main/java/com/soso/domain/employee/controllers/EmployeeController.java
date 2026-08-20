package com.soso.domain.employee.controllers;

import com.soso.domain.employee.dto.AttendanceHistoryDTO;
import com.soso.domain.employee.dto.EmployeeDTO;
import com.soso.domain.employee.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    
    @Autowired
    private EmployeeService employeeService;

    
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getEmployeeList(@RequestParam int storeSeq) {
        List<EmployeeDTO> list = employeeService.getEmployeeList(storeSeq);
        return ResponseEntity.ok(list);
    }

    
    @PostMapping
    public ResponseEntity<String> registerEmployee(@RequestBody EmployeeDTO employee) {
        try {
            employeeService.registerEmployee(employee);
            return ResponseEntity.ok("직원 등록이 완료되었습니다.");
        } catch (Exception e) {
            
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @PostMapping("/{employeeSeq}/check-in")
    public ResponseEntity<String> checkIn(@PathVariable int employeeSeq) {
        try {
            employeeService.processCheckIn(employeeSeq);
            return ResponseEntity.ok("출근 처리가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @PostMapping("/{employeeSeq}/check-out")
    public ResponseEntity<String> checkOut(@PathVariable int employeeSeq) {
        try {
            employeeService.processCheckOut(employeeSeq);
            return ResponseEntity.ok("퇴근 처리가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @GetMapping("/{employeeSeq}/attendance")
    public ResponseEntity<List<AttendanceHistoryDTO>> getAttendanceHistory(
            @PathVariable int employeeSeq,
            @RequestParam String yearMonth) {
        List<AttendanceHistoryDTO> list = employeeService.getAttendanceHistory(employeeSeq, yearMonth);
        return ResponseEntity.ok(list);
    }
}
