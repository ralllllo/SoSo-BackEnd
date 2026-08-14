package com.soso.domain.groupbuy.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class GroupBuyDTO {
    private Integer groupBuySeq;
    private Integer userSeq;
    private String creatorType; 
    private String partnerName; 
    private String itemName;    
    private String category;    
    private Integer quantity;   
    private Integer targetParticipants; 
    private Integer currentParticipants;
    private Integer unitPrice;  
    private Integer totalAmount;
    private LocalDateTime endDate; 
    private String pickupLocation; 
    private String pickupTime;     
    private String notice;         
    private String status;
    private LocalDateTime createdAt;
    
    
    private String groupName;
    private String description;

    
    private String paymentStatus;
    private String deliveryStatus;
    private boolean isOwner;

    public GroupBuyDTO() {}

    public Integer getGroupBuySeq() { return groupBuySeq; }
    public void setGroupBuySeq(Integer groupBuySeq) { this.groupBuySeq = groupBuySeq; }

    public Integer getUserSeq() { return userSeq; }
    public void setUserSeq(Integer userSeq) { this.userSeq = userSeq; }

    public String getCreatorType() { return creatorType; }
    public void setCreatorType(String creatorType) { this.creatorType = creatorType; }

    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTargetParticipants() { return targetParticipants; }
    public void setTargetParticipants(Integer targetParticipants) { this.targetParticipants = targetParticipants; }

    public Integer getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Integer unitPrice) { this.unitPrice = unitPrice; }

    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getPickupTime() { return pickupTime; }
    public void setPickupTime(String pickupTime) { this.pickupTime = pickupTime; }

    public String getNotice() { return notice; }
    public void setNotice(String notice) { this.notice = notice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public boolean isOwner() { return isOwner; }
    public void setOwner(boolean owner) { isOwner = owner; }
}
