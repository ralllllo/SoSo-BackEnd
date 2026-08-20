package com.soso.domain.notification.dto;

import java.time.LocalDateTime;


public class NotificationDTO {
    
    private int notificationSeq;
    
    
    private int storeSeq;
    
    
    private String type;
    
    
    private String title;
    
    
    private String message;
    
    
    private String isRead;
    
    
    private LocalDateTime createdAt;

    
    public NotificationDTO() {}

    
    public NotificationDTO(int storeSeq, String type, String title, String message) {
        this.storeSeq = storeSeq;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    
    public int getNotificationSeq() { return notificationSeq; }
    public void setNotificationSeq(int notificationSeq) { this.notificationSeq = notificationSeq; }
    
    public int getStoreSeq() { return storeSeq; }
    public void setStoreSeq(int storeSeq) { this.storeSeq = storeSeq; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getIsRead() { return isRead; }
    public void setIsRead(String isRead) { this.isRead = isRead; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
