package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.UserDTO;
import org.backend.organizer.Model.User;
import org.backend.organizer.Model.UserPrincipal;
import org.backend.organizer.Service.JWTService;
import org.backend.organizer.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/auth")
public class AuthorizationController {
    @Autowired
    UserService service;

    @Autowired
    JWTService jwtService;
    //TODO log out currently logged user, maybe log in the new one
    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        try {
            UserDTO newUser = service.register(user);
            if (newUser == null) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        try {
            UserPrincipal userPrincipal = service.login(user);
            ResponseCookie jwtCookie = jwtService.generateJwtCookie(userPrincipal);
            String role = userPrincipal.getAuthorities().toArray()[0].toString();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(new UserDTO(userPrincipal.getId(), userPrincipal.getUsername(), role));
        } catch (NullPointerException ex) {
            return ResponseEntity.badRequest().body("Empty username or password " + user.getUsername() + user.getPassword());
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, service.logout()).body("Success");
    }

    @PutMapping("/grant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> grantAdminPrivilege(HttpServletRequest request, @RequestBody UserDTO user) {
        try {
            return new ResponseEntity<>(service.changePrivilege("ROLE_ADMIN", user.getUsername(), request), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("/revoke")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> revokeAdminPrivilege(HttpServletRequest request, @RequestBody UserDTO user) {
        try {
            return new ResponseEntity<>(service.changePrivilege("ROLE_USER", user.getUsername(), request), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
