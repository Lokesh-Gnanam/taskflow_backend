package com.examly.springapp.repository;

import com.examly.springapp.model.Task;
import com.examly.springapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByUserAndArchivedFalse(User user);
    List<Task> findByUserAndStatusAndArchivedFalse(User user, Task.Status status);
    List<Task> findByUserAndPriorityAndArchivedFalse(User user, Task.Priority priority);
    
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.deadlineDate = :deadlineDate AND t.archived = false")
    List<Task> findByUserAndDeadlineDateAndArchivedFalse(@Param("user") User user, @Param("deadlineDate") LocalDate deadlineDate);
    
    @Query("SELECT t FROM Task t WHERE t.user = :user AND (t.title LIKE CONCAT('%', :searchTerm, '%') OR t.description LIKE CONCAT('%', :searchTerm, '%')) AND t.archived = false")
    List<Task> findByUserAndTitleContainingOrDescriptionContainingAndArchivedFalse(
        @Param("user") User user, 
        @Param("searchTerm") String searchTerm);
    
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.deadlineDate BETWEEN :startDate AND :endDate AND t.archived = false")
    List<Task> findByUserAndDeadlineDateBetweenAndArchivedFalse(
        @Param("user") User user, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t FROM Task t WHERE t.user = :user AND YEAR(t.deadlineDate) = :year AND MONTH(t.deadlineDate) = :month AND t.archived = false")
    List<Task> findByUserAndMonthAndYear(@Param("user") User user, @Param("year") int year, @Param("month") int month);
}