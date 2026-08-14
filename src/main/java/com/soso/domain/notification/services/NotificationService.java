package com.soso.domain.notification.services;

import com.soso.domain.notification.dao.NotificationDAO;
import com.soso.domain.notification.dto.NotificationDTO;
import com.soso.domain.notification.events.NotificationEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class NotificationService {

    
    @Autowired
    private NotificationDAO notificationDAO;

    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    
    @PostConstruct
    public void init() {
        try {
            notificationDAO.createTableIfNotExists();
        } catch (Exception e) {
        }
    }

    
    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationEvent(NotificationEvent event) {
        
        String configType = mapEventToConfigType(event.getType());
        String isEnabled = notificationDAO.checkNotificationEnabled(event.getStoreSeq(), configType);
        
        
        if ("N".equals(isEnabled)) {
            return;
        }

        
        NotificationDTO notification = new NotificationDTO(
            event.getStoreSeq(),
            event.getType(),
            event.getTitle(),
            event.getMessage()
        );
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead("N"); 

        
        notificationDAO.insertNotification(notification);

        
        String destination = "/sub/store/" + event.getStoreSeq() + "/notifications";
        messagingTemplate.convertAndSend(destination, notification);
    }

    
    private String mapEventToConfigType(String eventType) {
        if (eventType == null) return "COMMON";
        switch (eventType) {
            case "SAFETY_LACK":
            case "STOCK_LACK":
            case "STOCK":
                return "STOCK_SHORTAGE";
            case "EXPIRY":
            case "EXPIRY_LACK":
                return "EXPIRY_IMMINENT";
            case "ORDER":
            case "ORDER_STATUS":
                return "ORDER_STATUS";
            case "CHAT":
                return "CHAT";
            case "MARKETING":
                return "MARKETING";
            case "NIGHT":
                return "NIGHT_RESTRICTION";
            default:
                return "COMMON";
        }
    }

    
    public List<NotificationDTO> getRecentNotifications(int storeSeq) {
        return notificationDAO.selectRecentNotifications(storeSeq);
    }

    
    @Transactional
    public void markAsRead(int notificationSeq) {
        notificationDAO.updateNotificationRead(notificationSeq);
    }
}
