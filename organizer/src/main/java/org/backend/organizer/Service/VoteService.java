package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.VoteDTO;
import org.backend.organizer.Mapper.VoteMapper;
import org.backend.organizer.Model.EventDate;
import org.backend.organizer.Model.User;
import org.backend.organizer.Model.Vote;
import org.backend.organizer.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoteService {
    @Autowired
    VoteRepository repository;
    @Autowired
    VoteMapper mapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EventDateRepository eventDateRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    AccessDirectoryService adService;
    @Autowired
    AccessFileService afService;

    public List<VoteDTO> getVotesByEventDateId(Long id) {
        if (id == null) throw new NullPointerException();
        EventDate eventDate = eventDateRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        var result = new ArrayList<VoteDTO>();
        for (var vote : repository.getAllByEventDate(eventDate)) {
            result.add(mapper.voteToVoteDTO(vote));
        }
        return result;
    }

    public VoteDTO getVoteByUserAndEventDate(String username, Long edId) {
        if (username == null || edId == null) throw new NullPointerException();
        User user = userRepository.findByUsername(username);
        EventDate eventDate = eventDateRepository.findById(edId).orElseThrow(EntityNotFoundException::new);
        Vote vote = repository.findByUserAndEventDate(user, eventDate).orElseThrow(EntityNotFoundException::new);
        return mapper.voteToVoteDTO(vote);
    }

    public VoteDTO createVote(VoteDTO voteDTO, String username) {
        if (voteDTO.getEventDate() == null) throw new NullPointerException();
        EventDate eventDate = eventDateRepository.findById(voteDTO.getEventDate()).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByUsername(username);
        FileService.checkAccess(1, eventDate.getEvent().getId(), eventDate.getEvent().getOwner(), username, eventDate.getEvent().getParent(), userRepository, directoryRepository, afService, adService);
        Vote vote;
        if (repository.findByUserAndEventDate(user, eventDate).isEmpty()){
            vote = mapper.voteDTOToVote(voteDTO);
            vote.setUser(user);
            eventDate.setTotalScore(eventDate.getTotalScore() + vote.getScore());
        } else {
            vote = repository.findByUserAndEventDate(user, eventDate).get();
            eventDate.setTotalScore(eventDate.getTotalScore() - vote.getScore() + voteDTO.getScore()); // total - old + new
            vote.setScore(voteDTO.getScore());
        }

        eventDateRepository.save(eventDate);
        return mapper.voteToVoteDTO(repository.save(vote));
    }

    public void deleteVote(Long id, String username) {
        Vote vote = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByUsername(username);
        if (user != vote.getUser()) throw new IllegalArgumentException();
        EventDate eventDate = vote.getEventDate();
        eventDate.setTotalScore(eventDate.getTotalScore() - vote.getScore());
        eventDateRepository.save(eventDate);
        repository.deleteById(id);
    }
}
