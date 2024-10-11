package org.backend.organizer.Service;

import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository repository;

    @Autowired
    JWTService jwtService;

    @Autowired
    AuthenticationManager manager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    //TODO Error proofing

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User getUserById(Long id) {
        if(id == null) throw new NullPointerException();
        return repository.getUserById(id); // does not work with getReferenceById
    }

    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public String login(User user) {
        Authentication authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        } else {
            return "fail";
        }
    }

    public User getUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public User updateUser(User userUpdates) {
        User user = repository.getUserById(userUpdates.getId());
        if (userUpdates.getEmail() != null) user.setEmail(userUpdates.getEmail());
        if (userUpdates.getName() != null) user.setName(userUpdates.getName());
        if (userUpdates.getUsername() != null) user.setUsername(userUpdates.getUsername());
        if (userUpdates.getPassword() != null) user.setPassword(encoder.encode(userUpdates.getPassword()));
        return repository.save(user);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}
