package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.DirectoryDTO;
import org.backend.organizer.Exception.DirectoryAccessForbiddenException;
import org.backend.organizer.Exception.UndefinedAccessException;
import org.backend.organizer.Mapper.DirectoryMapper;
import org.backend.organizer.Model.AccessDirectory;
import org.backend.organizer.Model.Directory;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DirectoryService {
    @Autowired
    DirectoryRepository repository;
    @Autowired
    DirectoryMapper mapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccessDirectoryService adService;

    public List<DirectoryDTO> getAllDirectoriesByParentID(Long parentID, String username) {
        if (parentID == null) throw new NullPointerException();
        Directory parent = repository.findById(parentID).orElseThrow(EntityNotFoundException::new);
        checkAccess(1, parentID, parent.getOwner(), username);
        var result = new ArrayList<DirectoryDTO>();
        for (var dir : repository.getAllByParent(parent)) {
            result.add(mapper.directoryToDirectoryDTO(dir));
        }
        return result;
    }

    public DirectoryDTO getBaseDirectoryByOwner(String username) {
        User user = userRepository.findByUsername(username);
        return mapper.directoryToDirectoryDTO(repository.getByOwnerAndParentIsNull(user));
    }

    public DirectoryDTO getByID(Long id, String username) {
        if (id == null) throw new NullPointerException();
        Directory dir = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        checkAccess(1, id, dir.getOwner(), username);
        return mapper.directoryToDirectoryDTO(dir);
    }

    public boolean checkDirectoryEditAccess(Long id, String username) {
        if (id == null) throw new NullPointerException();
        Directory dir = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        try {
            checkAccess(1, id, dir.getOwner(), username);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public DirectoryDTO createDirectory(DirectoryDTO newDirectory, String username, boolean isNewUser) {
        if (!isNewUser && newDirectory.getParent() == null) throw new NullPointerException();
        Directory directory = mapper.directoryDTOToDirectory(newDirectory);
        User owner = userRepository.findByUsername(username);
        directory.setOwner(owner);
        if (!isNewUser) {
            Directory parent = repository.findById(newDirectory.getParent()).orElseThrow(EntityNotFoundException::new);
            if (parent.getOwner() != owner) {
                throw new IllegalArgumentException();
            }
        }
        if (newDirectory.getName() == null) directory.setName("Unnamed Directory");
        return mapper.directoryToDirectoryDTO(repository.save(directory));
    }

    public DirectoryDTO updateDirectory(DirectoryDTO directoryUpdates, String username) {
        if (directoryUpdates == null) throw new NullPointerException();
        Directory directory = repository.findById(directoryUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        checkAccess(2, directoryUpdates.getId(), directory.getOwner(), username);
        if(directoryUpdates.getParent() != null) {
            if (directory.getParent() == null) directoryUpdates.setParent(null);
            else if (Objects.equals(directoryUpdates.getId(), directoryUpdates.getParent())) directoryUpdates.setParent(directory.getParent().getId());
            else if (!Objects.equals(directoryUpdates.getParent(), directory.getParent().getId())) {
                Directory parent = repository.findById(directoryUpdates.getParent()).orElseThrow(EntityNotFoundException::new);
                if (parent.getOwner() != directory.getOwner()) {
                    throw new IllegalArgumentException();
                }
                if (!this.canMoveDirectories(directory, parent)) {
                    directoryUpdates.setParent(directory.getParent().getId());
                }
            }
        }

        mapper.updateDirectoryFromDirectoryDTO(directoryUpdates, directory);
        return mapper.directoryToDirectoryDTO(repository.save(directory));
    }

    public void deleteDirectory(Long id, String username) {
        User user = userRepository.findByUsername(username);
        Directory directory = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (directory.getParent() == null || directory.getOwner() != user) throw new IllegalArgumentException();
        repository.deleteById(id);
    }

    boolean canMoveDirectories(Directory movedDir, Directory newParent) {
        boolean hasParent = true;
        Directory checkedDir = newParent;
        while (hasParent) {
            if (checkedDir.getParent() == null) hasParent = false;
            else if (checkedDir.getParent() == movedDir) return false;
            else checkedDir = checkedDir.getParent();

        }
        return true;
    }

    void checkAccess(int accessLevel, Long id, User owner, String username) {
        User user = userRepository.findByUsername(username);
        if (!Objects.equals(owner.getId(), user.getId())) {
            Optional<Directory> dir = repository.findById(id);
            Optional<AccessDirectory> ad = adService.getAccessDirectory(user.getId(), dir.get().getId());
            while (ad.isEmpty()) { // look for ad
                if (dir.get().getParent() != null) {
                    dir = repository.findById(dir.get().getParent().getId());
                } else {
                    throw new UndefinedAccessException(); // no ad has been defined -> forbid access
                }
                ad = adService.getAccessDirectory(user.getId(), dir.get().getId()); // parent's ad
            }
            if (ad.get().getAccessPrivilege() < accessLevel) { // dir privilege must be at least accessLevel
                throw new DirectoryAccessForbiddenException();
            }

        }

    }
}
