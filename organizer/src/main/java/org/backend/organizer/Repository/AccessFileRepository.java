package org.backend.organizer.Repository;

import org.backend.organizer.CompositeKeys.UserFileId;
import org.backend.organizer.Model.AccessFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessFileRepository extends JpaRepository<AccessFile, UserFileId> {
    @Query("SELECT af FROM AccessFile af WHERE af.id.userId = :userId AND af.id.fileId = :fileId")
    Optional<AccessFile> findAccessFile(Long userId, Long fileId);
}
