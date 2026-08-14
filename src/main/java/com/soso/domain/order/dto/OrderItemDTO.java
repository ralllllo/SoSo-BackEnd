package com.soso.domain.order.dto;

public class OrderItemDTO { 
	
	private int itemSeq;
	private int userSeq;
	private int storeSeq;
	private String partnerId;
	private String partnerName;
	private int categorySeq;
	private String categoryName;
	private String itemCode;
	private String itemName;
	private String spec;
	private int unitPrice;
	private String itemImage;
	
	public OrderItemDTO() {}
	public OrderItemDTO(int itemSeq, int userSeq, int storeSeq, String partnerId, String partnerName, int categorySeq,
			String categoryName, String itemCode, String itemName, String spec, int unitPrice, String itemImage) {
		super();
		this.itemSeq = itemSeq;
		this.userSeq = userSeq;
		this.storeSeq = storeSeq;
		this.partnerId = partnerId;
		this.partnerName = partnerName;
		this.categorySeq = categorySeq;
		this.categoryName = categoryName;
		this.itemCode = itemCode;
		this.itemName = itemName;
		this.spec = spec;
		this.unitPrice = unitPrice;
		this.itemImage = itemImage;
	}
	
	public int getItemSeq() {
		return itemSeq;
	}
	public void setItemSeq(int itemSeq) {
		this.itemSeq = itemSeq;
	}
	public int getUserSeq() {
		return userSeq;
	}
	public void setUserSeq(int userSeq) {
		this.userSeq = userSeq;
	}
	public int getStoreSeq() {
		return storeSeq;
	}
	public void setStoreSeq(int storeSeq) {
		this.storeSeq = storeSeq;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getPartnerName() {
		return partnerName;
	}
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	public int getCategorySeq() {
		return categorySeq;
	}
	public void setCategorySeq(int categorySeq) {
		this.categorySeq = categorySeq;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	public int getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(int unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getItemImage() {
		return itemImage;
	}
	public void setItemImage(String itemImage) {
		this.itemImage = itemImage;
	}
}
