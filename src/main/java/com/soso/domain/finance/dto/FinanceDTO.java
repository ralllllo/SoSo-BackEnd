package com.soso.domain.finance.dto;

import java.sql.Date;
import java.sql.Timestamp;

public class FinanceDTO {
    private int financeSeq;
    private int userSeq;
    private Integer storeSeq;
    private String type; 
    private int amount;
    private String category;
    private String description;
    private Date targetDate;
    private Timestamp createdAt;

    public FinanceDTO() {}

    public int getFinanceSeq() { return financeSeq; }
    public void setFinanceSeq(int financeSeq) { this.financeSeq = financeSeq; }
    public int getUserSeq() { return userSeq; }
    public void setUserSeq(int userSeq) { this.userSeq = userSeq; }
    public Integer getStoreSeq() { return storeSeq; }
    public void setStoreSeq(Integer storeSeq) { this.storeSeq = storeSeq; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getTargetDate() { return targetDate; }
    public void setTargetDate(Date targetDate) { this.targetDate = targetDate; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
