package com.soso.domain.payment.services;

//포트원 API 호출을 위해 사용
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
//application.properties 값을 가져오기 위해 사용
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//결제 저장 중 하나라도 실패하면 전체 롤백하기 위해 사용
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
	
	
    // application.properties에 작성한 포트원 상점 ID
    @Value("${portone.store-id}")
    private String portoneStoreId;

    // application.properties에 작성한 포트원 채널 키
    @Value("${portone.channel-key}")
    private String portoneChannelKey;

    // application.properties에 작성한 포트원 API Secret
    // 서버에서 포트원 API 호출할 때만 사용한다.
    // 절대 프론트로 내려주면 안 됨.
    @Value("${portone.api-secret}")
    private String portoneApiSecret;
    
    
	
	// 계좌 등록 + 4개 제한
	public int insertAccount(PaymentDTO dto) {
		
		int limit = dao.accountCount(dto.getStoreSeq());
		
		if(limit >= 4) {
			throw new RuntimeException("계좌는 최대 4개까지 등록할 수 있습니다.");
		}
		
		String billingKey = "TEST_BILLING_KEY" + UUID.randomUUID();
		dto.setBillingKey(billingKey);
		
		
		return dao.insertAccount(dto);
	}
	
	// 계좌 출력
	public List<PaymentDTO> accountList(Long storeSeq) {
		
		return dao.accountList(storeSeq);
	}
	
	// 계좌 삭제 : 실제 삭제 X -> Update
	public int accountDel(Long accountSeq) {
		return dao.accountDel(accountSeq);
	}
	
	// 자동이체 설정 등록
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
	
	// 카드 등록
	public int insertCard(PaymentCardDTO dto) {
		
		if(dto.getStoreSeq() == null || dto.getStoreSeq() == 0) {
			throw new RuntimeException("사업장 정보가 없습니다.");
		}
		
		if(dto.getBillingKey() == null || dto.getBillingKey().isBlank()) {
			throw new RuntimeException("빌링키 정보가 없습니다.");
		}
		return dao.insertCard(dto);
	}
	
	// 카드 조회
	public List<PaymentCardDTO> selectCard(Long storeSeq) {
		
		if(storeSeq == null || storeSeq == 0) {
			throw new RuntimeException("사업장 정보가 없습니다.");
		}
		
		return dao.selectCard(storeSeq);
	}
	
	/**
	 * 발주 결제 처리
	 *
	 * 현재 단계:
	 * 1. 프론트에서 넘어온 결제 요청값 검증
	 * 2. Service까지 요청이 잘 들어오는지 확인
	 *
	 * 다음 단계에서 여기 안에
	 * 카드 조회 → 발주 조회 → 포트원 결제 → DB 저장 로직을 추가할 예정
	 */
	// 포트원 결제 성공 후 DB 저장 중 에러가 나면 DB 작업을 롤백하기 위해 사용
	@Transactional
	public Map<String, Object> payOrders(OrderPaymentRequestDTO dto) {

	    // 응답으로 내려줄 Map
	    Map<String, Object> result = new HashMap<>();

	    // 매장 번호가 없으면 결제 불가
	    if (dto.getStoreSeq() == null) {
	        result.put("success", false);
	        result.put("message", "사업장 정보가 없습니다.");
	        return result;
	    }

	    // 거래처 번호가 없으면 결제 불가
	    if (dto.getPartnerSeq() == null) {
	        result.put("success", false);
	        result.put("message", "거래처 정보가 없습니다.");
	        return result;
	    }

	    // 카드 번호가 없으면 결제 불가
	    if (dto.getCardSeq() == null) {
	        result.put("success", false);
	        result.put("message", "결제 카드 정보가 없습니다.");
	        return result;
	    }

	    // 결제할 발주 목록이 없으면 결제 불가
	    if (dto.getOrderSeqList() == null || dto.getOrderSeqList().isEmpty()) {
	        result.put("success", false);
	        result.put("message", "결제할 발주가 없습니다.");
	        return result;
	    }

	    // 확인용 로그
	    
	 // 결제에 사용할 카드 조회
	 // storeSeq = 현재 결제하는 사업자 매장 번호
	 // cardSeq = 사용자가 선택한 카드 번호
	 PaymentCardDTO card = dao.selectCardForPayment(
	         dto.getStoreSeq(),
	         dto.getCardSeq()
	 );

	 // 카드가 조회되지 않으면 결제 진행 불가
	 if (card == null) {
	     result.put("success", false);
	     result.put("message", "결제할 카드를 찾을 수 없습니다.");
	     return result;
	 }

	// 카드 조회 성공 확인용 로그


	 // 결제 대상 발주 목록 조회
	 // 사용자가 선택한 발주가 실제로 현재 매장과 거래처 사이의 발주인지 확인한다.
	 // 이미 결제된 발주는 payment.xml 쿼리에서 제외된다.
	 List<Map<String, Object>> orders = dao.selectOrdersForPayment(
	         dto.getStoreSeq(),
	         dto.getPartnerSeq(),
	         dto.getOrderSeqList()
	 );

	 // 프론트에서 선택한 발주 개수와 DB에서 조회된 발주 개수가 다르면 문제 있음
	 // 예: 이미 결제된 발주가 포함됐거나, 다른 거래처의 발주가 섞인 경우
	 if (orders.size() != dto.getOrderSeqList().size()) {
	     result.put("success", false);
	     result.put("message", "이미 결제된 발주가 포함되어 있거나 결제할 수 없는 발주입니다.");
	     return result;
	 }

	 // 총 결제 금액 계산
	 // orders의 totalAmount를 전부 더해서 결제 금액을 만든다.
	 int totalAmount = orders.stream()
	         .mapToInt(order -> ((Number) order.get("totalAmount")).intValue())
	         .sum();

	 // 확인용 로그


	// 포트원 빌링키 결제 요청
	// card.getBillingKey() = payment_cards 테이블에 저장된 포트원 빌링키
	// totalAmount = 위에서 계산한 실제 결제 금액
	String paymentId = callPortOneBillingPayment(
	        card.getBillingKey(),
	        totalAmount
	);

	// 포트원 결제가 성공했으므로 payments 테이블에 결제 내역 저장
	Integer paymentSeq = dao.insertPayment(
	        dto.getStoreSeq(),      // 돈을 낸 사업자 매장 번호
	        dto.getPartnerSeq(),    // 돈을 받은 거래처 매장 번호
	        dto.getCardSeq(),       // 결제에 사용한 카드 번호
	        paymentId,              // 포트원 결제 ID
	        totalAmount             // 총 결제 금액
	);

	// 결제한 발주들을 payment_order_map 테이블에 저장
	dao.insertPaymentOrderMap(
	        paymentSeq,             // 방금 생성된 payments.payment_seq
	        dto.getOrderSeqList()   // 결제한 발주 번호 목록
	);

	// 발주 결제 금액을 expenses 테이블에 식자재비로 자동 등록
	dao.insertExpensesForPaidOrders(
	        dto.getStoreSeq(),      // 지출이 발생한 사업자 매장 번호
	        dto.getOrderSeqList()   // 지출로 등록할 발주 번호 목록
	);
	
	// 결제 내역을 RAG에 저장 또는 갱신
	// payments 기준으로 "카드 결제 내역"을 챗봇이 답변할 수 있게 만든다.
	upsertPaymentRag(
	        paymentSeq,
	        paymentId,
	        dto,
	        card,
	        orders,
	        totalAmount
	);

	// 최종 성공 응답
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
	
	
	/**
	 * 포트원 빌링키 결제 요청
	 *
	 * billingKey:
	 * payment_cards 테이블에 저장된 포트원 빌링키
	 *
	 * totalAmount:
	 * 결제할 총 금액
	 *
	 * 성공하면 우리 DB에 저장할 paymentId를 반환한다.
	 * 실패하면 RuntimeException을 발생시켜 결제 실패로 처리한다.
	 */
	private String callPortOneBillingPayment(String billingKey, int totalAmount) {

	    try {
	        // 포트원 결제 ID
	        // 결제 건마다 고유해야 해서 현재 시간 기준으로 생성
	        String paymentId = "soso-order-" + System.currentTimeMillis();

	        // Map 데이터를 JSON 문자열로 바꾸기 위한 객체
	        ObjectMapper objectMapper = new ObjectMapper();

	        // 포트원 amount 형식
	        // total에 실제 결제할 금액을 넣는다.
	        Map<String, Object> amount = new HashMap<>();
	        amount.put("total", totalAmount);

	     // 고객 이름 정보
	     // 포트원 V2에서 customer.name은 문자열이 아니라 객체 형태여야 함
	     // name: { full: "SOSO 사업자" } 형태로 전달
	     Map<String, Object> customerName = new HashMap<>();
	     customerName.put("full", "SOSO 사업자");

	     // 고객 정보
	     // 포트원 결제 요청에서 email, phoneNumber가 필수라서 같이 넣어야 함
	     Map<String, Object> customer = new HashMap<>();

	     // 고객 구분용 ID
	     customer.put("id", "soso-customer");

	     // 고객 이름
	     customer.put("name", customerName);

	     // 고객 이메일
	     // 테스트용이면 임시 이메일 사용 가능
	     customer.put("email", "soso@test.com");

	     // 고객 전화번호
	     // 테스트용이면 임시 번호 사용 가능
	     customer.put("phoneNumber", "01012345678");

	        // 포트원으로 보낼 요청 바디
	        Map<String, Object> body = new HashMap<>();
	        body.put("storeId", portoneStoreId);
	        body.put("channelKey", portoneChannelKey);
	        body.put("billingKey", billingKey);
	        body.put("orderName", "SOSO 발주 결제");
	        body.put("amount", amount);
	        body.put("currency", "KRW");
	        body.put("customer", customer);

	        // 요청 바디를 JSON 문자열로 변환
	        String jsonBody = objectMapper.writeValueAsString(body);

	        // 포트원 빌링키 결제 API 요청 생성
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create("https://api.portone.io/payments/" + paymentId + "/billing-key"))
	                .header("Authorization", "PortOne " + portoneApiSecret)
	                // 한글이 포함되어 있으므로 charset도 명시
	                .header("Content-Type", "application/json; charset=utf-8")
	                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
	                .build();

	        // HTTP 요청을 보내기 위한 클라이언트 생성
	        HttpClient client = HttpClient.newHttpClient();

	        // 포트원 API 호출
	        HttpResponse<String> response =
	                client.send(request, HttpResponse.BodyHandlers.ofString());

	        // 포트원 응답 확인용 로그

	        // 응답 코드가 200번대가 아니면 결제 실패로 처리
	        if (response.statusCode() < 200 || response.statusCode() >= 300) {
	            throw new RuntimeException("포트원 결제 실패: " + response.body());
	        }

	        // 결제 성공 시 paymentId 반환
	        return paymentId;

	    } catch (Exception e) {
	        throw new RuntimeException("포트원 결제 요청 중 오류: " + e.getMessage(), e);
	    }
	}
	
	
	/**
	 * 카드 결제 데이터를 RAG 문서로 만들어 Qdrant에 저장 또는 갱신하는 메소드
	 *
	 * 이 메소드는 결제 성공 후에만 호출된다.
	 * RAG 저장에 실패해도 실제 결제/DB 저장이 실패하면 안 되기 때문에 try-catch로 감싼다.
	 */
	private void upsertPaymentRag(
	        Integer paymentSeq,
	        String paymentId,
	        OrderPaymentRequestDTO dto,
	        PaymentCardDTO card,
	        List<Map<String, Object>> orders,
	        int totalAmount
	) {
	    try {
	        // 결제한 발주 목록을 챗봇이 읽기 좋은 문장으로 만든다.
	    	// 발주코드보다 실제 결제한 품목명이 나오도록 itemSummary를 사용한다.
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

	        // 첫 번째 발주 기준으로 결제한 매장명과 받은 거래처명을 가져온다.
	        // 같은 결제 건은 같은 storeSeq, partnerSeq 기준이라 첫 번째 값만 써도 된다.
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

	        // Qdrant payload에 같이 저장할 부가 정보
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

	        // 챗봇이 검색해서 읽을 실제 결제 문장
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
	        // RAG 실패 때문에 결제 성공 흐름이 막히면 안 된다.
	    }
	}
	
	// 이체관리 최근 결제 내역 조회
	// 현재 매장에서 카드로 결제한 내역을 검색 조건에 맞게 가져온다.
	public List<Map<String, Object>> selectRecentPayments(
	        Integer storeSeq,
	        String period,
	        String startDate,
	        String endDate,
	        String keyword) {

	    // 매장 번호가 없으면 조회 불가
	    if (storeSeq == null || storeSeq == 0) {
	        throw new RuntimeException("사업장 정보가 없습니다.");
	    }

	    // period가 비어 있으면 기본값으로 이번 주 사용
	    if (period == null || period.isBlank()) {
	        period = "week";
	    }

	    // DAO에 검색 조건 전달
	    return dao.selectRecentPayments(
	            storeSeq,
	            period,
	            startDate,
	            endDate,
	            keyword
	    );
	}
	
	
	/**
	 * 거래처 로그인 기준 수금관리 대시보드 조회
	 *
	 * storeSeq:
	 * 현재 로그인한 거래처의 매장 번호
	 *
	 * 반환 데이터:
	 * 1. 사업자 기본 정보
	 * 2. 상단 수금 요약 카드
	 * 3. 최근 입금 내역
	 * 4. 거래처별 수금 이력
	 */
	public Map<String, Object> selectCollectionDashboard(Long storeSeq) {

	    // 매장 번호가 없으면 조회 불가
	    if (storeSeq == null || storeSeq == 0) {
	        throw new RuntimeException("사업장 정보가 없습니다.");
	    }

	    // 프론트로 반환할 전체 결과 Map 생성
	    Map<String, Object> result = new HashMap<>();

	    // 거래처 사업자 기본 정보 조회
	    Map<String, Object> businessInfo = dao.selectCollectionBusinessInfo(storeSeq);

	    // 상단 요약 카드 데이터 조회
	    Map<String, Object> summary = dao.selectCollectionSummary(storeSeq);

	    // 최근 입금 내역 조회
	    List<Map<String, Object>> depositAccounts = dao.selectCollectionDepositAccounts(storeSeq);

	    // 거래처별 수금 이력 조회
	    List<Map<String, Object>> collectionRows = dao.selectCollectionRows(storeSeq);

	    // 사업자 정보가 없으면 프론트 오류 방지를 위해 빈 Map으로 처리
	    if (businessInfo == null) {
	        businessInfo = new HashMap<>();
	    }

	    // 요약 정보가 없으면 프론트 오류 방지를 위해 기본값 세팅
	    if (summary == null) {
	        summary = new HashMap<>();

	        // 이번 달 입금 완료 금액
	        summary.put("paidAmount", 0);

	        // 이번 달 입금 완료 건수
	        summary.put("paidCount", 0);

	        // 월 입금 예정 금액
	        summary.put("scheduledAmount", 0);

	        // 월 입금 예정 건수
	        summary.put("scheduledCount", 0);

	        // 미수금 금액
	        summary.put("unpaidAmount", 0);

	        // 미수금 건수
	        summary.put("unpaidCount", 0);
	    }

	    // 최근 입금 내역이 null이면 빈 리스트로 처리
	    if (depositAccounts == null) {
	        depositAccounts = new ArrayList<>();
	    }

	    // 수금 이력이 null이면 빈 리스트로 처리
	    if (collectionRows == null) {
	        collectionRows = new ArrayList<>();
	    }

	    // 전체 결과에 사업자 기본 정보 추가
	    result.put("businessInfo", businessInfo);

	    // 전체 결과에 요약 카드 데이터 추가
	    result.put("summary", summary);

	    // 전체 결과에 최근 입금 내역 추가
	    result.put("depositAccounts", depositAccounts);

	    // 전체 결과에 거래처별 수금 이력 추가
	    result.put("collectionRows", collectionRows);

	    // 프론트로 반환
	    return result;
	}
	
	// 카드 삭제
	public int deleteCard(Long cardSeq) {
	    return dao.deleteCard(cardSeq);
	}
	
}