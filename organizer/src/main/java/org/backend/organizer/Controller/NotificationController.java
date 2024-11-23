package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.NotificationDTO;
import org.backend.organizer.Service.JWTService;
import org.backend.organizer.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@Controller
@RequestMapping("/api/notifs")
@PreAuthorize("isAuthenticated()")
public class NotificationController {
    @Autowired
    NotificationService service;
    @Autowired
    JWTService jwtService;

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

    @GetMapping("/file/{id}")
    public ResponseEntity<List<NotificationDTO>> getNotificationByUserAndFileID(HttpServletRequest request, @PathVariable(name = "id") Long id) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(service.getAllUnsentByUserAndFile(username, id), HttpStatus.OK);
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
    public ResponseEntity<HttpStatus> sendUsersNotifications(HttpServletRequest request) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            service.sendUsersNotifications(username);
            return new ResponseEntity<>(HttpStatus.OK);
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
