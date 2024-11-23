package org.backend.organizer.Repository;

import org.backend.organizer.Model.File;
import org.backend.organizer.Model.Notification;
import org.backend.organizer.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isSent = :isSent AND n.sendTimeSetting <= :sendTimeSetting")
    List<Notification> getAllByUserAndSentAndSendTimeSettingAfter(User user, boolean isSent, LocalDateTime sendTimeSetting);
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isSent = TRUE ORDER BY n.sendTimeSetting DESC")
    List<Notification> getAllSentByUser(User user);
    @Query("SELECT n FROM Notification n WHERE n.isRead = :isRead AND n.user = :user AND n.isSent = TRUE ORDER BY n.sendTimeSetting DESC")
    List<Notification> getAllSentByReadAndUser(boolean isRead, User user);

    @Query("SELECT n FROM Notification n WHERE n.file = :file AND n.user = :user AND n.isSent = FALSE ORDER BY n.sendTimeSetting DESC")
    List<Notification> getAllUnsentByUserAndFile(User user, File file);
}
