package org.backend.organizer.DTO;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private Long user;
    private Long file;
    private String message;
    private LocalDateTime sendTimeSetting;
    private boolean isSent;
    private boolean isRead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getFile() {
        return file;
    }

    public void setFile(Long file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSendTimeSetting() {
        return sendTimeSetting;
    }

    public void setSendTimeSetting(LocalDateTime sendTimeSetting) {
        this.sendTimeSetting = sendTimeSetting;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
