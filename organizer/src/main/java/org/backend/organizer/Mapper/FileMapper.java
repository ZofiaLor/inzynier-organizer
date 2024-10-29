package org.backend.organizer.Mapper;

import org.backend.organizer.DTO.EventDTO;
import org.backend.organizer.DTO.FileDTO;
import org.backend.organizer.DTO.NoteDTO;
import org.backend.organizer.DTO.TaskDTO;
import org.backend.organizer.Model.*;
import org.backend.organizer.Repository.DirectoryRepository;
import org.backend.organizer.Repository.EventDateRepository;
import org.backend.organizer.Repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class FileMapper {
    @Autowired
    DirectoryRepository directoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EventDateRepository eventDateRepository;

    @SubclassMapping(source = Event.class, target = EventDTO.class)
    @SubclassMapping(source = Note.class, target = NoteDTO.class)
    @SubclassMapping(source = Task.class, target = TaskDTO.class)
    @Mapping(source = "parent", target = "parent", qualifiedByName = "getLongParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getLongOwner")
    public abstract FileDTO fileToFileDTO(File file);

    @Named("getLongParent")
    Long getLongParent(Directory parent) {
        return parent.getId();
    }

    @Named("getLongOwner")
    Long getLongOwner(User owner){
        if (owner == null) return null;
        return owner.getId();
    }

    @Named("getListOfLongEventDates")
    List<Long> getListOfLongEventDates(List<EventDate> eventDates) {
        if (eventDates == null) return null;
        var result = new ArrayList<Long>();
        for (var eventDate : eventDates) {
            result.add(eventDate.getId());
        }
        return result;
    }

    @Mapping(source = "parent", target = "parent", qualifiedByName = "getLongParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getLongOwner")
    @Mapping(source = "eventDates", target = "eventDates", qualifiedByName = "getListOfLongEventDates")
    public abstract EventDTO eventToEventDTO(Event event);

    @Mapping(source = "parent", target = "parent", qualifiedByName = "getLongParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getLongOwner")
    public abstract NoteDTO noteToNoteDTO(Note note);

    @Mapping(source = "parent", target = "parent", qualifiedByName = "getLongParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getLongOwner")
    public abstract TaskDTO taskToTaskDTO(Task task);

    @SubclassMapping(source = EventDTO.class, target = Event.class)
    @SubclassMapping(source = NoteDTO.class, target = Note.class)
    @SubclassMapping(source = TaskDTO.class, target = Task.class)
    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getOwner")
    public abstract File fileDTOtoFile(FileDTO fileDTO);

    @Named("getParent")
    Directory getParent(Long parent) {
        return directoryRepository.getReferenceById(parent);
    }

    @Named("getOwner")
    User getOwner(Long owner) {
        if (owner == null) return null;
        return userRepository.getReferenceById(owner);
    }

    @Named("getListOfEventDates")
    List<EventDate> getListOfEventDates(List<Long> eventDates) {
        if (eventDates == null) return null;
        var result = new ArrayList<EventDate>();
        for (var eventDate : eventDates) {
            result.add(eventDateRepository.getReferenceById(eventDate));
        }
        return result;
    }

    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getOwner")
    @Mapping(source = "eventDates", target = "eventDates", qualifiedByName = "getListOfEventDates")
    public abstract Event eventDTOToEvent(EventDTO eventDTO);

    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getOwner")
    public abstract Note noteDTOtoNote(NoteDTO noteDTO);

    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(source = "owner", target = "owner", qualifiedByName = "getOwner")
    public abstract Task taskDTOToTask(TaskDTO taskDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    public abstract void updateFileFromFileDTO(FileDTO fileDTO, @MappingTarget File file);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(source = "eventDates", target = "eventDates", qualifiedByName = "getListOfEventDates")
    public abstract void updateEventFromEventDTO(EventDTO eventDTO, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    public abstract void updateNoteFromNoteDTO(NoteDTO noteDTO, @MappingTarget Note note);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "parent", target = "parent", qualifiedByName = "getParent")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    public abstract void updateTaskFromTaskDTO(TaskDTO taskDTO, @MappingTarget Task task);


}