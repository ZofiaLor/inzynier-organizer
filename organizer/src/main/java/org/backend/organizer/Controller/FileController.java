package org.backend.organizer.Controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.EventDTO;
import org.backend.organizer.DTO.FileDTO;
import org.backend.organizer.DTO.NoteDTO;
import org.backend.organizer.DTO.TaskDTO;
import org.backend.organizer.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/files")
public class FileController {
    @Autowired
    FileService fileService;
    @Autowired
    EventService eventService;
    @Autowired
    NoteService noteService;
    @Autowired
    TaskService taskService;
    @Autowired
    JWTService jwtService;

    @GetMapping("")
    public ResponseEntity<List<FileDTO>> getAllFiles() {
        return new ResponseEntity<>(fileService.getAllFiles(), HttpStatus.OK);
    }

    @GetMapping("/myfiles")
    public ResponseEntity<List<FileDTO>> getCurrentUsersFiles(HttpServletRequest request) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        return new ResponseEntity<>(fileService.getAllFilesByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/dir/{id}")
    public ResponseEntity<List<FileDTO>> getFilesInDirectory(@PathVariable(name = "id") Long id) {
        try {
            return new ResponseEntity<>(fileService.getAllFilesByDirectory(id), HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileDTO> getFileByID(HttpServletRequest request, @PathVariable(name = "id") Long id) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(fileService.getFileByID(id, username), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/event")
    public ResponseEntity<EventDTO> createEvent(HttpServletRequest request, @RequestBody EventDTO event) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        return new ResponseEntity<>(eventService.createEvent(event, username), HttpStatus.OK);
    }
    @PutMapping("/event")
    public ResponseEntity<EventDTO> updateEvent(HttpServletRequest request, @RequestBody EventDTO event) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(eventService.updateEvent(event, username), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/note")
    public ResponseEntity<NoteDTO> createNote(HttpServletRequest request, @RequestBody NoteDTO note) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        return new ResponseEntity<>(noteService.createNote(note, username), HttpStatus.OK);
    }
    @PutMapping("/note")
    public ResponseEntity<NoteDTO> updateNote(HttpServletRequest request, @RequestBody NoteDTO note) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(noteService.updateNote(note, username), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/task")
    public ResponseEntity<TaskDTO> createTask(HttpServletRequest request, @RequestBody TaskDTO task) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        return new ResponseEntity<>(taskService.createTask(task, username), HttpStatus.OK);
    }
    @PutMapping("/task")
    public ResponseEntity<TaskDTO> updateTask(HttpServletRequest request, @RequestBody TaskDTO task) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        try {
            return new ResponseEntity<>(taskService.updateTask(task, username), HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteFile(@PathVariable(name = "id") Long id) {
        try {
            fileService.deleteFile(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
