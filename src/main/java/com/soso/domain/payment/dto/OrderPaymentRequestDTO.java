package com.soso.domain.payment.dto;

import java.util.List;

public class OrderPaymentRequestDTO {
	
	private Integer storeSeq;
	private Integer partnerSeq;
	private Integer cardSeq;
	private List<Integer> orderSeqList; 
	
	public OrderPaymentRequestDTO() {}
	public OrderPaymentRequestDTO(Integer storeSeq, Integer partnerSeq, Integer cardSeq, List<Integer> orderSeqList) {
		super();
		this.storeSeq = storeSeq;
		this.partnerSeq = partnerSeq;
		this.cardSeq = cardSeq;
		this.orderSeqList = orderSeqList;
	}
	
	public Integer getStoreSeq() {
		return storeSeq;
	}
	public void setStoreSeq(Integer storeSeq) {
		this.storeSeq = storeSeq;
	}
	public Integer getPartnerSeq() {
		return partnerSeq;
	}
	public void setPartnerSeq(Integer partnerSeq) {
		this.partnerSeq = partnerSeq;
	}
	public Integer getCardSeq() {
		return cardSeq;
	}
	public void setCardSeq(Integer cardSeq) {
		this.cardSeq = cardSeq;
	}
	public List<Integer> getOrderSeqList() {
		return orderSeqList;
	}
	public void setOrderSeqList(List<Integer> orderSeqList) {
		this.orderSeqList = orderSeqList;
	}
}
