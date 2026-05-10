package com.taskmanager.integration;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.service.TaskService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration avec Testcontainers + MySQL réel dans Docker.
 *
 * Approche :
 * - @Testcontainers démarre automatiquement un conteneur MySQL Docker
 * - @DynamicPropertySource injecte les propriétés de connexion dynamiquement
 * - Chaque test s'exécute dans une transaction rollbackée → isolation garantie
 */
@SpringBootTest
@Testcontainers
@DisplayName("Tests d'intégration - TaskService avec MySQL (Testcontainers)")
class TaskServiceIntegrationTest {

    // Un seul conteneur partagé pour toute la classe (plus rapide)
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("taskmanager_test")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(false);

    /**
     * Injecte dynamiquement l'URL, le username et le password
     * du conteneur MySQL démarré par Testcontainers dans le contexte Spring.
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQLDialect");
    }

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    // ===================== TESTS CRUD DE BASE =====================

    @Test
    @DisplayName("Créer et retrouver une tâche dans MySQL réel")
    void testCreateTask_Success() {
        // Arrange
        Task task = new Task("Tâche de test", "Description de test");

        // Act
        Task saved = taskService.saveTask(task);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Tâche de test");
        assertThat(saved.getDescription()).isEqualTo("Description de test");
        assertThat(saved.getStatus()).isEqualTo(Task.TaskStatus.TODO);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Récupérer une tâche par ID - trouvée")
    void testGetTask_Found() {
        // Arrange
        Task saved = taskService.saveTask(new Task("Ma tâche", "Description"));

        // Act
        Optional<Task> result = taskService.findTaskById(saved.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Ma tâche");
    }

    @Test
    @DisplayName("Récupérer une tâche par ID - non trouvée")
    void testGetTask_NotFound() {
        Optional<Task> result = taskService.findTaskById(9999L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Supprimer une tâche par ID")
    void testDeleteTask_Success() {
        // Arrange
        Task saved = taskService.saveTask(new Task("À supprimer", "Desc"));
        Long id = saved.getId();

        // Act
        taskService.deleteTaskById(id);

        // Assert
        Optional<Task> deleted = taskService.findTaskById(id);
        assertThat(deleted).isEmpty();
    }

    // ===================== TESTS MISE À JOUR =====================

    @Test
    @DisplayName("Mettre à jour une tâche existante")
    void testUpdateTask_Success() {
        // Arrange
        Task original = taskService.saveTask(new Task("Original", "Desc originale"));

        Task updateData = new Task();
        updateData.setTitle("Titre mis à jour");
        updateData.setDescription("Nouvelle description");
        updateData.setStatus(Task.TaskStatus.IN_PROGRESS);

        // Act
        Task updated = taskService.updateTask(original.getId(), updateData);

        // Assert
        assertThat(updated.getTitle()).isEqualTo("Titre mis à jour");
        assertThat(updated.getStatus()).isEqualTo(Task.TaskStatus.IN_PROGRESS);
    }

    // ===================== TESTS FILTRAGE =====================

    @Test
    @DisplayName("Récupérer toutes les tâches")
    void testFindAllTasks() {
        // Arrange
        taskService.saveTask(new Task("Tâche A", "Desc A"));
        taskService.saveTask(new Task("Tâche B", "Desc B"));
        taskService.saveTask(new Task("Tâche C", "Desc C"));

        // Act
        List<Task> all = taskService.findAllTasks();

        // Assert
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("Filtrer les tâches par statut TODO")
    void testFindTasksByStatus_TODO() {
        // Arrange
        taskService.saveTask(new Task("Todo 1", "Desc"));
        taskService.saveTask(new Task("Todo 2", "Desc"));

        Task inProgress = new Task("En cours", "Desc");
        inProgress.setStatus(Task.TaskStatus.IN_PROGRESS);
        taskService.saveTask(inProgress);

        // Act
        List<Task> todos = taskService.findTasksByStatus(Task.TaskStatus.TODO);

        // Assert
        assertThat(todos).hasSize(2);
        assertThat(todos).allMatch(t -> t.getStatus() == Task.TaskStatus.TODO);
    }

    @Test
    @DisplayName("Filtrer les tâches par statut DONE")
    void testFindTasksByStatus_DONE() {
        // Arrange
        Task done1 = new Task("Terminée 1", "Desc");
        done1.setStatus(Task.TaskStatus.DONE);
        taskService.saveTask(done1);

        taskService.saveTask(new Task("Pas terminée", "Desc"));

        // Act
        List<Task> doneTasks = taskService.findTasksByStatus(Task.TaskStatus.DONE);

        // Assert
        assertThat(doneTasks).hasSize(1);
        assertThat(doneTasks.get(0).getTitle()).isEqualTo("Terminée 1");
    }

    @Test
    @DisplayName("Recherche de tâches par mot-clé dans le titre")
    void testSearchTasksByTitle() {
        // Arrange
        taskService.saveTask(new Task("Rapport financier Q4", "Desc"));
        taskService.saveTask(new Task("Réunion d'équipe", "Desc"));
        taskService.saveTask(new Task("Rapport mensuel", "Desc"));

        // Act
        List<Task> results = taskService.searchTasksByTitle("rapport");

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(t -> t.getTitle().toLowerCase().contains("rapport"));
    }

    // ===================== TEST SCÉNARIO COMPLET =====================

    @Test
    @DisplayName("Scénario complet : cycle de vie d'une tâche")
    void testFullTaskLifecycle() {
        // 1. Création
        Task task = taskService.saveTask(new Task("Sprint planning", "Planifier le sprint 5"));
        assertThat(task.getId()).isNotNull();
        assertThat(task.getStatus()).isEqualTo(Task.TaskStatus.TODO);

        // 2. Mise à jour du statut
        Task updateData = new Task();
        updateData.setTitle("Sprint planning");
        updateData.setDescription("Planifier le sprint 5");
        updateData.setStatus(Task.TaskStatus.IN_PROGRESS);
        taskService.updateTask(task.getId(), updateData);

        Optional<Task> inProgress = taskService.findTaskById(task.getId());
        assertThat(inProgress).isPresent();
        assertThat(inProgress.get().getStatus()).isEqualTo(Task.TaskStatus.IN_PROGRESS);

        // 3. Finalisation
        updateData.setStatus(Task.TaskStatus.DONE);
        taskService.updateTask(task.getId(), updateData);

        Optional<Task> done = taskService.findTaskById(task.getId());
        assertThat(done).isPresent();
        assertThat(done.get().getStatus()).isEqualTo(Task.TaskStatus.DONE);

        // 4. Suppression
        taskService.deleteTaskById(task.getId());
        assertThat(taskService.findTaskById(task.getId())).isEmpty();
    }

    // ===================== TEST SCÉNARIO AJOUTÉ (nouveau) =====================

    @Test
    @DisplayName("[NOUVEAU] Vérifier la persistance de plusieurs tâches et isolation des données")
    void testMultipleTasksPersistenceAndIsolation() {
        // Ce test vérifie que les données sont bien isolées entre tests
        // grâce au @BeforeEach deleteAll()

        assertThat(taskService.findAllTasks()).isEmpty();

        for (int i = 1; i <= 5; i++) {
            taskService.saveTask(new Task("Tâche " + i, "Description " + i));
        }

        List<Task> all = taskService.findAllTasks();
        assertThat(all).hasSize(5);

        // Supprimer en lot et vérifier
        taskService.deleteTaskById(all.get(0).getId());
        assertThat(taskService.findAllTasks()).hasSize(4);
    }
}
