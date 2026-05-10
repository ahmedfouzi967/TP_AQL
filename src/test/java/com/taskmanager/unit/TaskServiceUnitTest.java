package com.taskmanager.unit;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - TaskService")
class TaskServiceUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task("Tâche 1", "Description de la tâche 1");
        task1.setId(1L);
        task1.setStatus(Task.TaskStatus.TODO);

        task2 = new Task("Tâche 2", "Description de la tâche 2");
        task2.setId(2L);
        task2.setStatus(Task.TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Sauvegarder une tâche avec succès")
    void testSaveTask_Success() {
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        Task saved = taskService.saveTask(task1);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getTitle()).isEqualTo("Tâche 1");
        verify(taskRepository, times(1)).save(task1);
    }

    @Test
    @DisplayName("Trouver une tâche par ID - trouvée")
    void testFindTaskById_Found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        Optional<Task> result = taskService.findTaskById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Tâche 1");
    }

    @Test
    @DisplayName("Trouver une tâche par ID - non trouvée")
    void testFindTaskById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.findTaskById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Récupérer toutes les tâches")
    void testFindAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.findAllTasks();

        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getTitle)
                .containsExactly("Tâche 1", "Tâche 2");
    }

    @Test
    @DisplayName("Filtrer les tâches par statut")
    void testFindTasksByStatus() {
        when(taskRepository.findByStatus(Task.TaskStatus.TODO))
                .thenReturn(List.of(task1));

        List<Task> todos = taskService.findTasksByStatus(Task.TaskStatus.TODO);

        assertThat(todos).hasSize(1);
        assertThat(todos.get(0).getStatus()).isEqualTo(Task.TaskStatus.TODO);
    }

    @Test
    @DisplayName("Supprimer une tâche par ID")
    void testDeleteTaskById() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTaskById(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Mettre à jour une tâche - ID inexistant lance exception")
    void testUpdateTask_NotFound_ThrowsException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Task updateData = new Task("Nouveau titre", "Nouvelle description");

        assertThatThrownBy(() -> taskService.updateTask(99L, updateData))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found with id: 99");
    }

    @Test
    @DisplayName("Vérifier l'existence d'une tâche")
    void testExistsById() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThat(taskService.existsById(1L)).isTrue();
        assertThat(taskService.existsById(99L)).isFalse();
    }
}
