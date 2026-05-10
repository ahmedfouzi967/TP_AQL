# Task Manager - Spring Boot + Testcontainers

Application de gestion de tâches développée avec **Spring Boot 3**, **MySQL**, et testée avec **Testcontainers**.

---

## Technologies

| Technologie | Version | Rôle |
|---|---|---|
| Java | 17 | Langage |
| Spring Boot | 3.2.3 | Framework principal |
| Spring Data JPA | 3.2.3 | Accès BDD |
| MySQL | 8.0 | Base de données |
| Testcontainers | 1.19.6 | Tests d'intégration avec Docker |
| JUnit 5 | 5.10+ | Framework de test |
| Mockito | 5.x | Mock pour tests unitaires |
| Docker | 24+ | Conteneurisation |

---

## Prérequis

- **Java 17+**
- **Maven 3.8+**
- **Docker** installé et en cours d'exécution (requis pour Testcontainers)
- (Optionnel) **MySQL 8** local pour lancer l'application

---

## Lancer l'application

### Avec Docker Compose (recommandé)

```bash
docker-compose up
```

L'application sera disponible sur `http://localhost:8080`

### Sans Docker (MySQL local requis)

Configurer `src/main/resources/application.properties` avec vos paramètres MySQL, puis :

```bash
mvn spring-boot:run
```

---

## Lancer les tests

### Tous les tests (Docker requis pour les tests d'intégration)

```bash
mvn test
```

### Tests unitaires uniquement (sans Docker)

```bash
mvn test -Dtest="*UnitTest"
```

### Tests d'intégration (Docker requis)

```bash
mvn test -Dtest="*IntegrationTest"
```

> **Important** : Docker doit être démarré pour les tests d'intégration car Testcontainers lance automatiquement un conteneur MySQL réel.

---

## Structure du projet

```
task-manager/
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/
│   │   │   ├── TaskManagerApplication.java
│   │   │   ├── controller/TaskController.java    ← API REST
│   │   │   ├── model/Task.java                   ← Entité JPA
│   │   │   ├── repository/TaskRepository.java    ← Accès données
│   │   │   └── service/TaskService.java          ← Logique métier
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/taskmanager/
│       │   ├── unit/
│       │   │   └── TaskServiceUnitTest.java           ← Mockito (sans Docker)
│       │   └── integration/
│       │       ├── TaskServiceIntegrationTest.java    ← Testcontainers MySQL
│       │       ├── TaskControllerIntegrationTest.java ← REST + Testcontainers
│       │       └── TaskRepositoryIntegrationTest.java ← JPA + Testcontainers
│       └── resources/
│           └── application-test.properties
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## API REST

| Méthode | URL | Description |
|---|---|---|
| `POST` | `/api/tasks` | Créer une tâche |
| `GET` | `/api/tasks` | Lister toutes les tâches |
| `GET` | `/api/tasks/{id}` | Récupérer une tâche par ID |
| `GET` | `/api/tasks/status/{status}` | Filtrer par statut (TODO/IN_PROGRESS/DONE) |
| `GET` | `/api/tasks/search?keyword=x` | Rechercher par titre |
| `PUT` | `/api/tasks/{id}` | Mettre à jour une tâche |
| `DELETE` | `/api/tasks/{id}` | Supprimer une tâche |

### Exemple de corps JSON

```json
{
  "title": "Préparer la réunion",
  "description": "Préparer les slides pour le sprint review",
  "status": "TODO"
}
```

---

## Architecture des tests

### Tests unitaires (`TaskServiceUnitTest`)
- Utilise **Mockito** pour mocker `TaskRepository`
- Pas besoin de Docker ni de base de données
- Rapide (~1 seconde)

### Tests d'intégration (`TaskServiceIntegrationTest`, `TaskControllerIntegrationTest`, `TaskRepositoryIntegrationTest`)
- Utilise **Testcontainers** pour démarrer un **vrai conteneur MySQL 8.0** dans Docker
- `@DynamicPropertySource` injecte dynamiquement l'URL de connexion
- `@BeforeEach deleteAll()` garantit l'isolation entre les tests
- Testent le comportement réel avec une vraie base de données

---

## Analyse : Tests existants vs Testcontainers

### Approche classique (H2 in-memory)
**Avantages** : rapide, pas de Docker requis  
**Inconvénients** : H2 ≠ MySQL (dialecte différent, comportements différents), faux sentiment de sécurité

### Approche Testcontainers (ce projet)
**Avantages** :
- MySQL **identique** à la production
- Isolation totale entre tests
- Cycle de vie des conteneurs géré automatiquement
- Reproductible sur n'importe quelle machine avec Docker

**Inconvénients** :
- Plus lent (démarrage du conteneur ~10-20s)
- Docker requis sur la machine de test/CI

---

## Scénarios de test ajoutés (nouveaux)

1. **Cycle de vie complet** (`testFullTaskLifecycle`) : création → mise à jour du statut → finalisation → suppression
2. **Isolation des données** (`testMultipleTasksPersistenceAndIsolation`) : vérifie que le `@BeforeEach` isole correctement chaque test
3. **Validation HTTP** (`testCreateTask_EmptyTitle_HTTP400`) : vérifie le retour 400 sur titre vide
4. **Recherche insensible à la casse** (`testFindByTitleContainingIgnoreCase`) : vérifie le comportement MySQL réel
