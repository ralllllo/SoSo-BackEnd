package com.soso.domain.order.dto;

import java.util.Date;


public class PartnerOrderListDTO {
    private Long orderSeq;
    private String orderNo;
    private String companyName;
    private Long totalAmount;
    private String status;
    private Date createdAt;
    
    
    private String zonecode;
    private String address1;
    private String address2;
    private String orderMemo;
    private String itemSummary;

    public PartnerOrderListDTO() {}

    public PartnerOrderListDTO(Long orderSeq, String orderNo, String companyName, Long totalAmount, String status, Date createdAt, String zonecode, String address1, String address2, String orderMemo) {
        this.orderSeq = orderSeq;
        this.orderNo = orderNo;
        this.companyName = companyName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.zonecode = zonecode;
        this.address1 = address1;
        this.address2 = address2;
        this.orderMemo = orderMemo;
    }

    public PartnerOrderListDTO(Long orderSeq, String orderNo, String companyName, Long totalAmount, String status, Date createdAt, String zonecode, String address1, String address2, String orderMemo, String itemSummary) {
        this.orderSeq = orderSeq;
        this.orderNo = orderNo;
        this.companyName = companyName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.zonecode = zonecode;
        this.address1 = address1;
        this.address2 = address2;
        this.orderMemo = orderMemo;
        this.itemSummary = itemSummary;
    }

    public Long getOrderSeq() { return orderSeq; }
    public void setOrderSeq(Long orderSeq) { this.orderSeq = orderSeq; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public Long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Long totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public String getZonecode() { return zonecode; }
    public void setZonecode(String zonecode) { this.zonecode = zonecode; }
    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }
    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }
    public String getOrderMemo() { return orderMemo; }
    public void setOrderMemo(String orderMemo) { this.orderMemo = orderMemo; }
    public String getItemSummary() { return itemSummary; }
    public void setItemSummary(String itemSummary) { this.itemSummary = itemSummary; }

    @Override
    public String toString() {
        return "PartnerOrderListDTO [orderSeq=" + orderSeq + ", orderNo=" + orderNo + ", companyName=" + companyName
                + ", totalAmount=" + totalAmount + ", status=" + status + ", createdAt=" + createdAt
                + ", zonecode=" + zonecode + ", address1=" + address1 + ", address2=" + address2 
                + ", orderMemo=" + orderMemo + ", itemSummary=" + itemSummary + "]";
    }
}
