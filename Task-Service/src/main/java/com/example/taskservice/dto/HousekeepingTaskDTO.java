// src/main/java/com/example/taskservice/dto/HousekeepingTaskDTO.java
package com.example.taskservice.dto;

import com.example.taskservice.enums.TaskStatus;
import lombok.Data;

@Data
public class HousekeepingTaskDTO {
    private Long roomId;
    private String roomNumber;
    private Long assignedTo;
    private Long reservationId;
    private String type;
    private String priority;
    private TaskStatus status;
    private String notes;
}