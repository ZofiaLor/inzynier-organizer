package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.UserDTO;
import org.backend.organizer.Mapper.UserMapper;
import org.backend.organizer.Model.RefreshToken;
import org.backend.organizer.Model.User;
import org.backend.organizer.Model.UserPrincipal;
import org.backend.organizer.Request.Passwords;
import org.backend.organizer.Service.JWTService;
import org.backend.organizer.Service.RefreshTokenService;
import org.backend.organizer.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true") // allowCredentials is not possible for origins *
@Controller
@RequestMapping("/api/auth")
public class AuthorizationController {
    @Autowired
    UserService service;
    @Autowired
    UserMapper mapper;

    @Autowired
    JWTService jwtService;
    @Autowired
    RefreshTokenService refreshTokenService;
    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        try {
            UserDTO newUser = service.register(user);
            if (newUser == null) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            newUser.setPassword("");
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
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal.getUser().getId());

            ResponseCookie jwtRefreshCookie = jwtService.generateRefreshJwtCookie(refreshToken.getToken());
            userPrincipal.getUser().setPassword("");
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(mapper.userToUserDTO(userPrincipal.getUser()));
        } catch (NullPointerException ex) {
            return ResponseEntity.badRequest().body("Empty username or password");
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(403).body("Incorrect credentials");
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        var cookies = service.logout();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookies.get(0)).header(HttpHeaders.SET_COOKIE, cookies.get(1)).body("Success");
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestBody Passwords passwords) {
        try {
            return new ResponseEntity<>(service.resetPassword(request, passwords), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtService.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (!refreshToken.isEmpty())) {
            try {
                RefreshToken token = refreshTokenService.findByToken(refreshToken).orElseThrow(EntityNotFoundException::new);
                refreshTokenService.verifyExpiration(token);
                User user = token.getUser();
                ResponseCookie jwtCookie = jwtService.generateJwtCookie(user);
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                        .body("Success");
            } catch (IllegalArgumentException ex) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } catch (EntityNotFoundException ex) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
