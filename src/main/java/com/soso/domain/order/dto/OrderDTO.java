package com.soso.domain.order.dto;

import java.util.List;

public class OrderDTO { 
	
	private Long orderSeq;
	private Long buyerSeq;
	private Integer buyerStoreSeq;
	private Long sellerSeq;
	private Integer totalAmount;
	private String status;
	private String createdAt;
	private String orderNo;
	private String zonecode;
	private String address1;
	private String address2;
	private String orderMemo;
	
	
	private List<OrderSaveItemDTO> items;
	
	public OrderDTO(List<OrderSaveItemDTO> items) {
		super();
		this.items = items;
	}
	
	public List<OrderSaveItemDTO> getItems() {
		return items;
	}

	public void setItems(List<OrderSaveItemDTO> items) {
		this.items = items;
	}

	public OrderDTO() {}
	public OrderDTO(Long orderSeq, Long buyerSeq, Integer buyerStoreSeq, Long sellerSeq, Integer totalAmount, String status, String createdAt,
			String orderNo, String zonecode, String address1, String address2, String orderMemo,
			List<OrderSaveItemDTO> items) {
		super();
		this.orderSeq = orderSeq;
		this.buyerSeq = buyerSeq;
		this.buyerStoreSeq = buyerStoreSeq;
		this.sellerSeq = sellerSeq;
		this.totalAmount = totalAmount;
		this.status = status;
		this.createdAt = createdAt;
		this.orderNo = orderNo;
		this.zonecode = zonecode;
		this.address1 = address1;
		this.address2 = address2;
		this.orderMemo = orderMemo;
		this.items = items;
	}

	public Long getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(Long orderSeq) {
		this.orderSeq = orderSeq;
	}

	public Long getBuyerSeq() {
		return buyerSeq;
	}

	public void setBuyerSeq(Long buyerSeq) {
		this.buyerSeq = buyerSeq;
	}

	public Integer getBuyerStoreSeq() {
		return buyerStoreSeq;
	}

	public void setBuyerStoreSeq(Integer buyerStoreSeq) {
		this.buyerStoreSeq = buyerStoreSeq;
	}

	public Long getSellerSeq() {
		return sellerSeq;
	}

	public void setSellerSeq(Long sellerSeq) {
		this.sellerSeq = sellerSeq;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getZonecode() {
		return zonecode;
	}

	public void setZonecode(String zonecode) {
		this.zonecode = zonecode;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getOrderMemo() {
		return orderMemo;
	}

	public void setOrderMemo(String orderMemo) {
		this.orderMemo = orderMemo;
	}
}
