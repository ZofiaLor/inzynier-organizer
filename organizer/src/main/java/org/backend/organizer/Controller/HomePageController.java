package org.backend.organizer.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomePageController {
    @GetMapping("/api/")
    public String greet() {
        return "Hello World!";
    }
}
