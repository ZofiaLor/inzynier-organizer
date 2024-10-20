package org.backend.organizer.Model;

import jakarta.persistence.*;
import org.backend.organizer.CompositeKeys.UserDirectoryId;

@Entity
public class AccessDirectory {
    @EmbeddedId
    private UserDirectoryId id;
    @Column(name = "access_privilege")
    private int accessPrivilege;

    public UserDirectoryId getId() {
        return id;
    }

    public void setId(UserDirectoryId id) {
        this.id = id;
    }

    public int getAccessPrivilege() {
        return accessPrivilege;
    }

    public void setAccessPrivilege(int accessPrivilege) {
        this.accessPrivilege = accessPrivilege;
    }
}
