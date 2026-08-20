package com.soso.domain.employee.dao;

import com.soso.domain.employee.dto.AttendanceHistoryDTO;
import com.soso.domain.employee.dto.EmployeeDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class EmployeeDAO {

    
    @Autowired
    private SqlSessionTemplate sqlSession;

    
    private static final String NAMESPACE = "com.soso.mappers.employee.EmployeeMapper.";

    
    public List<EmployeeDTO> selectEmployeeList(int businessSeq) {
        return sqlSession.selectList(NAMESPACE + "selectEmployeeList", businessSeq);
    }

    
    public void insertEmployee(EmployeeDTO employee) {
        sqlSession.insert(NAMESPACE + "insertEmployee", employee);
    }

    
    public EmployeeDTO selectEmployeeBySeq(int employeeSeq) {
        return sqlSession.selectOne(NAMESPACE + "selectEmployeeBySeq", employeeSeq);
    }

    
    public void updateEmployeeStatus(int employeeSeq, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("employeeSeq", employeeSeq);
        params.put("status", status);
        sqlSession.update(NAMESPACE + "updateEmployeeStatus", params);
    }

    
    public List<AttendanceHistoryDTO> selectAttendanceHistoryList(int employeeSeq, String yearMonth) {
        Map<String, Object> params = new HashMap<>();
        params.put("employeeSeq", employeeSeq);
        params.put("yearMonth", yearMonth);
        return sqlSession.selectList(NAMESPACE + "selectAttendanceHistoryList", params);
    }

    
    public AttendanceHistoryDTO selectTodayAttendance(int employeeSeq, String workDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("employeeSeq", employeeSeq);
        params.put("workDate", workDate);
        return sqlSession.selectOne(NAMESPACE + "selectTodayAttendance", params);
    }

    
    public void insertAttendance(AttendanceHistoryDTO attendance) {
        sqlSession.insert(NAMESPACE + "insertAttendance", attendance);
    }

    
    public void updateAttendanceCheckOut(AttendanceHistoryDTO attendance) {
        sqlSession.update(NAMESPACE + "updateAttendanceCheckOut", attendance);
    }
}
