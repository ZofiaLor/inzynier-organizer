package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.FileDTO;
import org.backend.organizer.Exception.DirectoryAccessForbiddenException;
import org.backend.organizer.Exception.FileAccessForbiddenException;
import org.backend.organizer.Exception.UndefinedAccessException;
import org.backend.organizer.Mapper.FileMapper;
import org.backend.organizer.Model.*;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.FileRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    DirectoryService directoryService;

    public List<FileDTO> getAllFilesByDirectory(Long parentID, String username) {
        Directory directory = directoryRepository.findById(parentID).orElseThrow(EntityNotFoundException::new);
        directoryService.checkAccess(1, parentID, directory.getOwner(), username);
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

    public boolean checkFileEditAccess(Long id, String username) {
        if (id == null) throw new NullPointerException();
        File file = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        try {
            FileService.checkAccess(2, file.getId(), file.getOwner(), username, file.getParent(), userRepository, directoryRepository, afService, adService);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void deleteFile(Long id, String username) {
        User user = userRepository.findByUsername(username);
        File file = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (file.getOwner() != user) throw new IllegalArgumentException();
        repository.deleteById(id);
    }

    static void checkAccess(int accessLevel, Long id, User owner, String username, Directory parent, UserRepository userRepository, DirectoryRepository directoryRepository, AccessFileService afService, AccessDirectoryService adService) {
        User user = userRepository.findByUsername(username);
        if (!Objects.equals(owner.getId(), user.getId())) { // current user is the owner -> can always access
            Optional<AccessFile> af = afService.getAccessFile(user.getId(), id);
            if (af.isEmpty()) { // file privilege not defined -> check parent directory
                Optional<Directory> dir = directoryRepository.findById(parent.getId());
                Optional<AccessDirectory> ad = adService.getAccessDirectory(user.getId(), dir.get().getId());
                while (ad.isEmpty()) { // look for ad
                    if (dir.get().getParent() != null) {
                        dir = directoryRepository.findById(dir.get().getParent().getId());
                    } else {
                        throw new UndefinedAccessException(); // no ad has been defined -> forbid access
                    }
                    ad = adService.getAccessDirectory(user.getId(), dir.get().getId()); // parent's ad
                }
                if (ad.get().getAccessPrivilege() < accessLevel) { // dir privilege must be at least accessLevel
                    throw new DirectoryAccessForbiddenException();
                }
            } else if (af.get().getAccessPrivilege() < accessLevel) { // file privilege must be at least accessLevel
                throw new FileAccessForbiddenException();
            }
        }
    }

    static boolean isValidDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss X");
        LocalDateTime minDate = LocalDateTime.parse("1970-01-01 00:00:00 Z", formatter);
        LocalDateTime maxDate = LocalDateTime.parse("2038-01-19 03:14:08 Z", formatter);
        return (date.isAfter(minDate) && date.isBefore(maxDate));
    }
}
