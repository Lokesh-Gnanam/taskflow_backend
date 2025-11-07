package com.examly.springapp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.examly.springapp.model.Task;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.TaskRepository;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasksByUser(User user) {
        return taskRepository.findByUserAndArchivedFalse(user);
    }

    public List<Task> getTasksByUserAndStatus(User user, Task.Status status) {
        return taskRepository.findByUserAndStatusAndArchivedFalse(user, status);
    }

    public List<Task> getTasksByUserAndPriority(User user, Task.Priority priority) {
        return taskRepository.findByUserAndPriorityAndArchivedFalse(user, priority);
    }

    public List<Task> getTasksByUserAndDeadlineDate(User user, String deadlineDate) {
        return taskRepository.findByUserAndDeadlineDateAndArchivedFalse(user, deadlineDate);
    }

    public List<Task> searchTasks(User user, String searchTerm) {
        return taskRepository.findByUserAndTitleContainingOrDescriptionContainingAndArchivedFalse(user, searchTerm);
    }

    public List<Task> getTasksByDateRange(User user, String startDate, String endDate) {
        return taskRepository.findByUserAndDeadlineDateBetweenAndArchivedFalse(user, startDate, endDate);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task existingTask = taskRepository.findById(id).orElse(null);
        if (existingTask != null) {
            existingTask.setTitle(taskDetails.getTitle());
            existingTask.setDescription(taskDetails.getDescription());
            existingTask.setStatus(taskDetails.getStatus());
            existingTask.setPriority(taskDetails.getPriority());
            existingTask.setDeadlineDate(taskDetails.getDeadlineDate());
            existingTask.setReminderTime(taskDetails.getReminderTime());
            existingTask.setArchived(taskDetails.getArchived());
            return taskRepository.save(existingTask);
        }
        return null;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Task archiveTask(Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setArchived(true);
            return taskRepository.save(task);
        }
        return null;
    }

    public Task markTaskComplete(Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setStatus(Task.Status.COMPLETED);
            return taskRepository.save(task);
        }
        return null;
    }

    // NEW: Update task status method
    public Task updateTaskStatus(Long id, Task.Status status) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setStatus(status);
            return taskRepository.save(task);
        }
        return null;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}