package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.TaskDTO;
import org.backend.organizer.Mapper.FileMapper;
import org.backend.organizer.Model.Task;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.TaskRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository repository;
    @Autowired
    FileMapper mapper;
    @Autowired
    UserRepository userRepository;

    public List<TaskDTO> getAllTasksByUser(String username) {
        User user = userRepository.findByUsername(username);
        var result = new ArrayList<TaskDTO>();
        for (var file : repository.getAllByOwner(user)) {
            result.add(mapper.taskToTaskDTO(file));
        }
        return result;
    }

    public TaskDTO getTaskByID(Long id) {
        if (id == null) throw new NullPointerException();
        return mapper.taskToTaskDTO(repository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public TaskDTO createTask(TaskDTO newTask, String username) {
        Task Task = mapper.taskDTOToTask(newTask);
        User owner = userRepository.findByUsername(username);
        Task.setCreationDate(LocalDateTime.now());
        Task.setOwner(owner);
        return mapper.taskToTaskDTO(repository.save(Task));
    }

    public TaskDTO updateTask(TaskDTO TaskUpdates) {
        if (TaskUpdates == null) throw new NullPointerException();
        Task Task = repository.findById(TaskUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        mapper.updateTaskFromTaskDTO(TaskUpdates, Task);
        return mapper.taskToTaskDTO(repository.save(Task));
    }
}
