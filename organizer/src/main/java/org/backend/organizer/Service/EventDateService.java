package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.EventDateDTO;
import org.backend.organizer.Mapper.EventDateMapper;
import org.backend.organizer.Model.Event;
import org.backend.organizer.Model.EventDate;
import org.backend.organizer.Repository.EventDateRepository;
import org.backend.organizer.Repository.EventRepository;
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

    public EventDateDTO createEventDate(EventDateDTO eventDateDTO) {
        if (eventDateDTO.getEvent() == null | eventDateDTO.getStart() == null | eventDateDTO.getEnd() == null) throw new NullPointerException();
        EventDate eventDate = mapper.eventDateDTOToEventDate(eventDateDTO);
        eventDate.setTotalScore(0);
        return mapper.eventDateToEventDateDTO(repository.save(eventDate));
    }

    public EventDateDTO updateEventDate(EventDateDTO eventDateUpdates) {
        if (eventDateUpdates == null) throw new NullPointerException();
        EventDate eventDate = repository.findById(eventDateUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        mapper.updateEventDateFromEventDateDTO(eventDateUpdates, eventDate);
        return mapper.eventDateToEventDateDTO(repository.save(eventDate));
    }

    public void deleteEventDate(Long id) {
        if(!repository.existsById(id)) throw new EntityNotFoundException();
        repository.deleteById(id);
    }
}
