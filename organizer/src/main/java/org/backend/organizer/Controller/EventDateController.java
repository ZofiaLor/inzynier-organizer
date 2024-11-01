package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.EventDateDTO;
import org.backend.organizer.Service.EventDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/ed")
public class EventDateController {
    @Autowired
    EventDateService service;

    @GetMapping("")
    public ResponseEntity<List<EventDateDTO>> getAllEventDates(@RequestParam(required = false) Long eventId) {
        if (eventId == null) {
            return new ResponseEntity<>(service.getAllEventDates(), HttpStatus.OK);
        }
        try {
            return new ResponseEntity<>(service.getEventDatesByEventId(eventId), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDateDTO> getEventDateById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("")
    public ResponseEntity<EventDateDTO> createEventDate(@RequestBody EventDateDTO eventDateDTO) {
        try {
            return new ResponseEntity<>(service.createEventDate(eventDateDTO), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("")
    public ResponseEntity<EventDateDTO> updateEventDate(@RequestBody EventDateDTO eventDateDTO) {
        try {
            return new ResponseEntity<>(service.updateEventDate(eventDateDTO), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteEventDate(@PathVariable("id") Long id) {
        try {
            service.deleteEventDate(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
