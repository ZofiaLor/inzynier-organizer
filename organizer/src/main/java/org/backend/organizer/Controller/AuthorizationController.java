package org.backend.organizer.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.UserDTO;
import org.backend.organizer.Model.User;
import org.backend.organizer.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthorizationController {
    @Autowired
    UserService service;
    //TODO log out currently logged user, maybe log in the new one
    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        return new ResponseEntity<>(service.register(user), HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        String loginResult = service.login(user);
        if (loginResult.equals("fail")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Fail");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, service.login(user)).body("Success");
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
