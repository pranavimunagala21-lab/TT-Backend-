package com.campussphere.service;

import com.campussphere.dto.NotificationDto;
import com.campussphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void notifyOnce(User recipient, String title, String message, String type, String link, String eventKey);
    Page<NotificationDto> list(String email, Pageable pageable);
    long unreadCount(String email);
    NotificationDto markRead(Long id, String email);
}
