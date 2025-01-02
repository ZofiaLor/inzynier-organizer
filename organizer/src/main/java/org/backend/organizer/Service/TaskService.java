package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.DTO.TaskDTO;
import org.backend.organizer.Mapper.FileMapper;
import org.backend.organizer.Model.Directory;
import org.backend.organizer.Model.Task;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.TaskRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {
    @Autowired
    TaskRepository repository;
    @Autowired
    FileMapper mapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    AccessDirectoryService adService;
    @Autowired
    AccessFileService afService;

    public TaskDTO createTask(TaskDTO newTask, String username) {
        if (newTask.getParent() == null) throw new NullPointerException();
        if (newTask.getDeadline() != null && !FileService.isValidDate(newTask.getDeadline())) throw new IllegalArgumentException();
        Task Task = mapper.taskDTOToTask(newTask);
        User owner = userRepository.findByUsername(username);
        Task.setCreationDate(LocalDateTime.now());
        Task.setOwner(owner);
        Directory parent = directoryRepository.findById(newTask.getParent()).orElseThrow(EntityNotFoundException::new);
        if (parent.getOwner() != owner) {
            throw new IllegalArgumentException();
        }
        if (newTask.getName() == null) Task.setName("Unnamed Task");
        return mapper.taskToTaskDTO(repository.save(Task));
    }

    public TaskDTO updateTask(TaskDTO TaskUpdates, String username) {
        if (TaskUpdates == null) throw new NullPointerException();
        Task task = repository.findById(TaskUpdates.getId()).orElseThrow(EntityNotFoundException::new);
        if (TaskUpdates.getDeadline() == null) task.setDeadline(null);
        else if (!FileService.isValidDate(TaskUpdates.getDeadline())) TaskUpdates.setDeadline(null);
        FileService.checkAccess(2, task.getId(), task.getOwner(), username, task.getParent(), userRepository, directoryRepository, afService, adService);
        if (TaskUpdates.getParent() != null && !Objects.equals(TaskUpdates.getParent(), task.getParent().getId())) {
            Directory parent = directoryRepository.findById(TaskUpdates.getParent()).orElseThrow(EntityNotFoundException::new);
            if (parent.getOwner() != task.getOwner()) {
                throw new IllegalArgumentException();
            }
        }
        mapper.updateTaskFromTaskDTO(TaskUpdates, task);
        return mapper.taskToTaskDTO(repository.save(task));
    }
}
