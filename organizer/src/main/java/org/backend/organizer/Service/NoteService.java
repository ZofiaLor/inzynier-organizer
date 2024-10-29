package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.NoteDTO;
import org.backend.organizer.Mapper.FileMapper;
import org.backend.organizer.Model.*;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.NoteRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService {
    @Autowired
    NoteRepository repository;
    @Autowired
    FileMapper mapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    AccessDirectoryService adService;
    @Autowired
    AccessFileService afService;

    public List<NoteDTO> getAllNotesByUser(String username) {
        User user = userRepository.findByUsername(username);
        var result = new ArrayList<NoteDTO>();
        for (var file : repository.getAllByOwner(user)) {
            result.add(mapper.noteToNoteDTO(file));
        }
        return result;
    }

    public NoteDTO getNoteByID(Long id) {
        if (id == null) throw new NullPointerException();
        return mapper.noteToNoteDTO(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public NoteDTO createNote(NoteDTO newNote, String username) {
        if (newNote.getParent() == null) throw new NullPointerException();
        Note note = mapper.noteDTOtoNote(newNote);
        User owner = userRepository.findByUsername(username);
        note.setCreationDate(LocalDateTime.now());
        note.setOwner(owner);
        if (newNote.getName() == null) note.setName("Unnamed Note");
        return mapper.noteToNoteDTO(repository.save(note));
    }

    public NoteDTO updateNote(NoteDTO noteUpdates, String username) {
        if (noteUpdates == null) throw new NullPointerException();
        Note note = repository.findById(noteUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        FileService.checkAccess(2, note.getId(), note.getOwner(), username, note.getParent(), userRepository, directoryRepository, afService, adService);
        mapper.updateNoteFromNoteDTO(noteUpdates, note);
        return mapper.noteToNoteDTO(repository.save(note));
    }


}
