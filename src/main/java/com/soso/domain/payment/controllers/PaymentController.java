package com.soso.domain.payment.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soso.domain.payment.dto.AutoPaymentScheduleDTO;
import com.soso.domain.payment.dto.OrderPaymentRequestDTO;
import com.soso.domain.payment.dto.PaymentCardDTO;
import com.soso.domain.payment.dto.PaymentDTO;
import com.soso.domain.payment.services.PaymentService;

@RestController
@RequestMapping("/account")
public class PaymentController {
	
	@Autowired
	private PaymentService accountServ;
	
	
	@PostMapping("/accountSystem")
	public ResponseEntity<String> insertAccount(@RequestBody PaymentDTO dto) {
		
		accountServ.insertAccount(dto);
		
		return ResponseEntity.ok().build();
	}
	
	
	@GetMapping("/accountList")
	public ResponseEntity<List<PaymentDTO>> accountList(@RequestParam Long storeSeq) {
		List<PaymentDTO> list = accountServ.accountList(storeSeq);
		return ResponseEntity.ok(list);
	}
	
	
	@DeleteMapping("/accountDel/{accountSeq}")
	public ResponseEntity<String> accountDel(@PathVariable Long accountSeq) {
		
		int result = accountServ.accountDel(accountSeq);
		
		if(result == 0) {
			return ResponseEntity.badRequest().body("삭제할 계좌가 없습니다.");
		}
		return ResponseEntity.ok("계좌가 삭제 되었습니다.");
	}
	
	
	@PostMapping("/autoPaymentSchedule")
	public ResponseEntity<String> autoPaymentSchedule(@RequestBody AutoPaymentScheduleDTO dto) {
	    accountServ.autoPaymentSchedule(dto);
	    return ResponseEntity.ok("자동이체 설정이 등록되었습니다.");
	}
	
	
	@PostMapping("/cards")
	public ResponseEntity<String> insertCard(@RequestBody PaymentCardDTO dto) {
		accountServ.insertCard(dto);
		return ResponseEntity.ok("카드가 등록되었습니다");
	}
	
	
	@GetMapping("/cards")
	public ResponseEntity<List<PaymentCardDTO>> selectCard(@RequestParam Long storeSeq) {
		return ResponseEntity.ok(accountServ.selectCard(storeSeq));
	}
	
	
	
	@PostMapping("/order/pay")
	public ResponseEntity<Map<String, Object>> payOrders(@RequestBody OrderPaymentRequestDTO dto) {

	    
	    
	    Map<String, Object> result = accountServ.payOrders(dto);

	    
	    return ResponseEntity.ok(result);
	}
	

	
	
	
	@GetMapping("/recent-payments")
	public ResponseEntity<List<Map<String, Object>>> selectRecentPayments(
	        @RequestParam Integer storeSeq,
	        @RequestParam(required = false, defaultValue = "week") String period,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate,
	        @RequestParam(required = false) String keyword) {

	    
	    List<Map<String, Object>> list = accountServ.selectRecentPayments(
	            storeSeq,
	            period,
	            startDate,
	            endDate,
	            keyword
	    );

	    
	    return ResponseEntity.ok(list);
	}
	
	
	@GetMapping("/collection")
	public ResponseEntity<Map<String, Object>> selectCollectionDashboard(
	        @RequestParam Long storeSeq) {

	    Map<String, Object> result = accountServ.selectCollectionDashboard(storeSeq);

	    return ResponseEntity.ok(result);
	}
	
	
	@DeleteMapping("/cards/{cardSeq}")
	public ResponseEntity<String> deleteCard(@PathVariable Long cardSeq) {

	    int result = accountServ.deleteCard(cardSeq);

	    if (result == 0) {
	        return ResponseEntity.badRequest().body("삭제할 카드가 없습니다.");
	    }

	    return ResponseEntity.ok("카드가 삭제되었습니다.");
	}

}
