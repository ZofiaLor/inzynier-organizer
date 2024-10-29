package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.NotificationDTO;
import org.backend.organizer.Service.JWTService;
import org.backend.organizer.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notifs")
public class NotificationController {
    @Autowired
    NotificationService service;
    @Autowired
    JWTService jwtService;

    @GetMapping("")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return new ResponseEntity<>(service.getAllNotifications(), HttpStatus.OK);
    }

    @GetMapping("/mynotifs")
    public ResponseEntity<List<NotificationDTO>> getCurrentUsersNotifications(HttpServletRequest request, @RequestParam(required = false) Boolean read) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            if (read == null) {
                return new ResponseEntity<>(service.getAllSentNotificationsByUser(username), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(service.getAllSentNotificationsByUserAndRead(username, read), HttpStatus.OK);
            }
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationByID(@PathVariable(name = "id") Long id) {
        try {
            return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("")
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            return new ResponseEntity<>(service.createNotification(notificationDTO), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/send")
    public ResponseEntity<HttpStatus> sendNotifications() {
        service.sendNotifications();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<NotificationDTO> updateNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            return new ResponseEntity<>(service.updateNotification(notificationDTO), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteNotification(@PathVariable("id") Long id) {
        try {
            service.deleteNotification(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}