package org.backend.organizer.Repository;

import org.backend.organizer.Model.Directory;
import org.backend.organizer.Model.File;
import org.backend.organizer.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> getAllByParent(Directory parent);
}
