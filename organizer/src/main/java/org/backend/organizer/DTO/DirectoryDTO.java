package org.backend.organizer.DTO;

import java.util.List;

public class DirectoryDTO {
    private Long id;
    private String name;
    private Long owner;
    private Long parent;
    private List<Long> files;

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

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public List<Long> getFiles() {
        return files;
    }

    public void setFiles(List<Long> files) {
        this.files = files;
    }
}
