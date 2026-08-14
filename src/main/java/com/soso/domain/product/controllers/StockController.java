package com.soso.domain.product.controllers;

import com.soso.domain.product.dto.*;
import com.soso.domain.product.services.StockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    
    @GetMapping
    public ResponseEntity<List<StockDTO>> list(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam(required = false) Integer storeSeq,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        
        Long userSeqLong = (Long) request.getAttribute("user_seq");
        int userSeq = userSeqLong != null ? userSeqLong.intValue() : 0;

        Map<String, Object> filters = new HashMap<>();
        filters.put("userSeq", userSeq);
        filters.put("storeSeq", storeSeq);
        filters.put("search", search);
        filters.put("category", category);
        filters.put("status", status);
        
        return ResponseEntity.ok(stockService.getStockList(filters));
    }

    
    @PostMapping
    public ResponseEntity<String> register(@RequestParam int storeSeq,@RequestBody StockDTO stock) {
        stockService.createStock(storeSeq,stock);
        return ResponseEntity.ok("품목이 등록되었습니다.");
    }

    
    @PostMapping("/incoming")
    public ResponseEntity<String> incoming(
            @RequestParam int storeSeq,
            @RequestBody StockIncomingDTO incoming) {
    	try {
            stockService.processIncoming(incoming, storeSeq);
            return ResponseEntity.ok("입고 처리가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @PostMapping("/outbound")
    public ResponseEntity<String> outbound(
            @RequestParam int storeSeq,
            @RequestBody StockOutboundRequest requestObj) {
        try {
            stockService.processOutbound(requestObj, storeSeq);
            return ResponseEntity.ok("출고 처리가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @PostMapping("/adjust")
    public ResponseEntity<String> adjust(
            @RequestParam int storeSeq,
            @RequestBody StockAdjustRequest requestObj) {
        try {
            stockService.processAdjust(requestObj, storeSeq);
            return ResponseEntity.ok("재고 조정이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @PutMapping("/{stockSeq}")
    public ResponseEntity<String> update(
            @PathVariable int stockSeq, 
            @RequestParam int storeSeq,
            @RequestBody StockDTO stock) {
        try {
            stock.setStockSeq(stockSeq);
            stock.setStoreSeq(storeSeq);
            stockService.updateStockInfo(stock);
            return ResponseEntity.ok("품목 정보가 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @DeleteMapping("/{stockSeq}")
    public ResponseEntity<String> delete(
            @PathVariable int stockSeq,
            @RequestParam int storeSeq) {
        try {
            stockService.deleteStock(stockSeq, storeSeq);
            return ResponseEntity.ok("품목이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{stockSeq}/batches")
    public ResponseEntity<List<StockBatchDTO>> getBatches(
            @PathVariable int stockSeq,
            @RequestParam int storeSeq) {
        return ResponseEntity.ok(stockService.getBatches(stockSeq, storeSeq));
    }

    @GetMapping("/{stockSeq}/histories")
    public ResponseEntity<List<StockHistoryDTO>> getHistories(
            @PathVariable int stockSeq,
            @RequestParam int storeSeq) {
        return ResponseEntity.ok(stockService.getHistories(stockSeq, storeSeq));
    }

    @GetMapping("/countExpiringSoon")
    public ResponseEntity<Integer> getcountExpiringSoon(
            jakarta.servlet.http.HttpServletRequest request,
            @RequestParam(required = false) Integer storeSeq) {
        
        Long userSeqLong = (Long) request.getAttribute("user_seq");
        int userSeq = userSeqLong != null ? userSeqLong.intValue() : 0;
        
        Map<String, Object> filters = new HashMap<>();
        filters.put("userSeq", userSeq);
        filters.put("storeSeq", storeSeq);
        
        return ResponseEntity.ok(stockService.getcountExpiringSoon(filters));
    }
}
