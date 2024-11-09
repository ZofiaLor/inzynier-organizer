package org.backend.organizer.Repository;

import org.backend.organizer.Model.Directory;
import org.backend.organizer.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {
    List<Directory> getAllByOwner(User owner);
    List<Directory> getAllByParent(Directory parent);
    List<Directory> getAllByOwnerAndParentIsNull(User owner);
}
