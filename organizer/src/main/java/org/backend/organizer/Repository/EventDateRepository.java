package org.backend.organizer.Repository;

import org.backend.organizer.Model.Event;
import org.backend.organizer.Model.EventDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDateRepository extends JpaRepository<EventDate, Long> {
    List<EventDate> getAllByEvent(Event event);
}
