package com.soso.domain.order.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soso.domain.order.dto.OrderDTO;
import com.soso.domain.order.dto.OrderItemDTO;
import com.soso.domain.order.dto.OrderListDTO;
import com.soso.domain.order.dto.OrderRecommendDTO;
import com.soso.domain.order.services.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService OrderServ;

	
	@GetMapping("/check")
	public ResponseEntity<List<OrderRecommendDTO>> recommendStock(
			@RequestParam("itemName") String itemName,
			@RequestParam("storeSeq") Long storeSeq) {

		List<OrderRecommendDTO> recommendList = OrderServ.recommendStock(itemName, storeSeq);


		return ResponseEntity.ok(recommendList);
	}

	
	@GetMapping("/items")
	public ResponseEntity<List<OrderItemDTO>> compareItem(OrderItemDTO dto) {

		List<OrderItemDTO> list = OrderServ.compareItem(dto);

		return ResponseEntity.ok(list);
	}

	
	@GetMapping("/identity")
	public ResponseEntity<Map<String, Object>> identityCheck(@RequestParam("storeSeq") Long storeSeq) {

		Map<String, Object> identity = OrderServ.identityCheck(storeSeq);

		return ResponseEntity.ok(identity);
	}

	
	@PostMapping("/form")
	public ResponseEntity<Integer> orderForm(@RequestBody OrderDTO dto, HttpServletRequest request) {

		
		
		if (dto.getBuyerSeq() == null) {
			return ResponseEntity.badRequest().body(0);
		}

		if (dto.getSellerSeq() == null) {
			return ResponseEntity.badRequest().body(0);
		}

		int result = OrderServ.orderForm(dto);

		return ResponseEntity.ok(result);
	}

	
	@GetMapping("/suppliers")
	public ResponseEntity<List<OrderItemDTO>> suppliers() {
		List<OrderItemDTO> list = OrderServ.suppliers();
		return ResponseEntity.ok(list);
	}

	
	@GetMapping("/list")
	public ResponseEntity<List<OrderListDTO>> orderList(
			@RequestParam("storeSeq") Long storeSeq,
			@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) {
		List<OrderListDTO> orderList = OrderServ.orderList(
				storeSeq,
				offset,
				keyword,
				status,
				startDate,
				endDate);

		return ResponseEntity.ok(orderList);
	}

	
	@GetMapping("/list/{orderSeq}")
	public ResponseEntity<Map<String, Object>> getOrderDetail(@PathVariable Long orderSeq) {
		Map<String, Object> detail = OrderServ.getOrderDetail(orderSeq);
		return ResponseEntity.ok(detail);
	}

	
	@GetMapping("/me")
	public ResponseEntity<Long> webSocketMe(HttpServletRequest request) {

		Long webSocketMe = (Long) request.getAttribute("user_seq");

		return ResponseEntity.ok(webSocketMe);
	}

	
	
	
	@PostMapping("/test/status/{orderSeq}")
	public ResponseEntity<String> testUpdateOrderStatus(@PathVariable Long orderSeq, @RequestParam String status) {
		OrderServ.updateOrderStatus(orderSeq, status);

		return ResponseEntity.ok("발주 상태 변경 및 웹소켓 알림 전송 완료");
	}

	@GetMapping("/unpaid")
	public ResponseEntity<List<Map<String, Object>>> unpaidOrders(
			@RequestParam Long storeSeq,
			@RequestParam Long partnerSeq) {

		List<Map<String, Object>> result = OrderServ.unpaidOrders(storeSeq, partnerSeq);
		return ResponseEntity.ok(result);
	}

}
