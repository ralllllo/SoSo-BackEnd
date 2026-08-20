package com.soso.domain.order.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.soso.domain.order.dto.PartnerOrderDetailDTO;
import com.soso.domain.order.dto.PartnerOrderListDTO;
import com.soso.domain.order.services.PartnerOrderService;


@RestController
@RequestMapping("/api/partner/orders") 
public class PartnerOrderController {

    @Autowired
    private PartnerOrderService partnerOrderService; 

    
    @GetMapping
    public ResponseEntity<List<PartnerOrderListDTO>> getOrderList(
            @RequestParam Long sellerSeq,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {

        List<PartnerOrderListDTO> list = partnerOrderService.getOrderList(sellerSeq, keyword, status);
        return ResponseEntity.ok(list);
    }

    
    @GetMapping("/{orderSeq}")
    public ResponseEntity<List<PartnerOrderDetailDTO>> getOrderDetail(@PathVariable Long orderSeq) {
        
        List<PartnerOrderDetailDTO> details = partnerOrderService.getOrderDetail(orderSeq);

        
        return ResponseEntity.ok(details);
    }

    
    @PutMapping("/{orderSeq}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long orderSeq, @RequestBody String status) {
        
        String cleanStatus = status.replace("\"", "");
        partnerOrderService.updateOrderStatus(orderSeq, cleanStatus);
        return ResponseEntity.ok("상태가 변경되었습니다.");
    }

    
    @GetMapping("/dashboard")
    public ResponseEntity<java.util.Map<String, Object>> getDashboardData(@RequestParam Long sellerSeq) {
        java.util.Map<String, Object> data = partnerOrderService.getDashboardData(sellerSeq);
        return ResponseEntity.ok(data);
    }
}
