package com.soso.domain.mypage.dto;

import java.time.LocalDate;

public class BusinessMypageDTO {
    
    private String userId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String createdAt;

    
    private Long storeSeq;       
    private String bizNumber;
    private String companyName;
    private String ceoName;      
    private LocalDate openingDate;
    private String zonecode;
    private String address1;
    private String address2;

    
    private String profileImageUrl;

    
    private String alertStockYn;
    private String alertExpiryYn;
    private String alertOrderYn;

    public BusinessMypageDTO() {}
    
    public String getCeoName() {
        return ceoName;
    }

    public void setCeoName(String ceoName) {
        this.ceoName = ceoName;
    }
    
    public Long getStoreSeq() {
        return storeSeq;
    }

    public void setStoreSeq(Long storeSeq) {
        this.storeSeq = storeSeq;
    }
    
    public String getCreatedAt() {
    	return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
    	this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBizNumber() {
        return bizNumber;
    }

    public void setBizNumber(String bizNumber) {
        this.bizNumber = bizNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getAlertStockYn() {
        return alertStockYn;
    }

    public void setAlertStockYn(String alertStockYn) {
        this.alertStockYn = alertStockYn;
    }

    public String getAlertExpiryYn() {
        return alertExpiryYn;
    }

    public void setAlertExpiryYn(String alertExpiryYn) {
        this.alertExpiryYn = alertExpiryYn;
    }

    public String getAlertOrderYn() {
        return alertOrderYn;
    }

    public void setAlertOrderYn(String alertOrderYn) {
        this.alertOrderYn = alertOrderYn;
    }
}
