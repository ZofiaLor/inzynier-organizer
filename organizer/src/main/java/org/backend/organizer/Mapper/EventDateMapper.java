package org.backend.organizer.Mapper;

import org.backend.organizer.DTO.EventDateDTO;
import org.backend.organizer.Model.Event;
import org.backend.organizer.Model.EventDate;
import org.backend.organizer.Model.Vote;
import org.backend.organizer.Repository.EventRepository;
import org.backend.organizer.Repository.VoteRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EventDateMapper {
    @Autowired
    EventRepository eventRepository;
    @Autowired
    VoteRepository voteRepository;
    @Mapping(source = "event", target = "event", qualifiedByName = "getLongEvent")
    @Mapping(source = "votes", target = "votes", qualifiedByName = "getListOfLongVotes")
    public abstract EventDateDTO eventDateToEventDateDTO(EventDate eventDate);

    @Named("getLongEvent")
    Long getLongEvent(Event event) {
        if (event == null) return null;
        return event.getId();
    }

    @Named("getListOfLongVotes")
    List<Long> getListOfLongVotes(List<Vote> votes) {
        if (votes == null) return null;
        var result = new ArrayList<Long>();
        for (var vote : votes) {
            result.add(vote.getId());
        }
        return result;
    }

    @Mapping(source = "event", target = "event", qualifiedByName = "getEvent")
    @Mapping(source = "votes", target = "votes", qualifiedByName = "getListOfVotes")
    public abstract EventDate eventDateDTOToEventDate(EventDateDTO eventDateDTO);

    @Named("getEvent")
    Event getEvent(Long id) {
        if (id == null) return null;
        return eventRepository.getReferenceById(id);
    }

    @Named("getListOfVotes")
    List<Vote> getListOfVotes(List<Long> ids) {
        if (ids == null) return null;
        var result = new ArrayList<Vote>();
        for (var id : ids) {
            result.add(voteRepository.getReferenceById(id));
        }
        return result;
    }
}
