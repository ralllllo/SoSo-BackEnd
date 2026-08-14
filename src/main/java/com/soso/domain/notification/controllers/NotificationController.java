package com.soso.domain.notification.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soso.domain.notification.dto.NotificationDTO;
import com.soso.domain.notification.services.NotificationService;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    
    @Autowired
    private NotificationService notificationService;

    
    @GetMapping("/recent")
    public ResponseEntity<List<NotificationDTO>> getRecentNotifications(@RequestParam int storeSeq) {
        
        List<NotificationDTO> list = notificationService.getRecentNotifications(storeSeq);
        
        return ResponseEntity.ok(list);
    }

    
    @PatchMapping("/{notificationSeq}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable int notificationSeq) {
        
        notificationService.markAsRead(notificationSeq);
        
        return ResponseEntity.ok().build();
    }
}
