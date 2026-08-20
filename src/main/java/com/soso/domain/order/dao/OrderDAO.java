package com.soso.domain.order.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.soso.domain.order.dto.OrderDTO;
import com.soso.domain.order.dto.OrderItemDTO;
import com.soso.domain.order.dto.OrderListDTO;
import com.soso.domain.order.dto.OrderRecommendDTO;
import com.soso.domain.order.dto.OrderSaveItemDTO;

@Repository
public class OrderDAO {

	@Autowired
	private SqlSessionTemplate mybatis;

	
	public List<OrderRecommendDTO> recommendStock(String itemName, Long storeSeq) {

	    Map<String, Object> params = new HashMap<>();
	    params.put("itemName", itemName);
	    params.put("storeSeq", storeSeq);

	    return mybatis.selectList("order.stockCheck", params);
	}

	
	public List<OrderItemDTO> compareItem(OrderItemDTO dto) {
		return mybatis.selectList("order.itemCheck", dto);
	}

	
	public Map<String, Object> identityCheck(Long storeSeq) {
		return mybatis.selectOne("order.identityCheck", storeSeq);
	}

	
	public int orderForm(OrderDTO dto) {
		return mybatis.insert("order.orderForm", dto);
	}
	
	
	public List<OrderItemDTO> suppliers() {
	    return mybatis.selectList("order.suppliers");
	}

	
	public int orderItem(OrderSaveItemDTO dto) {
		return mybatis.insert("order.orderItem", dto);
	}

	
	public int updateOrderNo(OrderDTO dto) {
		return mybatis.update("order.updateOrderNo", dto);
	}

	
	
	public List<OrderListDTO> orderList(
	        Long storeSeq,
	        Integer offset,
	        String keyword,
	        String status,
	        String startDate,
	        String endDate
	) {
	    Map<String, Object> params = new HashMap<>();

	    params.put("storeSeq", storeSeq);
	    params.put("offset", offset);
	    params.put("keyword", keyword);
	    params.put("status", status);
	    params.put("startDate", startDate);
	    params.put("endDate", endDate);

	    return mybatis.selectList("order.orderList", params);
	}
	

	
	
	public int updateOrderStatus(Long orderSeq, String status) {
		Map<String, Object> params = new HashMap<>();
		params.put("orderSeq", orderSeq);
		params.put("status", status);

		return mybatis.update("order.updateOrderStatus", params);
	}

	
	public Long findBuyerSeqByOrderSeq(Long orderSeq) {
		return mybatis.selectOne("order.findBuyerSeqByOrderSeq", orderSeq);
	}
	
	
	public Map<String, Object> findOrderInfoBySeq(Long orderSeq) {
		return mybatis.selectOne("order.findOrderInfoBySeq", orderSeq);
	}

	
	public List<Map<String, Object>> findOrderItemsBySeq(Long orderSeq) {
		return mybatis.selectList("order.findOrderItemsBySeq", orderSeq);
	}
	
	
	public List<Map<String, Object>> unpaidOrders(Long storeSeq, Long partnerSeq) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("storeSeq", storeSeq);
	    params.put("partnerSeq", partnerSeq);

	    return mybatis.selectList("order.unpaidOrders", params);
	}
	
	
	
	
	public Map<String, Object> selectOrderRag(Long orderSeq) {

	    
	    
	    return mybatis.selectOne("order.selectOrderRag", orderSeq);
	}
}
