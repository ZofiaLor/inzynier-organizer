package org.backend.organizer.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class FileDTO {
    private Long id;
    private String name;
    private String textContent;
    private LocalDateTime creationDate;
    private Long parent;
    private Long owner;

    private List<Long> notifications;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public List<Long> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Long> notifications) {
        this.notifications = notifications;
    }
}
