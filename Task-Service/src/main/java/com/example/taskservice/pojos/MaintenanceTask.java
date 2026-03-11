package com.example.taskservice.pojos;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "maintenance_task")
@Data
public class MaintenanceTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;

    private String description;

    private Long assignedTo;

    private String status;
}

