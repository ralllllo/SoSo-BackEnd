package com.soso.domain.main;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private BusinessMainService businessMainService;

    @GetMapping("/api/test")
    public ResponseEntity<Map<String, String>> testConnection() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "스프링 부트와 리액트가 성공적으로 연결되었습니다!");
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/api/business/dashboard")
    public ResponseEntity<Map<String, Object>> getBusinessDashboard(
            @RequestParam int storeSeq,
            @RequestParam long userSeq) {
        Map<String, Object> data = businessMainService.getDashboardData(storeSeq, userSeq);
        return ResponseEntity.ok(data);
    }
}