// src/main/java/com/example/taskservice/repository/HousekeepingTaskRepository.java
package com.example.taskservice.repository;

import com.example.taskservice.enums.TaskStatus;
import com.example.taskservice.pojos.HousekeepingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {

    List<HousekeepingTask> findByAssignedTo(Long staffId);

    List<HousekeepingTask> findByAssignedToAndStatus(Long staffId, TaskStatus status);

    List<HousekeepingTask> findByStatus(TaskStatus status);

    List<HousekeepingTask> findByRoomId(Long roomId);

    List<HousekeepingTask> findByPriority(String priority);

    long countByStatus(TaskStatus status);

    long countByAssignedToAndStatus(Long staffId, TaskStatus status);
}