package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.VoteDTO;
import org.backend.organizer.Mapper.VoteMapper;
import org.backend.organizer.Model.EventDate;
import org.backend.organizer.Model.User;
import org.backend.organizer.Model.Vote;
import org.backend.organizer.Repository.EventDateRepository;
import org.backend.organizer.Repository.UserRepository;
import org.backend.organizer.Repository.VoteRepository;
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

    public List<VoteDTO> getAllVotes() {
        var result = new ArrayList<VoteDTO>();
        for (var vote : repository.findAll()) {
            result.add(mapper.voteToVoteDTO(vote));
        }
        return result;
    }

    public List<VoteDTO> getVotesByUserId(Long id) {
        if (id == null) throw new NullPointerException();
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        var result = new ArrayList<VoteDTO>();
        for (var vote : repository.getAllByUser(user)) {
            result.add(mapper.voteToVoteDTO(vote));
        }
        return result;
    }

    public List<VoteDTO> getVotesByEventDateId(Long id) {
        if (id == null) throw new NullPointerException();
        EventDate eventDate = eventDateRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        var result = new ArrayList<VoteDTO>();
        for (var vote : repository.getAllByEventDate(eventDate)) {
            result.add(mapper.voteToVoteDTO(vote));
        }
        return result;
    }

    public VoteDTO getVoteById(Long id) {
        if (id == null) throw new NullPointerException();
        return mapper.voteToVoteDTO(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public VoteDTO createVote(VoteDTO voteDTO, String username) {
        if (voteDTO.getEventDate() == null) throw new NullPointerException();
        EventDate eventDate = eventDateRepository.findById(voteDTO.getEventDate()).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByUsername(username);
        Vote vote;
        if (repository.findByUserAndEventDate(user, eventDate).isEmpty()){
            vote = mapper.voteDTOToVote(voteDTO);
            vote.setUser(user);
            eventDate.setTotalScore(eventDate.getTotalScore() + vote.getScore());
        } else {
            vote = repository.findByUserAndEventDate(user, eventDate).get();
            eventDate.setTotalScore(eventDate.getTotalScore() - vote.getScore() + voteDTO.getScore()); // total - old + new
            mapper.updateVoteFromVoteDTO(voteDTO, vote);
        }

        eventDateRepository.save(eventDate);
        return mapper.voteToVoteDTO(repository.save(vote));
    }

    public VoteDTO updateVote(VoteDTO voteUpdates) {
        if (voteUpdates == null) throw new NullPointerException();
        Vote vote = repository.findById(voteUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        EventDate eventDate = eventDateRepository.findById(vote.getEventDate().getId()).orElseThrow(EntityNotFoundException::new);
        eventDate.setTotalScore(eventDate.getTotalScore() - vote.getScore() + voteUpdates.getScore()); // total - old + new
        eventDateRepository.save(eventDate);
        mapper.updateVoteFromVoteDTO(voteUpdates, vote);
        return mapper.voteToVoteDTO(repository.save(vote));
    }

    public void deleteVote(Long id) {
        Vote vote = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        EventDate eventDate = vote.getEventDate();
        eventDate.setTotalScore(eventDate.getTotalScore() - vote.getScore());
        eventDateRepository.save(eventDate);
        repository.deleteById(id);
    }
}