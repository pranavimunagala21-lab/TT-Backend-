package com.campussphere.dto;

import com.campussphere.entity.Notification;
import java.time.LocalDateTime;

public class NotificationDto {
    private Long id;
    private Long recipientId;
    private String title;
    private String message;
    private String type;
    private Boolean read;
    private String link;
    private LocalDateTime createdAt;

    public NotificationDto() {}

    public NotificationDto(Notification notification) {
        if (notification != null) {
            this.id = notification.getId();
            if (notification.getRecipient() != null) {
                this.recipientId = notification.getRecipient().getId();
            }
            this.title = notification.getTitle();
            this.message = notification.getMessage();
            this.type = notification.getType();
            this.read = notification.getIsRead();
            this.link = notification.getLink();
            this.createdAt = notification.getCreatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
