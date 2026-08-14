package com.soso.domain.payment.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.soso.domain.payment.dto.AutoPaymentScheduleDTO;
import com.soso.domain.payment.dto.PaymentCardDTO;
import com.soso.domain.payment.dto.PaymentDTO;

@Repository
public class PaymentDAO {
	
	@Autowired
	private SqlSessionTemplate mybatis;
	
	
	public int insertAccount(PaymentDTO dto) {
		return mybatis.insert("payment.account", dto);
	}
	
	
	public List<PaymentDTO> accountList(Long storeSeq) {
		return mybatis.selectList("payment.list", storeSeq);
	}
	
	
	public int accountCount(Long storeSeq) {
		return mybatis.selectOne("payment.limit", storeSeq);
	}
	
	
	public int accountDel(Long accountSeq) {
		return mybatis.update("payment.accountDel", accountSeq);
	}
	
	
	public int autoPaymentSchedule(AutoPaymentScheduleDTO dto) {
		return mybatis.insert("payment.autoSchedule", dto);
	}
	
	
	public int insertCard(PaymentCardDTO dto) {
		return mybatis.insert("payment.insertCard", dto);
	}
	
	
	public List<PaymentCardDTO> selectCard(Long storeSeq) {
		return mybatis.selectList("payment.selectCard", storeSeq);
	}
	
	
	public PaymentCardDTO selectCardForPayment(Integer storeSeq, Integer cardSeq) {

	    
	    Map<String, Object> params = new HashMap<>();
	    params.put("storeSeq", storeSeq);
	    params.put("cardSeq", cardSeq);

	    
	    return mybatis.selectOne("payment.selectCardForPayment", params);
	}
	
		
	
	
	
	
	
	public List<Map<String, Object>> selectOrdersForPayment(
	        Integer storeSeq,
	        Integer partnerSeq,
	        List<Integer> orderSeqList) {

	    
	    Map<String, Object> params = new HashMap<>();
	    params.put("storeSeq", storeSeq);
	    params.put("partnerSeq", partnerSeq);
	    params.put("orderSeqList", orderSeqList);

	    
	    return mybatis.selectList("payment.selectOrdersForPayment", params);
	}
	
	
	
	
	
	
	
	public List<Map<String, Object>> selectRecentPayments(
	        Integer storeSeq,
	        String period,
	        String startDate,
	        String endDate,
	        String keyword) {

	    
	    Map<String, Object> params = new HashMap<>();

	    
	    params.put("storeSeq", storeSeq);

	    
	    params.put("period", period);

	    
	    params.put("startDate", startDate);

	    
	    params.put("endDate", endDate);

	    
	    params.put("keyword", keyword);

	    
	    return mybatis.selectList("payment.selectRecentPayments", params);
	}
	
	
	
	public Integer insertPayment(
	        Integer storeSeq,
	        Integer partnerSeq,
	        Integer cardSeq,
	        String paymentId,
	        Integer totalAmount) {

	    
	    Map<String, Object> params = new HashMap<>();
	    params.put("storeSeq", storeSeq);       
	    params.put("partnerSeq", partnerSeq);   
	    params.put("cardSeq", cardSeq);         
	    params.put("paymentId", paymentId);     
	    params.put("totalAmount", totalAmount); 

	    
	    
	    mybatis.insert("payment.insertPayment", params);

	    
	    return ((Number) params.get("paymentSeq")).intValue();
	}


	
	public int insertPaymentOrderMap(Integer paymentSeq, List<Integer> orderSeqList) {

	    
	    Map<String, Object> params = new HashMap<>();
	    params.put("paymentSeq", paymentSeq);       
	    params.put("orderSeqList", orderSeqList);   

	    
	    return mybatis.insert("payment.insertPaymentOrderMap", params);
	}


	
	public int insertExpensesForPaidOrders(Integer storeSeq, List<Integer> orderSeqList) {

	    
	    Map<String, Object> params = new HashMap<>();
	    params.put("storeSeq", storeSeq);           
	    params.put("orderSeqList", orderSeqList);   

	    
	    return mybatis.insert("payment.insertExpensesForPaidOrders", params);
	}
	
	
	public Map<String, Object> selectCollectionBusinessInfo(Long storeSeq) {

	    
	    return mybatis.selectOne("payment.selectCollectionBusinessInfo", storeSeq);
	}


	
	public Map<String, Object> selectCollectionSummary(Long storeSeq) {

	    
	    return mybatis.selectOne("payment.selectCollectionSummary", storeSeq);
	}


	
	public List<Map<String, Object>> selectCollectionDepositAccounts(Long storeSeq) {

	    
	    return mybatis.selectList("payment.selectCollectionDepositAccounts", storeSeq);
	}


	
	public List<Map<String, Object>> selectCollectionRows(Long storeSeq) {

	    
	    return mybatis.selectList("payment.selectCollectionRows", storeSeq);
	}
	
	
	public int deleteCard(Long cardSeq) {
	    return mybatis.update("payment.deleteCard", cardSeq);
	}

}
