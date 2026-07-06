package com.soso.domain.order.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.soso.domain.order.dao.OrderDAO;
import com.soso.domain.order.dto.OrderDTO;
import com.soso.domain.order.dto.OrderItemDTO;
import com.soso.domain.order.dto.OrderListDTO;
import com.soso.domain.order.dto.OrderRecommendDTO;
import com.soso.domain.order.dto.OrderSaveItemDTO;

@Service
public class OrderService {
	
	@Autowired OrderDAO dao;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private org.springframework.context.ApplicationEventPublisher eventPublisher;

	@Autowired
	private com.soso.domain.notification.dao.NotificationDAO notificationDAO;
	
	// 사업자 재고 비교
	public List<OrderRecommendDTO> recommendStock(String itemName, Long storeSeq) {
	    return dao.recommendStock(itemName, storeSeq);
	}
	
	// 거래처 품목 목록
	public List<OrderItemDTO> compareItem(OrderItemDTO dto) {
		
		return dao.compareItem(dto);
	}
	
	// 사업자명과 주소
	public Map<String, Object> identityCheck(Long storeSeq) {
	    return dao.identityCheck(storeSeq);
	}
	
	// 발주 신청시 공급업체 목록 조회
	public List<OrderItemDTO> suppliers() {
	    return dao.suppliers();
	}
	
	// 발주서 작성
	public int orderForm(OrderDTO dto) {
		
		// 1. orders테이블에 발주 기본 정보 저장
		int result = dao.orderForm(dto);
		
		// 2. 방금 생성된 order_seq 가져오기
		Long orderSeq = dto.getOrderSeq();
		
		// ↓ DB가 자동으로 만들어준 orderSeq를 이용해서 발주번호 orderNo를 만들고, 다시 orders 테이블에 저장하는 코드
		
		// 3. 오늘 날짜 생성  
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));  

		// 4. 발주번호 생성  
		String orderNo = String.format("ORD-%s-%03d", today, orderSeq);  

		// 5. DTO에 발주번호 넣기  
		dto.setOrderNo(orderNo);  

		// 6. orders 테이블에 order_no 업데이트  
		dao.updateOrderNo(dto);
		
		// 7. 발주 품목 목록 저장
	    for (OrderSaveItemDTO item : dto.getItems()) {
	        item.setOrderSeq(orderSeq);
	        dao.orderItem(item);
	    }
	    
		 // 8. RAG 발주 문서 실시간 upsert
		 // 발주 기본정보 + 발주 품목이 모두 DB에 저장된 뒤에 실행해야 함
		 // 그래야 챗봇이 "가장 최근 발주 품목 알려줘"라고 했을 때 품목명/수량/단가까지 답할 수 있음
		 try {
		     upsertOrderRag(orderSeq);
		 } catch (Exception e) {
		     // RAG upsert가 실패해도 발주 등록 자체는 실패하면 안 되기 때문에 try-catch로 감쌈
		     e.printStackTrace();
		 }

		// [초보자 가이드 - 실시간 알림 연동]
		// 기능: 가맹점 사장님이 거래처(파트너)로 발주서를 전송하면, 해당 거래처 사장님 대시보드에 실시간으로 알림을 발생시키고 DB에 저장합니다.
		try {
			// 1. 발주서의 판매자(sellerSeq) 정보가 있는지 확인
			if (dto.getSellerSeq() != null) {
				// 2. 판매자 userSeq에 매핑된 파트너 매장의 storeSeq를 가져옴
				Integer sellerStoreSeq = notificationDAO.selectStoreSeqByUserSeq(dto.getSellerSeq());
				if (sellerStoreSeq != null) {
					// 3. 발주한 구매자(buyerSeq)의 매장 storeSeq를 가져와서 가맹점 상호명(예: 교촌치킨 옥길점)을 조회
					Integer buyerStoreSeq = notificationDAO.selectStoreSeqByUserSeq(dto.getBuyerSeq());
					String buyerName = "가맹점";
					if (buyerStoreSeq != null) {
						String company = notificationDAO.selectCompanyNameByStoreSeq(buyerStoreSeq);
						if (company != null && !company.isEmpty()) {
							buyerName = company;
						}
					}
					// 4. Spring의 비비동기 이벤트 시스템(ApplicationEventPublisher)을 통해 
					// 파트너 매장(sellerStoreSeq) 앞으로 "NEW_ORDER" (신규 발주서) 타입의 알림 이벤트를 발행합니다.
					// 이 이벤트는 NotificationService로 전달되어 DB에 인서트되고 웹소켓 토픽을 통해 실시간으로 프론트엔드에 전송됩니다.
					eventPublisher.publishEvent(new com.soso.domain.notification.events.NotificationEvent(
						this,
						sellerStoreSeq,
						"NEW_ORDER",
						"신규 발주서 도착",
						String.format("[%s] 신규 발주서가 등록되었습니다. (발주번호: %s)", buyerName, orderNo)
					));
				}
			}
		} catch (Exception e) {
		}

	    return result;
	}
	
	
	// 발주 RAG upsert
	// 새 발주가 등록되면 FastAPI /rag/upsert로 발주 정보를 보내서
	// Qdrant에 실시간으로 반영하는 메서드
	private void upsertOrderRag(Long orderSeq) {

	    // 1. 방금 등록된 발주 정보를 DB에서 다시 조회
	    // 이유: dto에 있는 값만 쓰면 거래처명, 품목명, 수량 같은 join 정보가 부족할 수 있음
	    Map<String, Object> order = dao.selectOrderRag(orderSeq);

	    // 2. 조회 결과가 없으면 RAG 저장하지 않고 종료
	    if (order == null) {
	        return;
	    }

	    // 3. 챗봇이 읽을 수 있는 자연어 문장 만들기
	    // 이 text가 Qdrant에 저장되고, 챗봇 답변의 기준이 됨
	    String text = String.format(
	    	    "발주 고유번호는 %s번입니다. " +
	    	    "발주서 번호는 %s입니다. " +
	    	    "발주 일시는 %s입니다. " +
	    	    "발주한 사업자 매장은 %s이고, 발주를 받은 거래처는 %s입니다. " +
	    	    "발주 상태는 %s입니다. " +
	    	    "발주 총 금액은 %s원입니다. " +
	    	    "발주 품목은 %s입니다. " +
	    	    "배송 주소는 %s입니다. " +
	    	    "배송 요청 사항은 %s입니다.",
	    	    order.get("orderSeq"),
	    	    order.get("orderNo"),
	    	    order.get("orderDate"),
	    	    order.get("buyerName"),
	    	    order.get("sellerName"),
	    	    order.get("status"),
	    	    order.get("totalAmount"),
	    	    order.get("itemSummary"),
	    	    order.get("deliveryAddress"),
	    	    order.get("deliveryNotes")
	    	);

	    // 4. 검색 필터나 추적용으로 쓸 메타데이터 만들기
	    Map<String, Object> metadata = new HashMap<>();
	    metadata.put("orderSeq", order.get("orderSeq"));
	    metadata.put("orderNo", order.get("orderNo"));
	    metadata.put("buyerName", order.get("buyerName"));
	    metadata.put("sellerName", order.get("sellerName"));
	    metadata.put("status", order.get("status"));
	    metadata.put("totalAmount", order.get("totalAmount"));
	    metadata.put("orderDate", order.get("orderDate"));


	    // 6. 콘솔 확인용 로그
	}
	

	// 발주서 상세 내역 조회
	public Map<String, Object> getOrderDetail(Long orderSeq) {
		Map<String, Object> result = new HashMap<>();
		
		// 1. 발주서 기본 정보 (상대 거래처명 포함)
		Map<String, Object> orderInfo = dao.findOrderInfoBySeq(orderSeq);
		result.put("orderInfo", orderInfo);
		
		// 2. 발주 품목 리스트
		List<Map<String, Object>> items = dao.findOrderItemsBySeq(orderSeq);
		result.put("items", items);
		
		return result;
	}
	
	// 발주서 목록 출력 + 검색 및 필터링
	public List<OrderListDTO> orderList(
	        Long storeSeq,
	        Integer offset,
	        String keyword,
	        String status,
	        String startDate,
	        String endDate
	) {
	    if (offset == null) {
	        offset = 0;
	    }

	    return dao.orderList(storeSeq, offset, keyword, status, startDate, endDate);
	}
	
	
	// 웹소켓
	// 발주 상태 변경 + 사업자 화면에 웹소켓 알림 전송
	// 발주 상태가 바뀌면 RAG 문서도 다시 upsert해서 챗봇이 최신 상태로 답하게 함
	public void updateOrderStatus(Long orderSeq, String status) {
		

	    // 1. orders 테이블의 발주 상태 변경
	    // 예: REQUESTED → ACCEPTED → PREPARING → SHIPPING → DELIVERED
	    int updated = dao.updateOrderStatus(orderSeq, status);

	    // 2. 상태 변경된 발주가 없으면 더 이상 진행하지 않음
	    if (updated <= 0) {
	        return;
	    }

	    // 3. RAG 발주 문서 다시 upsert
	    // 이유:
	    // 기존 Qdrant 문서에는 예전 상태가 들어있을 수 있음.
	    // 상태 변경 후 DB에서 다시 조회해서 같은 order 문서를 덮어쓰기 함.
	    try {
	        upsertOrderRag(orderSeq);
	    } catch (Exception e) {
	        // RAG upsert가 실패해도 상태 변경과 웹소켓 알림은 계속 진행해야 함
	        e.printStackTrace();
	    }

	    // 4. 이 발주를 신청한 사업자 user_seq 조회
	    Long buyerSeq = dao.findBuyerSeqByOrderSeq(orderSeq);

	    // 5. 프론트로 보낼 웹소켓 메시지 생성
	    Map<String, Object> message = new HashMap<>();
	    message.put("orderSeq", orderSeq);
	    message.put("status", status);
	    message.put("message", getStatusMessage(status));

	    // 6. 사업자 화면이 구독 중인 주소로 웹소켓 전송

	    messagingTemplate.convertAndSend("/sub/order/" + buyerSeq, (Object) message);

	    // 7. 알림 이벤트 발행
	    try {
	        Map<String, Object> orderInfo = dao.findOrderInfoBySeq(orderSeq);
	        if (orderInfo != null && buyerSeq != null) {
	            String orderNo = String.valueOf(orderInfo.get("orderNo"));
	            eventPublisher.publishEvent(new com.soso.domain.notification.events.NotificationEvent(
	                this,
	                buyerSeq.intValue(),
	                "ORDER_STATUS",
	                "발주 상태 변경",
	                String.format("발주번호 [%s]의 %s", orderNo, getStatusMessage(status))
	            ));
	        }
	    } catch (Exception e) {
	    }
	}
//	public void updateOrderStatus(Long orderSeq, String status) {
//
//	    // 1. orders 테이블 status 변경
//	    dao.updateOrderStatus(orderSeq, status);
//
//	    // 2. 이 발주를 신청한 사업자 user_seq 조회
//	    Long buyerSeq = dao.findBuyerSeqByOrderSeq(orderSeq);
//
//	    // 3. 프론트로 보낼 데이터
//	    Map<String, Object> message = new HashMap<>();
//	    message.put("orderSeq", orderSeq);
//	    message.put("status", status);
//	    message.put("message", getStatusMessage(status));
//
//	    // 4. 사업자 화면이 구독 중인 주소로 전송
//	    
//	    messagingTemplate.convertAndSend("/sub/order/" + buyerSeq, (Object) message);
//	}

	// 상태별 메시지
	private String getStatusMessage(String status) {
	    switch (status) {
	        case "ACCEPTED":
	            return "발주가 접수완료되었습니다.";
	        case "PREPARING":
	            return "상품준비가 시작되었습니다.";
	        case "SHIPPING":
	            return "상품이 배송중입니다.";
	        case "DELIVERED":
	            return "배송이 완료되었습니다.";
	        default:
	            return "발주 상태가 변경되었습니다.";
	    }
	}
	
	// 발주 미결제
	public List<Map<String, Object>> unpaidOrders(Long storeSeq, Long partnerSeq) {
	    return dao.unpaidOrders(storeSeq, partnerSeq);
	}
	
	
}
