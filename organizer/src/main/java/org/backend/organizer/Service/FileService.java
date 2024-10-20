package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.FileDTO;
import org.backend.organizer.Mapper.FileMapper;
import org.backend.organizer.Model.*;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.FileRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    @Autowired
    AccessDirectoryService adService;
    @Autowired
    AccessFileService afService;

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

    public FileDTO getFileByID(Long id, String username) {
        if (id == null) throw new NullPointerException();
        File file = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        FileService.checkAccess(1, file.getId(), file.getOwner(), username, file.getParent(), userRepository, directoryRepository, afService, adService);
        return mapper.fileToFileDTO(file);
    }

    public void deleteFile(Long id) {
        if(!repository.existsById(id)) throw new EntityNotFoundException();
        repository.deleteById(id);
    }

    static void checkAccess(int accessLevel, Long id, User owner, String username, Directory parent, UserRepository userRepository, DirectoryRepository directoryRepository, AccessFileService afService, AccessDirectoryService adService) {
        User user = userRepository.findByUsername(username);
        if (!Objects.equals(owner.getId(), user.getId())) {
            Optional<AccessFile> af = afService.getAccessFile(user.getId(), id);
            if (af.isEmpty()) {
                Optional<Directory> dir = directoryRepository.findById(parent.getId());
                Optional<AccessDirectory> ad;
                while (true) {
                    ad = adService.getAccessDirectory(user.getId(), dir.get().getId());
                    if (ad.isEmpty()) {
                        if (dir.get().getParent() != null) {
                            dir = directoryRepository.findById(dir.get().getParent().getId());
                        } else {
                            throw new IllegalArgumentException();
                        }
                    } else if (ad.get().getAccessPrivilege() < accessLevel) {
                        throw new IllegalArgumentException();
                    } else {
                        break;
                    }
                }
            } else if (af.get().getAccessPrivilege() < accessLevel) {
                throw new IllegalArgumentException();
            }
        }
    }
}
