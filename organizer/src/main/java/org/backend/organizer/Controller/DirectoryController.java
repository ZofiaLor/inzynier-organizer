package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.DirectoryDTO;
import org.backend.organizer.Service.DirectoryService;
import org.backend.organizer.Service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@Controller
@RequestMapping("/api/directories")
public class DirectoryController {
    @Autowired
    DirectoryService service;
    @Autowired
    JWTService jwtService;

    @GetMapping("")
    public ResponseEntity<List<DirectoryDTO>> getAllDirectories() {
        return new ResponseEntity<>(service.getAllDirectories(), HttpStatus.OK);
    }

    @GetMapping("/mydirs")
    public ResponseEntity<List<DirectoryDTO>> getCurrentUsersDirectories(HttpServletRequest request) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        return new ResponseEntity<>(service.getAllDirectoriesByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/basedirs")
    public ResponseEntity<DirectoryDTO> getCurrentUsersBaseDirectory(HttpServletRequest request) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        return new ResponseEntity<>(service.getBaseDirectoryByOwner(username), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectoryDTO> getDirectoryByID(HttpServletRequest request, @PathVariable(name = "id") Long id) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(service.getByID(id, username), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<Boolean> checkDirectoryEditAccess(HttpServletRequest request, @PathVariable(name = "id") Long id) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(service.checkDirectoryEditAccess(id, username), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/subdirs/{id}")
    public ResponseEntity<List<DirectoryDTO>> getDirectoryByParentID(HttpServletRequest request, @PathVariable(name = "id") Long id) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(service.getAllDirectoriesByParentID(id, username), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("")
    public ResponseEntity<DirectoryDTO> createDirectory(HttpServletRequest request, @RequestBody DirectoryDTO directory) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(service.createDirectory(directory, username, false), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("")
    public ResponseEntity<DirectoryDTO> updateDirectory(HttpServletRequest request, @RequestBody DirectoryDTO directory) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(service.updateDirectory(directory, username), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDirectory(HttpServletRequest request, @PathVariable(name = "id") Long id) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            service.deleteDirectory(id, username);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
