package org.backend.organizer.Model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import org.backend.organizer.CompositeKeys.UserFileId;

@Entity
public class AccessFile {
    @EmbeddedId
    private UserFileId id;
    @Column(name = "access_privilege")
    private int accessPrivilege;

    public UserFileId getId() {
        return id;
    }

    public void setId(UserFileId id) {
        this.id = id;
    }

    public int getAccessPrivilege() {
        return accessPrivilege;
    }

    public void setAccessPrivilege(int accessPrivilege) {
        this.accessPrivilege = accessPrivilege;
    }
}
