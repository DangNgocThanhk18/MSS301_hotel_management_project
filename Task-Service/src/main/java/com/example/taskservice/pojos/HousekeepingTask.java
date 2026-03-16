package com.example.taskservice.pojos;

import com.example.taskservice.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "housekeeping_task")
@Data
public class HousekeepingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;

    private Long assignedTo;

    private LocalDate taskDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;
}