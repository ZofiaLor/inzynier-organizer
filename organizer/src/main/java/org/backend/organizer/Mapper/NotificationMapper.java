package org.backend.organizer.Mapper;

import org.backend.organizer.DTO.NotificationDTO;
import org.backend.organizer.Model.File;
import org.backend.organizer.Model.Notification;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.FileRepository;
import org.backend.organizer.Repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class NotificationMapper {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FileRepository fileRepository;

    @Mapping(source = "user", target = "user", qualifiedByName = "getLongUser")
    @Mapping(source = "file", target = "file", qualifiedByName = "getLongFile")
    public abstract NotificationDTO notificationToNotificationDTO(Notification notification);

    @Named("getLongUser")
    Long getLongUser(User user) {
        if (user == null) return null;
        return user.getId();
    }

    @Named("getLongFile")
    Long getLongFile(File file) {
        if (file == null) return null;
        return file.getId();
    }

    @Mapping(source = "user", target = "user", qualifiedByName = "getUser")
    @Mapping(source = "file", target = "file", qualifiedByName = "getFile")
    public abstract Notification notificationDTOToNotification(NotificationDTO notificationDTO);

    @Named("getUser")
    User getUser(Long id) {
        if (id == null) return null;
        return userRepository.getReferenceById(id);
    }

    @Named("getFile")
    File getFile(Long id) {
        if (id == null) return null;
        return fileRepository.getReferenceById(id);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "file", ignore = true)
    @Mapping(target = "sent", ignore = true)
    public abstract void updateNotificationFromNotificationDTO(NotificationDTO notificationDTO, @MappingTarget Notification notification);
}
