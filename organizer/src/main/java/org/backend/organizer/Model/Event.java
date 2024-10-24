package org.backend.organizer.Model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("1")
public class Event extends File{
    @Column(name="start_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;
    @Column(name="end_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;
    private String location;
    @OneToMany(mappedBy = "event")
    private List<EventDate> eventDates;

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

    public List<EventDate> getEventDates() {
        return eventDates;
    }

    public void setEventDates(List<EventDate> eventDates) {
        this.eventDates = eventDates;
    }
}
