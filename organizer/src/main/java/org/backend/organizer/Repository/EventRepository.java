package org.backend.organizer.Repository;

import org.backend.organizer.Model.Event;
import org.backend.organizer.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> getAllByOwner(User owner);
}
