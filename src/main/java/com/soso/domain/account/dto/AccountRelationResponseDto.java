package com.soso.domain.account.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class AccountRelationResponseDto {
    private int relationSeq;
    private int businessSeq;
    private int partnerSeq;
    private String memo;
    private LocalDateTime createdAt;
    
    
    private String companyName;
    private String bizNumber;
    private String ceoName;
    private String address1;
    private String address2;

    public AccountRelationResponseDto() {
    }

    public AccountRelationResponseDto(int relationSeq, int businessSeq, int partnerSeq, String memo, LocalDateTime createdAt, String companyName, String bizNumber, String ceoName, String address1, String address2) {
        this.relationSeq = relationSeq;
        this.businessSeq = businessSeq;
        this.partnerSeq = partnerSeq;
        this.memo = memo;
        this.createdAt = createdAt;
        this.companyName = companyName;
        this.bizNumber = bizNumber;
        this.ceoName = ceoName;
        this.address1 = address1;
        this.address2 = address2;
    }

    public int getRelationSeq() {
        return relationSeq;
    }

    public void setRelationSeq(int relationSeq) {
        this.relationSeq = relationSeq;
    }

    public int getBusinessSeq() {
        return businessSeq;
    }

    public void setBusinessSeq(int businessSeq) {
        this.businessSeq = businessSeq;
    }

    public int getPartnerSeq() {
        return partnerSeq;
    }

    public void setPartnerSeq(int partnerSeq) {
        this.partnerSeq = partnerSeq;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBizNumber() {
        return bizNumber;
    }

    public void setBizNumber(String bizNumber) {
        this.bizNumber = bizNumber;
    }

    public String getCeoName() {
        return ceoName;
    }

    public void setCeoName(String ceoName) {
        this.ceoName = ceoName;
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
}
