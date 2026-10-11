package com.campussphere.service.impl;

import com.campussphere.dto.NotificationDto;
import com.campussphere.entity.Notification;
import com.campussphere.entity.User;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.exception.ForbiddenException;
import com.campussphere.repository.NotificationRepository;
import com.campussphere.repository.UserRepository;
import com.campussphere.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notifications;
    private final UserRepository users;
    public NotificationServiceImpl(NotificationRepository notifications, UserRepository users) { this.notifications=notifications; this.users=users; }

    @Override
    @Transactional
    public void notifyOnce(User recipient, String title, String message, String type, String link, String eventKey) {
        if (recipient==null || eventKey==null || notifications.existsByEventKey(eventKey)) return;
        Notification n=new Notification(recipient,title,message,type,link); n.setEventKey(eventKey);
        notifications.save(n);
    }

    @Override
    @Transactional(readOnly=true)
    public Page<NotificationDto> list(String email, Pageable pageable) { User user=user(email); return notifications.findByRecipientIdOrderByCreatedAtDesc(user.getId(),pageable).map(NotificationDto::new); }

    @Override
    @Transactional(readOnly=true)
    public long unreadCount(String email) { return notifications.countByRecipientIdAndIsReadFalse(user(email).getId()); }

    @Override
    @Transactional
    public NotificationDto markRead(Long id,String email) {
        User user=user(email); Notification n=notifications.findByIdAndRecipientId(id,user.getId()).orElseThrow(()->new ResourceNotFoundException("Notification not found"));
        if (!n.getRecipient().getId().equals(user.getId())) throw new ForbiddenException("Cannot modify another user's notification");
        n.setIsRead(true); return new NotificationDto(notifications.save(n));
    }
    private User user(String email) { return users.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found")); }
}
