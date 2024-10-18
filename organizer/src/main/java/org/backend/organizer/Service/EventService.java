package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.EventDTO;
import org.backend.organizer.Mapper.FileMapper;
import org.backend.organizer.Model.Event;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.EventRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    @Autowired
    EventRepository repository;
    @Autowired
    FileMapper mapper;
    @Autowired
    UserRepository userRepository;

    public List<EventDTO> getAllEventsByUser(String username) {
        User user = userRepository.findByUsername(username);
        var result = new ArrayList<EventDTO>();
        for (var file : repository.getAllByOwner(user)) {
            result.add(mapper.eventToEventDTO(file));
        }
        return result;
    }

    public EventDTO getEventByID(Long id) {
        if (id == null) throw new NullPointerException();
        return mapper.eventToEventDTO(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public EventDTO createEvent(EventDTO newEvent, String username) {
        Event Event = mapper.eventDTOToEvent(newEvent);
        User owner = userRepository.findByUsername(username);
        Event.setCreationDate(LocalDateTime.now());
        Event.setOwner(owner);
        return mapper.eventToEventDTO(repository.save(Event));
    }

    public EventDTO updateEvent(EventDTO EventUpdates) {
        if (EventUpdates == null) throw new NullPointerException();
        Event Event = repository.findById(EventUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        mapper.updateEventFromEventDTO(EventUpdates, Event);
        return mapper.eventToEventDTO(repository.save(Event));
    }
}
