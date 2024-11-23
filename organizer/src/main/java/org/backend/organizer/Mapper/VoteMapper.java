package org.backend.organizer.Mapper;

import org.backend.organizer.DTO.VoteDTO;
import org.backend.organizer.Model.EventDate;
import org.backend.organizer.Model.User;
import org.backend.organizer.Model.Vote;
import org.backend.organizer.Repository.EventDateRepository;
import org.backend.organizer.Repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class VoteMapper {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EventDateRepository eventDateRepository;

    @Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "getLongEventDate")
    @Mapping(source = "user", target = "user", qualifiedByName = "getLongUser")
    public abstract VoteDTO voteToVoteDTO(Vote vote);

    @Named("getLongEventDate")
    Long getLongEventDate(EventDate eventDate) {
        if (eventDate == null) return null;
        return eventDate.getId();
    }

    @Named("getLongUser")
    Long getLongUser(User user) {
        if (user == null) return null;
        return user.getId();
    }

    @Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "getEventDate")
    @Mapping(source = "user", target = "user", qualifiedByName = "getUser")
    public abstract Vote voteDTOToVote(VoteDTO voteDTO);

    @Named("getEventDate")
    EventDate getEventDate(Long id) {
        if (id == null) return null;
        return eventDateRepository.getReferenceById(id);
    }

    @Named("getUser")
    User getUser(Long id) {
        if (id == null) return null;
        return userRepository.getReferenceById(id);
    }
}
