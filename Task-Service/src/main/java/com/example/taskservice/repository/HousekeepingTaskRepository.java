package com.example.taskservice.repository;

import com.example.taskservice.enums.TaskStatus;
import com.example.taskservice.pojos.HousekeepingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {
    List<HousekeepingTask> findByStatus(TaskStatus status);
    List<HousekeepingTask> findByAssignedTo(Long assignedTo);

}