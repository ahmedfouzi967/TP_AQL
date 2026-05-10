package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Task> findTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Task> searchTasksByTitle(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(existingTask -> {
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setStatus(updatedTask.getStatus());
            return taskRepository.save(existingTask);
        }).orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return taskRepository.existsById(id);
    }
}
