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

import com.soso.domain.payment.dto.ExpensesDTO;
import com.soso.domain.payment.services.ExpenseCategoryService;

@RestController
@RequestMapping("/expense")
public class ExpenseCategoryController {
	
	@Autowired
	private ExpenseCategoryService ExpenseServ;
	
	
	@PostMapping("/{storeSeq}")
	public ResponseEntity<Map<String, Object>> insertCategory(@PathVariable Integer storeSeq, @RequestBody ExpensesDTO dto) {
		
		dto.setStoreSeq(storeSeq);
		
		int result = ExpenseServ.insertCategory(dto);
		
		Map<String, Object> response = new HashMap<>();
		
		if(result > 0) {
			response.put("success", true);
			response.put("message", "등록 완료");
			return ResponseEntity.ok(response);
		}
			response.put("success", false);
			response.put("message", "등록 실패");
			return ResponseEntity.badRequest().body(response);
	}
	
	
	@GetMapping("/{storeSeq}/total")
	public ResponseEntity<Map<String, Object>> monthlyTotal(@PathVariable Integer storeSeq, @RequestParam String month) {
		
		Long totalAmount = ExpenseServ.monthlyTotal(storeSeq, month);
		
		Map<String, Object> response = new HashMap<>();
		response.put("totalAmount", totalAmount);
		
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/{storeSeq}/categoryCost")
	public ResponseEntity<List<Map<String, Object>>> categoryCost(@PathVariable int storeSeq, @RequestParam String month) {

	    List<Map<String, Object>> result = ExpenseServ.categoryCost(storeSeq, month);
	    return ResponseEntity.ok(result);
	}
	
	
	@GetMapping("/{storeSeq}/details")
	public ResponseEntity<List<ExpensesDTO>> expenseDetails(
	        @PathVariable int storeSeq,
	        @RequestParam String month,
	        @RequestParam int categorySeq) {

	    List<ExpensesDTO> result = ExpenseServ.expenseDetails(storeSeq, month, categorySeq);

	    return ResponseEntity.ok(result);
	}
	
	
	@GetMapping("/{storeSeq}/general-orders")
	public ResponseEntity<List<Map<String, Object>>> generalOrdersForExpense(
	        @PathVariable int storeSeq,
	        @RequestParam int partnerStoreSeq) {

	    List<Map<String, Object>> result =
	            ExpenseServ.generalOrdersForExpense(storeSeq, partnerStoreSeq);

	    return ResponseEntity.ok(result);
	}
	
	
			@PutMapping("/{storeSeq}/{expenseSeq}/memo")
			public ResponseEntity<Map<String, Object>> updateExpenseMemo(
			        @PathVariable Long storeSeq,
			        @PathVariable Long expenseSeq,
			        @RequestBody Map<String, String> body) {

			    String memo = body.get("memo");

			    int result = ExpenseServ.updateExpenseMemo(storeSeq, expenseSeq, memo);

			    Map<String, Object> response = new HashMap<>();

			    if (result == 0) {
			        response.put("success", false);
			        response.put("message", "수정할 지출 내역이 없습니다.");
			        return ResponseEntity.badRequest().body(response);
			    }

			    response.put("success", true);
			    response.put("message", "메모가 수정되었습니다.");

			    return ResponseEntity.ok(response);
			}
			
			
			
			@DeleteMapping("/{storeSeq}/{expenseSeq}")
			public ResponseEntity<Map<String, Object>> deleteExpense(
			        @PathVariable Long storeSeq,
			        @PathVariable Long expenseSeq) {

			    int result = ExpenseServ.deleteExpense(storeSeq, expenseSeq);

			    Map<String, Object> response = new HashMap<>();

			    if (result == 0) {
			        response.put("success", false);
			        response.put("message", "삭제할 지출 내역이 없습니다.");
			        return ResponseEntity.badRequest().body(response);
			    }

			    response.put("success", true);
			    response.put("message", "지출 내역이 삭제되었습니다.");

			    return ResponseEntity.ok(response);
			}
}
