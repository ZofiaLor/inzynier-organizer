package org.backend.organizer.Repository;

import org.backend.organizer.Model.Task;
import org.backend.organizer.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> getAllByDeadlineBefore(LocalDateTime deadline);
    List<Task> getAllByDeadlineAfter(LocalDateTime deadline);
    List<Task> getAllByOwner(User owner);
}
