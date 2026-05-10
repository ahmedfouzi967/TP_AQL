package com.taskmanager.integration;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration de la couche Repository avec Testcontainers.
 * Utilise @DataJpaTest pour charger uniquement la couche JPA.
 */
@DataJpaTest
@Testcontainers
@DisplayName("Tests d'intégration - TaskRepository avec MySQL (Testcontainers)")
class TaskRepositoryIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("taskmanager_repo_test")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.test.database.replace", () -> "none");
    }

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        entityManager.flush();
    }

    @Test
    @DisplayName("Sauvegarder et retrouver une tâche")
    void testSaveAndFind() {
        Task task = new Task("Test Repository", "Description test");
        taskRepository.save(task);
        entityManager.flush();
        entityManager.clear();

        Optional<Task> found = taskRepository.findById(task.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Repository");
    }

    @Test
    @DisplayName("Trouver par statut")
    void testFindByStatus() {
        taskRepository.save(new Task("Todo Task", "Desc"));
        
        Task done = new Task("Done Task", "Desc");
        done.setStatus(Task.TaskStatus.DONE);
        taskRepository.save(done);
        entityManager.flush();

        List<Task> todos = taskRepository.findByStatus(Task.TaskStatus.TODO);
        List<Task> dones = taskRepository.findByStatus(Task.TaskStatus.DONE);

        assertThat(todos).hasSize(1);
        assertThat(dones).hasSize(1);
    }

    @Test
    @DisplayName("Recherche insensible à la casse par titre")
    void testFindByTitleContainingIgnoreCase() {
        taskRepository.save(new Task("Rapport Financier", "Desc"));
        taskRepository.save(new Task("rapport mensuel", "Desc"));
        taskRepository.save(new Task("Réunion d'équipe", "Desc"));
        entityManager.flush();

        List<Task> results = taskRepository.findByTitleContainingIgnoreCase("rapport");

        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("Supprimer une tâche et vérifier l'absence")
    void testDeleteById() {
        Task task = taskRepository.save(new Task("À supprimer", "Desc"));
        entityManager.flush();
        Long id = task.getId();

        taskRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        assertThat(taskRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Compter le nombre de tâches")
    void testCount() {
        assertThat(taskRepository.count()).isEqualTo(0);

        taskRepository.save(new Task("T1", "D1"));
        taskRepository.save(new Task("T2", "D2"));
        entityManager.flush();

        assertThat(taskRepository.count()).isEqualTo(2);
    }
}
