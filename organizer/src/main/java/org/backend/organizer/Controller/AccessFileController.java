package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.Model.AccessFile;
import org.backend.organizer.Service.AccessFileService;
import org.backend.organizer.Service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/af")
public class AccessFileController {
    @Autowired
    AccessFileService service;
    @Autowired
    JWTService jwtService;

    @GetMapping("")
    public ResponseEntity<List<AccessFile>> getAllAccessFiles() {
        return new ResponseEntity<>(service.getAllAccessFiles(), HttpStatus.OK);
    }

    @GetMapping("{user}/{file}")
    public ResponseEntity<AccessFile> getAccessFile(@PathVariable(name = "user") Long user, @PathVariable(name = "file") Long file) {
        try {
            Optional<AccessFile> af = service.getAccessFile(user, file);
            return af.map(accessFile -> new ResponseEntity<>(accessFile, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Seems like it works exactly the same for creating and updating, and it's best if there's also no difference in the UI
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
