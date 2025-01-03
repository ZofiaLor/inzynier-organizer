package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.backend.organizer.DTO.DirectoryDTO;
import org.backend.organizer.DTO.UserDTO;
import org.backend.organizer.Mapper.UserMapper;
import org.backend.organizer.Model.User;
import org.backend.organizer.Model.UserPrincipal;
import org.backend.organizer.Repository.UserRepository;
import org.backend.organizer.Request.Passwords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    UserRepository repository;

    @Autowired
    JWTService jwtService;
    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    DirectoryService directoryService;

    @Autowired
    AuthenticationManager manager;

    @Autowired
    UserMapper mapper;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public List<UserDTO> getAllUsers() {
        var result = new ArrayList<UserDTO>();
        for (var user : repository.findAll()) {
            user.setPassword("");
            result.add(mapper.userToUserDTO(user));
        }
        return result;
    }

    public List<UserDTO> getAllUsersSafe() {
        var result = new ArrayList<UserDTO>();
        for (var user : repository.findAll()) {
            User safeUser = new User(user.getUsername(), user.getName(), "");
            safeUser.setId(user.getId());
            result.add(mapper.userToUserDTO(safeUser));
        }
        return result;
    }

    public UserDTO getUserById(Long id) {
        if(id == null) throw new NullPointerException();
        User user = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        user.setPassword("");
        return mapper.userToUserDTO(user);
    }

    public UserDTO getUserByIdSafe(Long id) {
        if(id == null) throw new NullPointerException();
        User unsafe = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        User safe = new User(unsafe.getUsername(), unsafe.getName(), "");
        safe.setId(id);
        return mapper.userToUserDTO(safe);
    }

    public UserDTO register(UserDTO newUser) {
        if (newUser.getUsername() == null | newUser.getPassword() == null) throw new NullPointerException();
        User user = mapper.userDTOToUser(newUser);
        if (repository.existsByUsername(user.getUsername())) {
            return null;
        }
        user.setPassword(encoder.encode(user.getPassword()));
        if (repository.findAll().isEmpty()) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }
        user = repository.save(user);
        DirectoryDTO dir = new DirectoryDTO();
        dir.setName("Base Directory");
        directoryService.createDirectory(dir, user.getUsername(), true);
        return mapper.userToUserDTO(user);
    }

    public UserPrincipal login(UserDTO user) {
        if (user.getUsername() == null | user.getPassword() == null) throw new NullPointerException();
        Authentication authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()) {
            return (UserPrincipal) authentication.getPrincipal();
        } else {
            return null;
        }
    }

    public List<String> logout() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(principal.toString(), "anonymousUser")) {
            Long userId = ((UserPrincipal) principal).getUser().getId();
            refreshTokenService.deleteByUserId(userId);
        }

        var result = new ArrayList<String>();
        result.add(jwtService.getCleanJwtCookie().toString());
        result.add(jwtService.getCleanJwtRefreshCookie().toString());

        return result;
    }

    public String resetPassword(HttpServletRequest request, Passwords passwords) {
        String username = jwtService.extractUsername(jwtService.getJwtFromCookies(request));
        if (username == null | passwords.getNewPassword() == null || passwords.getOldPassword() == null) throw new NullPointerException();
        if (!repository.existsByUsername(username)) throw new EntityNotFoundException();
        User user = repository.findByUsername(username);
        if (encoder.matches(passwords.getOldPassword(), user.getPassword())) {
            user.setPassword(encoder.encode(passwords.getNewPassword()));
            repository.save(user);
            return "Success";
        }
        throw new IllegalArgumentException();
    }

    public UserDTO changePrivilege(String newRole, String username, HttpServletRequest request) {
        if (jwtService.extractUsername(jwtService.getJwtFromCookies(request)).equals(username)) {
            throw new IllegalArgumentException();
        }
        if (!repository.existsByUsername(username)) throw new EntityNotFoundException();
        User user = repository.findByUsername(username);
        if (newRole.equals("ROLE_ADMIN") || newRole.equals("ROLE_USER")) {
            user.setRole(newRole);
        }
        repository.save(user);
        user.setPassword("");
        return mapper.userToUserDTO(user);
    }

    public UserDTO updateUser(UserDTO userUpdates, String username) {
        if (userUpdates == null || userUpdates.getId() == null) throw new NullPointerException();
        User currentUser = repository.findByUsername(username);
        User user = repository.findById(userUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        if (currentUser != user) throw new IllegalArgumentException();
        if (userUpdates.getUsername() != null) {
            if (!Objects.equals(user.getUsername(), userUpdates.getUsername()) && repository.existsByUsername(userUpdates.getUsername())) throw new IllegalArgumentException();
            if (userUpdates.getUsername().isEmpty()) throw new IllegalArgumentException();
        }
        mapper.updateUserFromUserDTO(userUpdates, user);
        repository.save(user);
        user.setPassword("");
        return mapper.userToUserDTO(user);
    }

    public void deleteUser(Long id, String username) {
        User user = repository.findByUsername(username);
        if(!repository.existsById(id) || user == null) throw new EntityNotFoundException();
        if (id.equals(user.getId())) throw new IllegalArgumentException();
        repository.deleteById(id);
    }

    public List<String> deleteMyUser(String username) {
        User user = repository.findByUsername(username);
        if (user == null) throw new EntityNotFoundException();
        var result = this.logout();
        repository.deleteById(user.getId());
        return result;
    }
}
