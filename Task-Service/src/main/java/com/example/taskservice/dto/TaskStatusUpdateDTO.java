package com.example.taskservice.dto;

import com.example.taskservice.enums.TaskStatus;
import lombok.Data;

@Data
public class TaskStatusUpdateDTO {
    private TaskStatus status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String notes;
}