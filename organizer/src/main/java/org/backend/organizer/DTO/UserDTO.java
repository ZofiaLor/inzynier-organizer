package org.backend.organizer.DTO;

import java.util.List;

public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String password;
    private String role;
    private List<Long> directories;
    private List<Long> votes;
    private List<Long> notifications;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Long> getDirectories() {
        return directories;
    }

    public void setDirectories(List<Long> directories) {
        this.directories = directories;
    }

    public List<Long> getVotes() {
        return votes;
    }

    public void setVotes(List<Long> votes) {
        this.votes = votes;
    }

    public List<Long> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Long> notifications) {
        this.notifications = notifications;
    }
}
