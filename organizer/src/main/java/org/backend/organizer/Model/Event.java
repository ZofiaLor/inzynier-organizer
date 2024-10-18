package org.backend.organizer.Model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("1")
public class Event extends File{
    @Column(name="start_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;
    @Column(name="end_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;
    private String location;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
