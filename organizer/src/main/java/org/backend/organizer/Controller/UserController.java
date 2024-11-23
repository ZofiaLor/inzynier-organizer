package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.UserDTO;
import org.backend.organizer.Model.User;
import org.backend.organizer.Service.JWTService;
import org.backend.organizer.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
@PreAuthorize("isAuthenticated()")
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
    public ResponseEntity<UserDTO> getUserByIdSafe(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(service.getUserByIdSafe(id), HttpStatus.OK);
        } catch (EntityNotFoundException ex){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (NullPointerException ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<HttpStatus> deleteUser(HttpServletRequest request, @PathVariable("id") Long id) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            service.deleteUser(id, username);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (EntityNotFoundException ex) { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }
        catch (IllegalArgumentException ex){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> deleteMyUser(HttpServletRequest request) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            var cookies = service.deleteMyUser(username);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookies.get(0)).header(HttpHeaders.SET_COOKIE, cookies.get(1)).body("");
        }
        catch (EntityNotFoundException ex) { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }
        catch (UsernameNotFoundException ex) {
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtService.getCleanJwtCookie().toString())
                    .header(HttpHeaders.SET_COOKIE, jwtService.getCleanJwtRefreshCookie().toString()).body("");
        }
    }


}
