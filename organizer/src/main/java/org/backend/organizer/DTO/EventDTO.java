package org.backend.organizer.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class EventDTO extends FileDTO{
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private List<Long> eventDates;

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

    public List<Long> getEventDates() {
        return eventDates;
    }

    public void setEventDates(List<Long> eventDates) {
        this.eventDates = eventDates;
    }
}
