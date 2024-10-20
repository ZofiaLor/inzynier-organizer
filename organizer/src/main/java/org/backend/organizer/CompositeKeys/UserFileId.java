package org.backend.organizer.CompositeKeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserFileId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "file_id")
    private Long fileId;

    protected UserFileId () {}

    public UserFileId(Long userId, Long fileId) {
        this.userId = userId;
        this.fileId = fileId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFileId that = (UserFileId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, fileId);
    }
}
