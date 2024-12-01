package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.EventDTO;
import org.backend.organizer.Mapper.FileMapper;
import org.backend.organizer.Model.*;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.EventRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EventService {
    @Autowired
    EventRepository repository;
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

    public EventDTO createEvent(EventDTO newEvent, String username) {
        if (newEvent.getParent() == null) throw new NullPointerException();
        Event Event = mapper.eventDTOToEvent(newEvent);
        User owner = userRepository.findByUsername(username);
        Event.setCreationDate(LocalDateTime.now());
        Event.setOwner(owner);
        Directory parent = directoryRepository.findById(newEvent.getParent()).orElseThrow(EntityNotFoundException::new);
        if (parent.getOwner() != owner) {
            throw new IllegalArgumentException();
        }
        if (newEvent.getName() == null) Event.setName("Unnamed Event");
        return mapper.eventToEventDTO(repository.save(Event));
    }

    public EventDTO updateEvent(EventDTO EventUpdates, String username) {
        if (EventUpdates == null) throw new NullPointerException();
        Event event = repository.findById(EventUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        FileService.checkAccess(2, event.getId(), event.getOwner(), username, event.getParent(), userRepository, directoryRepository, afService, adService);
        if (EventUpdates.getParent() != null && !Objects.equals(EventUpdates.getParent(), event.getParent().getId())) {
            Directory parent = directoryRepository.findById(EventUpdates.getParent()).orElseThrow(EntityNotFoundException::new);
            if (parent.getOwner() != event.getOwner()) {
                throw new IllegalArgumentException();
            }
        }
        mapper.updateEventFromEventDTO(EventUpdates, event);
        return mapper.eventToEventDTO(repository.save(event));
    }
}
