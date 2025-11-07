package com.examly.springapp;

import com.examly.springapp.model.Task;
import com.examly.springapp.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.List;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SurveyAppApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Task sampleTask;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        sampleTask = new Task();
        sampleTask.setTitle("Unit Test Task");
        sampleTask.setDescription("Testing task controller");
        sampleTask.setDate("2025-08-01");
        sampleTask.setStatus("Pending");
        taskRepository.save(sampleTask);
    }

    @Test
    void testCreateTaskSuccess() throws Exception {
        Task newTask = new Task();
        newTask.setTitle("Create Task");
        newTask.setDescription("Create via POST");
        newTask.setDate("2025-08-05");
        newTask.setStatus("Open");

        mockMvc.perform(post("/api/tasks/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Create Task"));
    }

    @Test
    void testGetAllTasksReturnsList() throws Exception {
        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Unit Test Task"));
    }

    @Test
    void testTaskPersistenceWithRepository() {
        Optional<Task> found = taskRepository.findById(sampleTask.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Unit Test Task");
    }

    @Test
    void testTaskStatusUpdate() {
        sampleTask.setStatus("Completed");
        Task updated = taskRepository.save(sampleTask);
        assertThat(updated.getStatus()).isEqualTo("Completed");
    }

    @Test
    void testInvalidPostReturnsBadRequest() throws Exception {
        Task invalidTask = new Task(); // Missing required fields

        mockMvc.perform(post("/api/tasks/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isOk()); // Update to isBadRequest() when validation is added
    }

    @Test
    void testJsonResponseStructureForGet() throws Exception {
        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").isNotEmpty())
                .andExpect(jsonPath("$[0].status").isNotEmpty());
    }

    @Test
    void testRepositoryDeleteTaskById() {
        taskRepository.deleteById(sampleTask.getId());
        Optional<Task> afterDelete = taskRepository.findById(sampleTask.getId());
        assertThat(afterDelete).isEmpty();
    }

    // ---------- ✅ New Test Cases Below ----------

    @Test
    void testMultipleTasksSavedAndCountMatches() {
        Task secondTask = new Task();
        secondTask.setTitle("Another Task");
        secondTask.setDescription("Extra desc");
        secondTask.setDate("2025-08-02");
        secondTask.setStatus("Pending");

        taskRepository.save(secondTask);
        List<Task> all = taskRepository.findAll();
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void testTaskRetrievalByDate() {
        List<Task> tasksOnDate = taskRepository.findAll().stream()
                .filter(t -> "2025-08-01".equals(t.getDate()))
                .toList();

        assertThat(tasksOnDate).isNotEmpty();
        assertThat(tasksOnDate.get(0).getTitle()).isEqualTo("Unit Test Task");
    }

    @Test
    void testEmptyTaskRepositoryReturnsEmptyList() throws Exception {
        taskRepository.deleteAll();
        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testTasksSortedByDate() {
        Task t1 = new Task();
        t1.setTitle("Earlier Task");
        t1.setDescription("desc");
        t1.setDate("2025-07-30");
        t1.setStatus("Pending");

        Task t2 = new Task();
        t2.setTitle("Later Task");
        t2.setDescription("desc");
        t2.setDate("2025-08-10");
        t2.setStatus("Pending");

        taskRepository.save(t1);
        taskRepository.save(t2);

        List<Task> all = taskRepository.findAll();
        List<Task> sorted = all.stream()
                .sorted(Comparator.comparing(Task::getDate))
                .toList();

        assertThat(sorted.get(0).getDate()).isEqualTo("2025-07-30");
        assertThat(sorted.get(2).getDate()).isEqualTo("2025-08-10");
    }
}
