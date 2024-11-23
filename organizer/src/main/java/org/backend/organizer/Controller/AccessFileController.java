package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.Model.AccessFile;
import org.backend.organizer.Service.AccessFileService;
import org.backend.organizer.Service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@Controller
@RequestMapping("/api/af")
@PreAuthorize("isAuthenticated()")
public class AccessFileController {
    @Autowired
    AccessFileService service;
    @Autowired
    JWTService jwtService;

    @GetMapping("user/{user}")
    public ResponseEntity<List<AccessFile>> getAccessFileByUser(@PathVariable(name = "user") Long user) {
        try {
            return new ResponseEntity<>(service.getAccessFileByUser(user), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("file/{file}")
    public ResponseEntity<List<AccessFile>> getAccessFileByFile(@PathVariable(name = "file") Long file) {
        try {
            return new ResponseEntity<>(service.getAccessFileByFile(file), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("")
    public ResponseEntity<AccessFile> createOrUpdateAccessFile(@RequestBody AccessFile af) {
        try {
            return new ResponseEntity<>(service.createOrUpdateAccessFile(af), HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{user}/{file}")
    public ResponseEntity<HttpStatus> deleteAccessFile(@PathVariable(name = "user") Long user, @PathVariable(name = "file") Long file) {
        try {
            service.deleteAccessFile(user, file);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
