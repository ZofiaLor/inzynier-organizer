package org.backend.organizer.Mapper;

import org.backend.organizer.DTO.DirectoryDTO;
import org.backend.organizer.Model.Directory;
import org.backend.organizer.Model.File;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.FileRepository;
import org.backend.organizer.Repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class DirectoryMapper {
    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FileRepository fileRepository;

    @Mapping(source = "parent", target = "parent", qualifiedByName = "getLongParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getLongOwner")
    @Mapping(source = "files", target = "files", qualifiedByName = "getListOfLongFiles")
    public abstract DirectoryDTO directoryToDirectoryDTO(Directory directory);

    @Named("getLongParent")
    Long getLongParent(Directory parent){
        if (parent == null) return null;
        return parent.getId();
    }

    @Named("getLongOwner")
    Long getLongOwner(User owner) {
        if (owner == null) return null;
        return owner.getId();
    }

    @Named("getListOfLongFiles")
    List<Long> getListOfLongFiles(List<File> files) {
        if (files == null) return null;
        var result = new ArrayList<Long>();
        for (var file : files) {
            result.add(file.getId());
        }
        return result;
    }

    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getOwner")
    @Mapping(source = "files", target = "files", qualifiedByName = "getListOfFiles")
    public abstract Directory directoryDTOToDirectory(DirectoryDTO directoryDTO);

    @Named("getParent")
    Directory getParent(Long parent){
        if (parent == null) return null;
        return directoryRepository.getReferenceById(parent);
    }

    @Named("getOwner")
    User getOwner(Long owner) {
        if (owner == null) return null;
        return userRepository.getReferenceById(owner);
    }

    @Named("getListOfFiles")
    List<File> getListOfFiles(List<Long> files) {
        if (files == null) return null;
        var result = new ArrayList<File>();
        for (var file : files) {
            result.add(fileRepository.getReferenceById(file));
        }
        return result;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(target = "owner", ignore = true)
    @Mapping(source = "files", target = "files", qualifiedByName = "getListOfFiles")
    public abstract void updateDirectoryFromDirectoryDTO(DirectoryDTO directoryDTO, @MappingTarget Directory directory);
}
