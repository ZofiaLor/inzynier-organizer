package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.VoteDTO;
import org.backend.organizer.Service.JWTService;
import org.backend.organizer.Service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@Controller
@RequestMapping("/api/votes")
@PreAuthorize("isAuthenticated()")
public class VoteController {
    @Autowired
    VoteService service;
    @Autowired
    JWTService jwtService;

    @GetMapping("/ed/{id}")
    public ResponseEntity<List<VoteDTO>> getVotesByEventDateId(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(service.getVotesByEventDateId(id), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("myvote/{id}")
    public ResponseEntity<VoteDTO> getCurrentUsersVoteByEventDateId(HttpServletRequest request, @PathVariable("id") Long id) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(service.getVoteByUserAndEventDate(username, id), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("")
    public ResponseEntity<VoteDTO> createVote(HttpServletRequest request, @RequestBody VoteDTO voteDTO) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(service.createVote(voteDTO, username), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteVote(HttpServletRequest request, @PathVariable("id") Long id) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            service.deleteVote(id, username);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
