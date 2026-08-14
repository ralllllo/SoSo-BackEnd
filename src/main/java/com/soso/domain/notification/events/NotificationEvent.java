package com.soso.domain.notification.events;

import org.springframework.context.ApplicationEvent;


public class NotificationEvent extends ApplicationEvent {
    
    
    private final int storeSeq;
    
    
    private final String type;
    
    
    private final String title;
    
    
    private final String message;

    
    public NotificationEvent(Object source, int storeSeq, String type, String title, String message) {
        super(source); 
        this.storeSeq = storeSeq;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    
    public int getStoreSeq() { return storeSeq; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
}
