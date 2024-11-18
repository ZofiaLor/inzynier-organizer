package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.VoteDTO;
import org.backend.organizer.Service.JWTService;
import org.backend.organizer.Service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@Controller
@RequestMapping("/api/votes")
public class VoteController {
    @Autowired
    VoteService service;
    @Autowired
    JWTService jwtService;

    @GetMapping("")
    public ResponseEntity<List<VoteDTO>> getAllVotes() {
        return new ResponseEntity<>(service.getAllVotes(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<VoteDTO>> getVotesByUserId(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(service.getVotesByUserId(id), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<VoteDTO> getVoteId(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(service.getVoteById(id), HttpStatus.OK);
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
        }
    }

    @PutMapping("")
    public ResponseEntity<VoteDTO> updateVote(@RequestBody VoteDTO voteDTO) {
        try {
            return new ResponseEntity<>(service.updateVote(voteDTO), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteVote(@PathVariable("id") Long id) {
        try {
            service.deleteVote(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
