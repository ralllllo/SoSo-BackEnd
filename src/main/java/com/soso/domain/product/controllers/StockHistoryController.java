package com.soso.domain.product.controllers;
import com.soso.domain.product.dto.StockHistoryDTO;
import com.soso.domain.product.services.StockHistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soso.domain.product.dto.StockHistoryDTO;
import com.soso.domain.product.services.StockHistoryService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/stock-history")
public class StockHistoryController {

    @Autowired
    private StockHistoryService stockHistoryService;

    
    @GetMapping("/dashboard")
    public ResponseEntity<List<StockHistoryDTO>> getDashboardHistory(
            HttpServletRequest request,
            @RequestParam(required = false) Integer storeSeq) {
        
        Long userSeqLong = (Long) request.getAttribute("user_seq");
        int userSeq = userSeqLong != null ? userSeqLong.intValue() : 0;
        
        List<StockHistoryDTO> history = stockHistoryService.getDashboardHistory(userSeq, storeSeq);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/modal")
    public ResponseEntity<Map<String, Object>> getModalHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer storeSeq,
            @RequestParam(required = false) Integer stockSeq,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String keyword) {
        
        Long userSeqLong = (Long) request.getAttribute("user_seq");
        int userSeq = userSeqLong.intValue();
        
        Map<String, Object> data = stockHistoryService.getModalHistory(page, size, userSeq, storeSeq, stockSeq, transactionType, startDate, endDate, keyword);
          
        return ResponseEntity.ok(data);
    }
}

