package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.Model.AccessDirectory;
import org.backend.organizer.Service.AccessDirectoryService;
import org.backend.organizer.Service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@Controller
@RequestMapping("/api/ad")
public class AccessDirectoryController {
    @Autowired
    AccessDirectoryService service;
    @Autowired
    JWTService jwtService;

    @GetMapping("")
    public ResponseEntity<List<AccessDirectory>> getAllAccessDirectories() {
        return new ResponseEntity<>(service.getAllAccessDirectories(), HttpStatus.OK);
    }

    @GetMapping("user/{user}")
    public ResponseEntity<List<AccessDirectory>> getAccessDirectoryByUser(@PathVariable(name = "user") Long user) {
        try {
            return new ResponseEntity<>(service.getAccessDirectoryByUser(user), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("dir/{dir}")
    public ResponseEntity<List<AccessDirectory>> getAccessDirectoryByDirectory(@PathVariable(name = "dir") Long dir) {
        try {
            return new ResponseEntity<>(service.getAccessDirectoryByDirectory(dir), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{user}/{dir}")
    public ResponseEntity<AccessDirectory> getAccessDirectory(@PathVariable(name = "user") Long user, @PathVariable(name = "dir") Long dir) {
        try {
            Optional<AccessDirectory> ad = service.getAccessDirectory(user, dir);
            return ad.map(accessDirectory -> new ResponseEntity<>(accessDirectory, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Seems like it works exactly the same for creating and updating, and it's best if there's also no difference in the UI
    @PostMapping("")
    public ResponseEntity<AccessDirectory> createOrUpdateAccessDirectory(@RequestBody AccessDirectory ad) {
        try {
            return new ResponseEntity<>(service.createOrUpdateAccessDirectory(ad), HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{user}/{dir}")
    public ResponseEntity<HttpStatus> deleteAccessDirectory(@PathVariable(name = "user") Long user, @PathVariable(name = "dir") Long dir) {
        try {
            service.deleteAccessDirectory(user, dir);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
