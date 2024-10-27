package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.DirectoryDTO;
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

    public List<DirectoryDTO> getAllDirectories() {
        var result = new ArrayList<DirectoryDTO>();
        for (var dir : repository.findAll()) {
            result.add(mapper.directoryToDirectoryDTO(dir));
        }
        return result;
    }

    public List<DirectoryDTO> getAllDirectoriesByUsername(String username) {
        User user = userRepository.findByUsername(username);
        var result = new ArrayList<DirectoryDTO>();
        for (var dir : repository.getAllByOwner(user)) {
            result.add(mapper.directoryToDirectoryDTO(dir));
        }
        return result;
    }

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

    public DirectoryDTO getByID(Long id, String username) {
        if (id == null) throw new NullPointerException();
        Directory dir = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        checkAccess(1, id, dir.getOwner(), username);
        return mapper.directoryToDirectoryDTO(dir);
    }

    public DirectoryDTO createDirectory(DirectoryDTO newDirectory, String username) {
        Directory directory = mapper.directoryDTOToDirectory(newDirectory);
        User owner = userRepository.findByUsername(username);
        directory.setOwner(owner);
        return mapper.directoryToDirectoryDTO(repository.save(directory));
    }

    public DirectoryDTO updateDirectory(DirectoryDTO directoryUpdates, String username) {
        if (directoryUpdates == null) throw new NullPointerException();
        Directory directory = repository.findById(directoryUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        checkAccess(2, directoryUpdates.getId(), directory.getOwner(), username);
        mapper.updateDirectoryFromDirectoryDTO(directoryUpdates, directory);
        return mapper.directoryToDirectoryDTO(repository.save(directory));
    }

    public void deleteDirectory(Long id) {
        if(!repository.existsById(id)) throw new EntityNotFoundException();
        repository.deleteById(id);
    }

    void checkAccess(int accessLevel, Long id, User owner, String username) {
        User user = userRepository.findByUsername(username);
        if (!Objects.equals(owner.getId(), user.getId())) {
            Optional<Directory> dir = repository.findById(id);
            Optional<AccessDirectory> ad = adService.getAccessDirectory(user.getId(), dir.get().getId());
            if (ad.isEmpty()) {
                dir = repository.findById(dir.get().getParent().getId());
                while (true) {
                    ad = adService.getAccessDirectory(user.getId(), dir.get().getId());
                    if (ad.isEmpty()) {
                        if (dir.get().getParent() != null) {
                            dir = repository.findById(dir.get().getParent().getId());
                        } else {
                            throw new IllegalArgumentException();
                        }
                    } else if (ad.get().getAccessPrivilege() < accessLevel) {
                        throw new IllegalArgumentException();
                    } else {
                        break;
                    }
                }
            } else if (ad.get().getAccessPrivilege() < accessLevel) {
                throw new IllegalArgumentException();
            }

        }

    }
}
