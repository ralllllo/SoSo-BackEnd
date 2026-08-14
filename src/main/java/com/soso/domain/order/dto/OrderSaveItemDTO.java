package com.soso.domain.order.dto;

public class OrderSaveItemDTO { 
	
	private Long orderItemSeq;
	private Long orderSeq;
	private Long itemSeq;
	private String itemName;
	private String categoryName;
	private Integer quantity;
	private String spec;
	private Integer unitPrice; 
	private Integer totalPrice;
	
	public OrderSaveItemDTO() {}
	public OrderSaveItemDTO(Long orderItemSeq, Long orderSeq, Long itemSeq, String itemName, String categoryName,
			Integer quantity, String spec, Integer unitPrice, Integer totalPrice) {
		super();
		this.orderItemSeq = orderItemSeq;
		this.orderSeq = orderSeq;
		this.itemSeq = itemSeq;
		this.itemName = itemName;
		this.categoryName = categoryName;
		this.quantity = quantity;
		this.spec = spec;
		this.unitPrice = unitPrice;
		this.totalPrice = totalPrice;
	}
	
	public Long getOrderItemSeq() {
		return orderItemSeq;
	}
	public void setOrderItemSeq(Long orderItemSeq) {
		this.orderItemSeq = orderItemSeq;
	}
	public Long getOrderSeq() {
		return orderSeq;
	}
	public void setOrderSeq(Long orderSeq) {
		this.orderSeq = orderSeq;
	}
	public Long getItemSeq() {
		return itemSeq;
	}
	public void setItemSeq(Long itemSeq) {
		this.itemSeq = itemSeq;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	public Integer getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Integer unitPrice) {
		this.unitPrice = unitPrice;
	}
	public Integer getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}
}
