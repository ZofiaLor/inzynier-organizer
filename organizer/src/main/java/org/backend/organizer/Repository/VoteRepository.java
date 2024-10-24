package org.backend.organizer.Repository;

import org.backend.organizer.Model.EventDate;
import org.backend.organizer.Model.User;
import org.backend.organizer.Model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> getAllByUser(User user);
    List<Vote> getAllByEventDate(EventDate eventDate);
}
