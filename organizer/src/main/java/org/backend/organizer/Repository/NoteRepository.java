package org.backend.organizer.Repository;

import org.backend.organizer.Model.Note;
import org.backend.organizer.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
}
