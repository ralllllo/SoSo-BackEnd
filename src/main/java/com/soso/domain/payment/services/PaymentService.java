package com.soso.domain.payment.services;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.soso.domain.payment.dao.PaymentDAO;
import com.soso.domain.payment.dto.AutoPaymentScheduleDTO;
import com.soso.domain.payment.dto.OrderPaymentRequestDTO;
import com.soso.domain.payment.dto.PaymentCardDTO;
import com.soso.domain.payment.dto.PaymentDTO;

import tools.jackson.databind.ObjectMapper;

@Service
public class PaymentService {
	
	@Autowired
	private PaymentDAO dao;
	
	
    
    @Value("${portone.store-id}")
    private String portoneStoreId;

    
    @Value("${portone.channel-key}")
    private String portoneChannelKey;

    
    
    
    @Value("${portone.api-secret}")
    private String portoneApiSecret;
    
    
	
	
	public int insertAccount(PaymentDTO dto) {
		
		int limit = dao.accountCount(dto.getStoreSeq());
		
		if(limit >= 4) {
			throw new RuntimeException("계좌는 최대 4개까지 등록할 수 있습니다.");
		}
		
		String billingKey = "TEST_BILLING_KEY" + UUID.randomUUID();
		dto.setBillingKey(billingKey);
		
		
		return dao.insertAccount(dto);
	}
	
	
	public List<PaymentDTO> accountList(Long storeSeq) {
		
		return dao.accountList(storeSeq);
	}
	
	
	public int accountDel(Long accountSeq) {
		return dao.accountDel(accountSeq);
	}
	
	
	public int autoPaymentSchedule(AutoPaymentScheduleDTO dto) {
	    if (dto.getStoreSeq() == 0) {
	        throw new RuntimeException("사업장 정보가 없습니다.");
	    }

	    if (dto.getPartnerSeq() == 0) {
	        throw new RuntimeException("거래처를 선택해 주세요.");
	    }

	    if (dto.getAccountSeq() == 0) {
	        throw new RuntimeException("출금 계좌를 선택해 주세요.");
	    }

	    if (dto.getPaymentDay() == 0 || dto.getPaymentDay() < 1 || dto.getPaymentDay() > 31) {
	        throw new RuntimeException("결제일은 1일부터 31일 사이로 선택해 주세요.");
	    }

	    return dao.autoPaymentSchedule(dto);
	}
	
	
	public int insertCard(PaymentCardDTO dto) {
		
		if(dto.getStoreSeq() == null || dto.getStoreSeq() == 0) {
			throw new RuntimeException("사업장 정보가 없습니다.");
		}
		
		if(dto.getBillingKey() == null || dto.getBillingKey().isBlank()) {
			throw new RuntimeException("빌링키 정보가 없습니다.");
		}
		return dao.insertCard(dto);
	}
	
	
	public List<PaymentCardDTO> selectCard(Long storeSeq) {
		
		if(storeSeq == null || storeSeq == 0) {
			throw new RuntimeException("사업장 정보가 없습니다.");
		}
		
		return dao.selectCard(storeSeq);
	}
	
	
	
	@Transactional
	public Map<String, Object> payOrders(OrderPaymentRequestDTO dto) {

	    
	    Map<String, Object> result = new HashMap<>();

	    
	    if (dto.getStoreSeq() == null) {
	        result.put("success", false);
	        result.put("message", "사업장 정보가 없습니다.");
	        return result;
	    }

	    
	    if (dto.getPartnerSeq() == null) {
	        result.put("success", false);
	        result.put("message", "거래처 정보가 없습니다.");
	        return result;
	    }

	    
	    if (dto.getCardSeq() == null) {
	        result.put("success", false);
	        result.put("message", "결제 카드 정보가 없습니다.");
	        return result;
	    }

	    
	    if (dto.getOrderSeqList() == null || dto.getOrderSeqList().isEmpty()) {
	        result.put("success", false);
	        result.put("message", "결제할 발주가 없습니다.");
	        return result;
	    }

	    
	    
	 
	 
	 
	 PaymentCardDTO card = dao.selectCardForPayment(
	         dto.getStoreSeq(),
	         dto.getCardSeq()
	 );

	 
	 if (card == null) {
	     result.put("success", false);
	     result.put("message", "결제할 카드를 찾을 수 없습니다.");
	     return result;
	 }

	


	 
	 
	 
	 List<Map<String, Object>> orders = dao.selectOrdersForPayment(
	         dto.getStoreSeq(),
	         dto.getPartnerSeq(),
	         dto.getOrderSeqList()
	 );

	 
	 
	 if (orders.size() != dto.getOrderSeqList().size()) {
	     result.put("success", false);
	     result.put("message", "이미 결제된 발주가 포함되어 있거나 결제할 수 없는 발주입니다.");
	     return result;
	 }

	 
	 
	 int totalAmount = orders.stream()
	         .mapToInt(order -> ((Number) order.get("totalAmount")).intValue())
	         .sum();

	 


	
	
	
	String paymentId = callPortOneBillingPayment(
	        card.getBillingKey(),
	        totalAmount
	);

	
	Integer paymentSeq = dao.insertPayment(
	        dto.getStoreSeq(),      
	        dto.getPartnerSeq(),    
	        dto.getCardSeq(),       
	        paymentId,              
	        totalAmount             
	);

	
	dao.insertPaymentOrderMap(
	        paymentSeq,             
	        dto.getOrderSeqList()   
	);

	
	dao.insertExpensesForPaidOrders(
	        dto.getStoreSeq(),      
	        dto.getOrderSeqList()   
	);
	
	
	
	upsertPaymentRag(
	        paymentSeq,
	        paymentId,
	        dto,
	        card,
	        orders,
	        totalAmount
	);

	
	result.put("success", true);
	result.put("message", "결제 및 DB 저장까지 완료되었습니다.");
	result.put("paymentId", paymentId);
	result.put("paymentSeq", paymentSeq);
	result.put("storeSeq", dto.getStoreSeq());
	result.put("partnerSeq", dto.getPartnerSeq());
	result.put("cardSeq", dto.getCardSeq());
	result.put("orderSeqList", dto.getOrderSeqList());
	result.put("totalAmount", totalAmount);

	return result;
	}
	
	
	
	private String callPortOneBillingPayment(String billingKey, int totalAmount) {

	    try {
	        
	        
	        String paymentId = "soso-order-" + System.currentTimeMillis();

	        
	        ObjectMapper objectMapper = new ObjectMapper();

	        
	        
	        Map<String, Object> amount = new HashMap<>();
	        amount.put("total", totalAmount);

	     
	     
	     
	     Map<String, Object> customerName = new HashMap<>();
	     customerName.put("full", "SOSO 사업자");

	     
	     
	     Map<String, Object> customer = new HashMap<>();

	     
	     customer.put("id", "soso-customer");

	     
	     customer.put("name", customerName);

	     
	     
	     customer.put("email", "soso@test.com");

	     
	     
	     customer.put("phoneNumber", "01012345678");

	        
	        Map<String, Object> body = new HashMap<>();
	        body.put("storeId", portoneStoreId);
	        body.put("channelKey", portoneChannelKey);
	        body.put("billingKey", billingKey);
	        body.put("orderName", "SOSO 발주 결제");
	        body.put("amount", amount);
	        body.put("currency", "KRW");
	        body.put("customer", customer);

	        
	        String jsonBody = objectMapper.writeValueAsString(body);

	        
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create("https://api.portone.io/payments/" + paymentId + "/billing-key"))
	                .header("Authorization", "PortOne " + portoneApiSecret)
	                
	                .header("Content-Type", "application/json; charset=utf-8")
	                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
	                .build();

	        
	        HttpClient client = HttpClient.newHttpClient();

	        
	        HttpResponse<String> response =
	                client.send(request, HttpResponse.BodyHandlers.ofString());

	        

	        
	        if (response.statusCode() < 200 || response.statusCode() >= 300) {
	            throw new RuntimeException("포트원 결제 실패: " + response.body());
	        }

	        
	        return paymentId;

	    } catch (Exception e) {
	        throw new RuntimeException("포트원 결제 요청 중 오류: " + e.getMessage(), e);
	    }
	}
	
	
	
	private void upsertPaymentRag(
	        Integer paymentSeq,
	        String paymentId,
	        OrderPaymentRequestDTO dto,
	        PaymentCardDTO card,
	        List<Map<String, Object>> orders,
	        int totalAmount
	) {
	    try {
	        
	    	
	    	StringBuilder orderSummary = new StringBuilder();

	    	for (Map<String, Object> order : orders) {
	    	    Object itemSummaryObj = order.get("itemSummary");

	    	    String itemSummary = "품목 정보 없음";
	    	    if (itemSummaryObj != null) {
	    	        itemSummary = String.valueOf(itemSummaryObj);
	    	    }

	    	    orderSummary.append("발주번호: ")
	    	            .append(order.get("orderSeq"))
	    	            .append(", 결제 품목: ")
	    	            .append(itemSummary)
	    	            .append(", 결제금액: ")
	    	            .append(order.get("totalAmount"))
	    	            .append("원. ");
	    	}

	        
	        
	        String payerName = "정보 없음";
	        String partnerName = "정보 없음";

	        if (orders != null && !orders.isEmpty()) {
	            Object payerNameObj = orders.get(0).get("payerName");
	            Object partnerNameObj = orders.get(0).get("partnerName");

	            if (payerNameObj != null) {
	                payerName = String.valueOf(payerNameObj);
	            }

	            if (partnerNameObj != null) {
	                partnerName = String.valueOf(partnerNameObj);
	            }
	        }

	        
	        Map<String, Object> metadata = new HashMap<>();
	        metadata.put("paymentSeq", paymentSeq);
	        metadata.put("paymentId", paymentId);
	        metadata.put("storeSeq", dto.getStoreSeq());
	        metadata.put("partnerSeq", dto.getPartnerSeq());
	        metadata.put("payerName", payerName);
	        metadata.put("partnerName", partnerName);
	        metadata.put("cardSeq", dto.getCardSeq());
	        metadata.put("cardCompany", card.getCardCompany());
	        metadata.put("cardName", card.getCardName());
	        metadata.put("cardNumberMasked", card.getCardNumberMasked());
	        metadata.put("totalAmount", totalAmount);
	        metadata.put("status", "결제완료");
	        metadata.put("orderSeqList", dto.getOrderSeqList().toString());
	        metadata.put("orderSummary", orderSummary.toString());

	        
	        String text = String.format(
	                "[카드 결제 상세 정보] 결제번호: %s, 포트원 결제ID: %s, 결제상태: 결제완료, 결제한 매장번호: %s, 결제한 매장명: %s, 받은 거래처번호: %s, 받은 거래처명: %s, 결제 카드번호: %s, 카드사: %s, 카드명: %s, 마스킹 카드번호: %s, 총 결제금액: %s원, 결제한 발주 목록: %s",
	                paymentSeq,
	                paymentId,
	                dto.getStoreSeq(),
	                payerName,
	                dto.getPartnerSeq(),
	                partnerName,
	                dto.getCardSeq(),
	                card.getCardCompany(),
	                card.getCardName(),
	                card.getCardNumberMasked(),
	                totalAmount,
	                orderSummary.toString()
	        );

	    } catch (Exception e) {
	        
	    }
	}
	
	
	
	public List<Map<String, Object>> selectRecentPayments(
	        Integer storeSeq,
	        String period,
	        String startDate,
	        String endDate,
	        String keyword) {

	    
	    if (storeSeq == null || storeSeq == 0) {
	        throw new RuntimeException("사업장 정보가 없습니다.");
	    }

	    
	    if (period == null || period.isBlank()) {
	        period = "week";
	    }

	    
	    return dao.selectRecentPayments(
	            storeSeq,
	            period,
	            startDate,
	            endDate,
	            keyword
	    );
	}
	
	
	
	public Map<String, Object> selectCollectionDashboard(Long storeSeq) {

	    
	    if (storeSeq == null || storeSeq == 0) {
	        throw new RuntimeException("사업장 정보가 없습니다.");
	    }

	    
	    Map<String, Object> result = new HashMap<>();

	    
	    Map<String, Object> businessInfo = dao.selectCollectionBusinessInfo(storeSeq);

	    
	    Map<String, Object> summary = dao.selectCollectionSummary(storeSeq);

	    
	    List<Map<String, Object>> depositAccounts = dao.selectCollectionDepositAccounts(storeSeq);

	    
	    List<Map<String, Object>> collectionRows = dao.selectCollectionRows(storeSeq);

	    
	    if (businessInfo == null) {
	        businessInfo = new HashMap<>();
	    }

	    
	    if (summary == null) {
	        summary = new HashMap<>();

	        
	        summary.put("paidAmount", 0);

	        
	        summary.put("paidCount", 0);

	        
	        summary.put("scheduledAmount", 0);

	        
	        summary.put("scheduledCount", 0);

	        
	        summary.put("unpaidAmount", 0);

	        
	        summary.put("unpaidCount", 0);
	    }

	    
	    if (depositAccounts == null) {
	        depositAccounts = new ArrayList<>();
	    }

	    
	    if (collectionRows == null) {
	        collectionRows = new ArrayList<>();
	    }

	    
	    result.put("businessInfo", businessInfo);

	    
	    result.put("summary", summary);

	    
	    result.put("depositAccounts", depositAccounts);

	    
	    result.put("collectionRows", collectionRows);

	    
	    return result;
	}
	
	
	public int deleteCard(Long cardSeq) {
	    return dao.deleteCard(cardSeq);
	}
	
}