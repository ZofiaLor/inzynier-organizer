package org.backend.organizer.Repository;

import org.backend.organizer.CompositeKeys.UserDirectoryId;
import org.backend.organizer.Model.AccessDirectory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessDirectoryRepository extends JpaRepository<AccessDirectory, UserDirectoryId> {
    @Query("SELECT ad FROM AccessDirectory ad WHERE ad.id.userId = :userId AND ad.id.directoryId = :directoryId")
    Optional<AccessDirectory> findAccessDirectory(Long userId, Long directoryId);
}
