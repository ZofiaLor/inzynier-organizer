package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.Model.AccessFile;
import org.backend.organizer.Repository.AccessFileRepository;
import org.backend.organizer.Repository.FileRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccessFileService {
    @Autowired
    AccessFileRepository repository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FileRepository fileRepository;

    public List<AccessFile> getAllAccessFiles() {
        return repository.findAll();
    }
    public List<AccessFile> getAccessFileByUser(Long userId) {
        if (userId == null) throw new NullPointerException();
        return repository.findAccessFileByUser(userId);
    }

    public Optional<AccessFile> getAccessFile(Long userId, Long fileId) {
        if (userId == null || fileId == null) throw new NullPointerException();
        return repository.findAccessFile(userId, fileId);
    }

    public AccessFile createOrUpdateAccessFile(AccessFile af) {
        if (af.getId().getUserId() == null | af.getId().getFileId() == null) throw new NullPointerException();
        if (userRepository.existsById(af.getId().getUserId()) && fileRepository.existsById(af.getId().getFileId())) {
            return repository.save(af);
        } else {
            throw new EntityNotFoundException();
        }
    }

    public void deleteAccessFile(Long userId, Long fileId) {
        if (userId == null || fileId == null) throw new NullPointerException();
        AccessFile af = repository.findAccessFile(userId, fileId).orElseThrow(EntityNotFoundException::new);
        repository.delete(af);
    }
}
