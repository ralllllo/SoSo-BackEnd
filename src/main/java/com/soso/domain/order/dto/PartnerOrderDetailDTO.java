package com.soso.domain.order.dto;


public class PartnerOrderDetailDTO {
    
    private Long orderItemSeq;
    
    
    private String itemName;
    
    
    private String categoryName;
    
    
    private Integer quantity;
    
    
    private String spec;
    
    
    private Integer unitPrice;
    
    
    private Integer totalPrice;

    
    public PartnerOrderDetailDTO() {}

    
    public PartnerOrderDetailDTO(Long orderItemSeq, String itemName, String categoryName, Integer quantity, String spec, Integer unitPrice, Integer totalPrice) {
        this.orderItemSeq = orderItemSeq;
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

    
    @Override
    public String toString() {
        return "PartnerOrderDetailDTO [orderItemSeq=" + orderItemSeq + ", itemName=" + itemName + ", categoryName="
                + categoryName + ", quantity=" + quantity + ", spec=" + spec + ", unitPrice=" + unitPrice
                + ", totalPrice=" + totalPrice + "]";
    }
}
