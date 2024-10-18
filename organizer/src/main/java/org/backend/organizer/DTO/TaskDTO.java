package org.backend.organizer.DTO;

import java.time.LocalDateTime;

public class TaskDTO extends FileDTO{
    private LocalDateTime deadline;
    private boolean isFinished;

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
