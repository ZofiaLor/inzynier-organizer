package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.EventDateDTO;
import org.backend.organizer.Mapper.EventDateMapper;
import org.backend.organizer.Model.Event;
import org.backend.organizer.Model.EventDate;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.EventDateRepository;
import org.backend.organizer.Repository.EventRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventDateService {
    @Autowired
    EventDateRepository repository;
    @Autowired
    EventDateMapper mapper;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;

    public List<EventDateDTO> getAllEventDates() {
        var result = new ArrayList<EventDateDTO>();
        for (var eventDate : repository.findAll()) {
            result.add(mapper.eventDateToEventDateDTO(eventDate));
        }
        return result;
    }

    public List<EventDateDTO> getEventDatesByEventId(Long id) {
        if (id == null) throw new NullPointerException();
        Event event = eventRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        var result = new ArrayList<EventDateDTO>();
        for (var eventDate : repository.getAllByEvent(event)) {
            result.add(mapper.eventDateToEventDateDTO(eventDate));
        }
        return result;
    }

    public EventDateDTO getById(Long id) {
        if (id == null) throw new NullPointerException();
        return mapper.eventDateToEventDateDTO(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public EventDateDTO createEventDate(EventDateDTO eventDateDTO, String username) {
        if (eventDateDTO.getEvent() == null | eventDateDTO.getStart() == null) throw new NullPointerException();
        User user = userRepository.findByUsername(username);
        Event event = eventRepository.findById(eventDateDTO.getEvent()).orElseThrow(EntityNotFoundException::new);
        if (event.getOwner() != user) throw new IllegalArgumentException();
        EventDate eventDate = mapper.eventDateDTOToEventDate(eventDateDTO);
        eventDate.setTotalScore(0);
        return mapper.eventDateToEventDateDTO(repository.save(eventDate));
    }

    public void deleteEventDate(Long id, String username) {
        EventDate eventDate = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByUsername(username);
        if (eventDate.getEvent().getOwner() != user) throw new IllegalArgumentException();
        repository.deleteById(id);
    }
}
