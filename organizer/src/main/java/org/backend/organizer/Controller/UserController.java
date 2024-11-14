package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.UserDTO;
import org.backend.organizer.Model.User;
import org.backend.organizer.Service.JWTService;
import org.backend.organizer.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService service;
    @Autowired
    JWTService jwtService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(service.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/safe")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<UserDTO>> getAllUsersSafe() {
        return new ResponseEntity<>(service.getAllUsersSafe(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(service.getUserById(id), HttpStatus.OK);
        } catch (EntityNotFoundException ex){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (NullPointerException ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/safe/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<UserDTO> getUserByIdSafe(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(service.getUserByIdSafe(id), HttpStatus.OK);
        } catch (EntityNotFoundException ex){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (NullPointerException ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //TODO temporary solution for id and username disambiguation
    @GetMapping("/name/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("username") String username) {
        try {
            UserDTO user = service.getUserByUsername(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (EntityNotFoundException ex){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/myuser")
    public ResponseEntity<UserDTO> getMyUser(HttpServletRequest request) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            UserDTO user = service.getUserByUsername(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (EntityNotFoundException ex){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO user) {
        try {
            return new ResponseEntity<>(service.updateUser(user), HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (NullPointerException ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException ex){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {
        try {
            service.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (EntityNotFoundException ex) { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }
    }


}
