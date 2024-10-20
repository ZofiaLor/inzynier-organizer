package org.backend.organizer.CompositeKeys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserDirectoryId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "directory_id")
    private Long directoryId;

    protected UserDirectoryId() {}

    public UserDirectoryId(Long userId, Long directoryId) {
        this.userId = userId;
        this.directoryId = directoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDirectoryId that = (UserDirectoryId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(directoryId, that.directoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, directoryId);
    }
}
