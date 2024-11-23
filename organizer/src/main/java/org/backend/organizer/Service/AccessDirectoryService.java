package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.Model.AccessDirectory;
import org.backend.organizer.Repository.AccessDirectoryRepository;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccessDirectoryService {
    @Autowired
    AccessDirectoryRepository repository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DirectoryRepository directoryRepository;

    public List<AccessDirectory> getAccessDirectoryByUser(Long userId) {
        if (userId == null) throw new NullPointerException();
        return repository.findAccessDirectoryByUser(userId);
    }

    public List<AccessDirectory> getAccessDirectoryByDirectory(Long dirId) {
        if (dirId == null) throw new NullPointerException();
        return repository.findAccessDirectoryByDirectory(dirId);
    }

    public Optional<AccessDirectory> getAccessDirectory(Long userId, Long directoryId) {
        if (userId == null || directoryId == null) throw new NullPointerException();
        return repository.findAccessDirectory(userId, directoryId);
    }

    public AccessDirectory createOrUpdateAccessDirectory(AccessDirectory ad) {
        if (ad.getId().getUserId() == null | ad.getId().getDirectoryId() == null) throw new NullPointerException();
        if (userRepository.existsById(ad.getId().getUserId()) && directoryRepository.existsById(ad.getId().getDirectoryId())) {
            return repository.save(ad);
        } else {
            throw new EntityNotFoundException();
        }
    }

    public void deleteAccessDirectory(Long userId, Long directoryId) {
        if (userId == null || directoryId == null) throw new NullPointerException();
        AccessDirectory ad = repository.findAccessDirectory(userId, directoryId).orElseThrow(EntityNotFoundException::new);
        repository.delete(ad);
    }
}
