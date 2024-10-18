package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.DirectoryDTO;
import org.backend.organizer.Mapper.DirectoryMapper;
import org.backend.organizer.Model.Directory;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DirectoryService {
    @Autowired
    DirectoryRepository repository;
    @Autowired
    DirectoryMapper mapper;
    @Autowired
    UserRepository userRepository;

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

    public List<DirectoryDTO> getAllDirectoriesByParentID(Long parentID) {
        Directory parent = repository.getReferenceById(parentID);
        var result = new ArrayList<DirectoryDTO>();
        for (var dir : repository.getAllByParent(parent)) {
            result.add(mapper.directoryToDirectoryDTO(dir));
        }
        return result;
    }

    public DirectoryDTO getByID(Long id) {
        if (id == null) throw new NullPointerException();
        return mapper.directoryToDirectoryDTO(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public DirectoryDTO createDirectory(DirectoryDTO newDirectory, String username) {
        Directory directory = mapper.directoryDTOToDirectory(newDirectory);
        User owner = userRepository.findByUsername(username);
        directory.setOwner(owner);
        return mapper.directoryToDirectoryDTO(repository.save(directory));
    }

    public DirectoryDTO updateDirectory(DirectoryDTO directoryUpdates) {
        if (directoryUpdates == null) throw new NullPointerException();
        Directory directory = repository.findById(directoryUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        mapper.updateDirectoryFromDirectoryDTO(directoryUpdates, directory);
        return mapper.directoryToDirectoryDTO(repository.save(directory));
    }

    public void deleteDirectory(Long id) {
        if(!repository.existsById(id)) throw new EntityNotFoundException();
        repository.deleteById(id);
    }
}
