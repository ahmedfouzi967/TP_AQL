package com.taskmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration de l'API REST avec Testcontainers.
 * Teste les endpoints HTTP end-to-end avec une vraie base MySQL dans Docker.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Tests d'intégration - API REST avec Testcontainers")
class TaskControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("taskmanager_api_test")
            .withUsername("testuser")
            .withPassword("testpass");

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/tasks - Créer une tâche avec succès")
    void testCreateTask_HTTP201() throws Exception {
        Task task = new Task("Nouvelle tâche", "Description via API");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Nouvelle tâche"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("POST /api/tasks - Titre vide retourne 400")
    void testCreateTask_EmptyTitle_HTTP400() throws Exception {
        Task task = new Task("", "Description");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Récupérer une tâche existante")
    void testGetTask_HTTP200() throws Exception {
        Task saved = taskRepository.save(new Task("Tâche existante", "Desc"));

        mockMvc.perform(get("/api/tasks/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Tâche existante"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - ID inexistant retourne 404")
    void testGetTask_NotFound_HTTP404() throws Exception {
        mockMvc.perform(get("/api/tasks/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks - Récupérer toutes les tâches")
    void testGetAllTasks_HTTP200() throws Exception {
        taskRepository.save(new Task("Tâche A", "Desc A"));
        taskRepository.save(new Task("Tâche B", "Desc B"));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Tâche A", "Tâche B")));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - Mettre à jour une tâche")
    void testUpdateTask_HTTP200() throws Exception {
        Task saved = taskRepository.save(new Task("Ancien titre", "Ancienne desc"));

        Task update = new Task("Nouveau titre", "Nouvelle desc");
        update.setStatus(Task.TaskStatus.DONE);

        mockMvc.perform(put("/api/tasks/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nouveau titre"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Supprimer une tâche")
    void testDeleteTask_HTTP204() throws Exception {
        Task saved = taskRepository.save(new Task("À supprimer", "Desc"));

        mockMvc.perform(delete("/api/tasks/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Vérifier que la tâche n'existe plus
        mockMvc.perform(get("/api/tasks/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks/status/{status} - Filtrer par statut")
    void testGetTasksByStatus_HTTP200() throws Exception {
        taskRepository.save(new Task("Todo 1", "Desc"));

        Task done = new Task("Done 1", "Desc");
        done.setStatus(Task.TaskStatus.DONE);
        taskRepository.save(done);

        mockMvc.perform(get("/api/tasks/status/TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    @DisplayName("GET /api/tasks/search?keyword= - Recherche par titre")
    void testSearchTasks_HTTP200() throws Exception {
        taskRepository.save(new Task("Rapport Q1", "Desc"));
        taskRepository.save(new Task("Réunion hebdo", "Desc"));
        taskRepository.save(new Task("Rapport annuel", "Desc"));

        mockMvc.perform(get("/api/tasks/search").param("keyword", "rapport"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
