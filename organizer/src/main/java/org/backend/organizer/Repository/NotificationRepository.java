package org.backend.organizer.Repository;

import org.backend.organizer.Model.Notification;
import org.backend.organizer.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.isSent = :isSent")
    List<Notification> getAllBySent(boolean isSent);
    @Query("SELECT n FROM Notification n WHERE n.isSent = :isSent AND n.sendTimeSetting <= :sendTimeSetting")
    List<Notification> getAllBySentAndSendTimeSettingAfter(boolean isSent, LocalDateTime sendTimeSetting);
    List<Notification> getAllByUser(User user);
    @Query("SELECT n FROM Notification n WHERE n.isRead = :isRead AND n.user = :user")
    List<Notification> getAllByReadAndUser(boolean isRead, User user);
}
