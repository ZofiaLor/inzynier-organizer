package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.UserDTO;
import org.backend.organizer.Mapper.UserMapper;
import org.backend.organizer.Model.User;
import org.backend.organizer.Model.UserPrincipal;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository repository;

    @Autowired
    JWTService jwtService;

    @Autowired
    AuthenticationManager manager;

    @Autowired
    UserMapper mapper;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    //TODO Error proofing

    public List<UserDTO> getAllUsers() {
        var result = new ArrayList<UserDTO>();
        for (var user : repository.findAll()) {
            result.add(mapper.userToUserDTO(user));
        }
        return result;
    }

    public UserDTO getUserById(Long id) {
        if(id == null) throw new NullPointerException();
        return mapper.userToUserDTO(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public UserDTO register(UserDTO newUser) {
        User user = mapper.userDTOToUser(newUser);
        if (repository.existsByUsername(user.getUsername())) {
            return null;
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        return mapper.userToUserDTO(repository.save(user));
    }

    public String login(UserDTO user) {
        Authentication authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            ResponseCookie jwtCookie = jwtService.generateJwtCookie(userPrincipal);
            return jwtCookie.toString();
        } else {
            return "fail"; // request is 401 unauthorized anyway
        }
    }

    public String logout() {
        return jwtService.getCleanJwtCookie().toString();
    }

    public UserDTO changePrivilege(String newRole, String username, HttpServletRequest request) {
        if (jwtService.extractUsername(jwtService.getJwtFromCookies(request)).equals(username)) {
            throw new IllegalArgumentException();
        }
        User user = repository.findByUsername(username);
        if (newRole.equals("ROLE_ADMIN") || newRole.equals("ROLE_USER")) {
            user.setRole(newRole);
        }
        return mapper.userToUserDTO(repository.save(user));
    }

    public UserDTO getUserByUsername(String username) {
        User user = repository.findByUsername(username);
        if (user == null) throw new EntityNotFoundException();
        return mapper.userToUserDTO(user);
    }

    public UserDTO updateUser(UserDTO userUpdates) {
        if (userUpdates == null) throw new NullPointerException();
        User user = repository.findById(userUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        if (repository.existsByUsername(userUpdates.getUsername())) throw new IllegalArgumentException();
        //TODO use mapper to ignore nulls
        mapper.updateUserFromUserDTO(userUpdates, user);
        return mapper.userToUserDTO(repository.save(user));
    }

    //TODO don't delete your own user
    public void deleteUser(Long id) {
        if(!repository.existsById(id)) throw new EntityNotFoundException();
        repository.deleteById(id);
    }
}
