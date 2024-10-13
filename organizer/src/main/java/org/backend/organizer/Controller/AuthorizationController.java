package org.backend.organizer.Controller;

import org.backend.organizer.Model.User;
import org.backend.organizer.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthorizationController {
    @Autowired
    UserService service;
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(service.register(user), HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        String loginResult = service.login(user);
        if (loginResult.equals("fail")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Fail");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, service.login(user)).body("Success");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, service.logout()).body("Success");
    }
}
