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
	
	
	public List<OrderRecommendDTO> recommendStock(String itemName, Long storeSeq) {
	    return dao.recommendStock(itemName, storeSeq);
	}
	
	
	public List<OrderItemDTO> compareItem(OrderItemDTO dto) {
		
		return dao.compareItem(dto);
	}
	
	
	public Map<String, Object> identityCheck(Long storeSeq) {
	    return dao.identityCheck(storeSeq);
	}
	
	
	public List<OrderItemDTO> suppliers() {
	    return dao.suppliers();
	}
	
	
	public int orderForm(OrderDTO dto) {
		
		
		int result = dao.orderForm(dto);
		
		
		Long orderSeq = dto.getOrderSeq();
		
		
		
		
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));  

		
		String orderNo = String.format("ORD-%s-%03d", today, orderSeq);  

		
		dto.setOrderNo(orderNo);  

		
		dao.updateOrderNo(dto);
		
		
	    for (OrderSaveItemDTO item : dto.getItems()) {
	        item.setOrderSeq(orderSeq);
	        dao.orderItem(item);
	    }
	    
		 
		 
		 
		 try {
		     upsertOrderRag(orderSeq);
		 } catch (Exception e) {
		     
		     e.printStackTrace();
		 }

		
		
		try {
			
			if (dto.getSellerSeq() != null) {
				
				Integer sellerStoreSeq = notificationDAO.selectStoreSeqByUserSeq(dto.getSellerSeq());
				if (sellerStoreSeq != null) {
					
					Integer buyerStoreSeq = notificationDAO.selectStoreSeqByUserSeq(dto.getBuyerSeq());
					String buyerName = "가맹점";
					if (buyerStoreSeq != null) {
						String company = notificationDAO.selectCompanyNameByStoreSeq(buyerStoreSeq);
						if (company != null && !company.isEmpty()) {
							buyerName = company;
						}
					}
					
					
					
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
	
	
	
	
	
	private void upsertOrderRag(Long orderSeq) {

	    
	    
	    Map<String, Object> order = dao.selectOrderRag(orderSeq);

	    
	    if (order == null) {
	        return;
	    }

	    
	    
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

	    
	    Map<String, Object> metadata = new HashMap<>();
	    metadata.put("orderSeq", order.get("orderSeq"));
	    metadata.put("orderNo", order.get("orderNo"));
	    metadata.put("buyerName", order.get("buyerName"));
	    metadata.put("sellerName", order.get("sellerName"));
	    metadata.put("status", order.get("status"));
	    metadata.put("totalAmount", order.get("totalAmount"));
	    metadata.put("orderDate", order.get("orderDate"));


	    
	}
	

	
	public Map<String, Object> getOrderDetail(Long orderSeq) {
		Map<String, Object> result = new HashMap<>();
		
		
		Map<String, Object> orderInfo = dao.findOrderInfoBySeq(orderSeq);
		result.put("orderInfo", orderInfo);
		
		
		List<Map<String, Object>> items = dao.findOrderItemsBySeq(orderSeq);
		result.put("items", items);
		
		return result;
	}
	
	
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
	
	
	
	
	
	public void updateOrderStatus(Long orderSeq, String status) {
		

	    
	    
	    int updated = dao.updateOrderStatus(orderSeq, status);

	    
	    if (updated <= 0) {
	        return;
	    }

	    
	    
	    
	    
	    try {
	        upsertOrderRag(orderSeq);
	    } catch (Exception e) {
	        
	        e.printStackTrace();
	    }

	    
	    Long buyerSeq = dao.findBuyerSeqByOrderSeq(orderSeq);

	    
	    Map<String, Object> message = new HashMap<>();
	    message.put("orderSeq", orderSeq);
	    message.put("status", status);
	    message.put("message", getStatusMessage(status));

	    

	    messagingTemplate.convertAndSend("/sub/order/" + buyerSeq, (Object) message);

	    
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
	
	
	public List<Map<String, Object>> unpaidOrders(Long storeSeq, Long partnerSeq) {
	    return dao.unpaidOrders(storeSeq, partnerSeq);
	}
	
	
}
