package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.NotificationDTO;
import org.backend.organizer.Mapper.NotificationMapper;
import org.backend.organizer.Model.File;
import org.backend.organizer.Model.Notification;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.FileRepository;
import org.backend.organizer.Repository.NotificationRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository repository;
    @Autowired
    NotificationMapper mapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FileRepository fileRepository;

    public List<NotificationDTO> getAllSentNotificationsByUser(String username) {
        if (!userRepository.existsByUsername(username)) throw new EntityNotFoundException();
        User user = userRepository.findByUsername(username);
        var result = new ArrayList<NotificationDTO>();
        for (var notif : repository.getAllSentByUser(user)) {
            result.add(mapper.notificationToNotificationDTO(notif));
        }
        return result;
    }

    public List<NotificationDTO> getAllSentNotificationsByUserAndRead(String username, boolean isRead) {
        if (!userRepository.existsByUsername(username)) throw new EntityNotFoundException();
        User user = userRepository.findByUsername(username);
        var result = new ArrayList<NotificationDTO>();
        for (var notif : repository.getAllSentByReadAndUser(isRead, user)) {
            result.add(mapper.notificationToNotificationDTO(notif));
        }
        return result;
    }

    public List<NotificationDTO> getAllUnsentByUserAndFile(String username, Long fileId) {
        if (fileId == null) throw new NullPointerException();
        if (!userRepository.existsByUsername(username)) throw new EntityNotFoundException();
        User user = userRepository.findByUsername(username);
        File file = fileRepository.findById(fileId).orElseThrow(EntityNotFoundException::new);
        var result = new ArrayList<NotificationDTO>();
        for (var notif : repository.getAllUnsentByUserAndFile(user, file)) {
            result.add(mapper.notificationToNotificationDTO(notif));
        }
        return result;
    }

    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        if (notificationDTO.getUser() == null || notificationDTO.getFile() == null) throw new NullPointerException();
        Notification notification = mapper.notificationDTOToNotification(notificationDTO);
        notification.setRead(false);
        if (notification.getSendTimeSetting() == null) notification.setSendTimeSetting(LocalDateTime.now());
        notification.setSent(LocalDateTime.now().isAfter(notification.getSendTimeSetting()));
        return mapper.notificationToNotificationDTO(repository.save(notification));
    }

    public void sendUsersNotifications(String username) {
        if (!userRepository.existsByUsername(username)) throw new EntityNotFoundException();
        User user = userRepository.findByUsername(username);
        for (var notif : repository.getAllByUserAndSentAndSendTimeSettingAfter(user,false, LocalDateTime.now())) {
            notif.setSent(true);
            repository.save(notif);
        }
    }

    public void deleteNotification(Long id) {
        if(!repository.existsById(id)) throw new EntityNotFoundException();
        repository.deleteById(id);
    }
}
