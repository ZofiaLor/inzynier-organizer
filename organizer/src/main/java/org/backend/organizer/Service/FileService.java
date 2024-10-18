package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.FileDTO;
import org.backend.organizer.Mapper.FileMapper;
import org.backend.organizer.Model.Directory;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.FileRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    @Autowired
    FileRepository repository;
    @Autowired
    FileMapper mapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DirectoryRepository directoryRepository;

    public List<FileDTO> getAllFiles() {
        var result = new ArrayList<FileDTO>();
        for (var file : repository.findAll()) {
            result.add(mapper.fileToFileDTO(file));
        }
        return result;
    }

    public List<FileDTO> getAllFilesByUsername(String username) {
        User user = userRepository.findByUsername(username);
        var result = new ArrayList<FileDTO>();
        for (var file : repository.getAllByOwner(user)) {
            result.add(mapper.fileToFileDTO(file));
        }
        return result;
    }

    public List<FileDTO> getAllFilesByDirectory(Long parentID) {
        Directory directory = directoryRepository.findById(parentID).orElseThrow(EntityNotFoundException::new);
        var result = new ArrayList<FileDTO>();
        for (var file : repository.getAllByParent(directory)) {
            result.add(mapper.fileToFileDTO(file));
        }
        return result;
    }

    public FileDTO getFileByID(Long id) {
        if (id == null) throw new NullPointerException();
        return mapper.fileToFileDTO(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public void deleteFile(Long id) {
        if(!repository.existsById(id)) throw new EntityNotFoundException();
        repository.deleteById(id);
    }
}
