package org.backend.organizer.Mapper;

import org.backend.organizer.DTO.UserDTO;
import org.backend.organizer.Model.Directory;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.DirectoryRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    DirectoryRepository directoryRepository;
    @Mapping(source = "directories", target = "directories", qualifiedByName = "getListOfLongDirectories")
    public abstract UserDTO userToUserDTO(User user);

    @Named("getListOfLongDirectories")
    List<Long> getListOfLongDirectories(List<Directory> directories) {
        if (directories == null) return null;
        var result = new ArrayList<Long>();
        for (var directory : directories) {
            result.add(directory.getId());
        }
        return result;
    }

    @Mapping(source = "directories", target = "directories", qualifiedByName = "getListOfDirectories")
    public abstract User userDTOToUser(UserDTO userDTO);

    @Named("getListOfDirectories")
    List<Directory> getListOfDirectories(List<Long> directories) {
        if (directories == null) return null;
        var result = new ArrayList<Directory>();
        for (var directory : directories) {
            result.add(directoryRepository.getReferenceById(directory));
        }
        return result;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "directories", target = "directories", qualifiedByName = "getListOfDirectories")
    //@Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    public abstract void updateUserFromUserDTO(UserDTO userDTO, @MappingTarget User user);
}
