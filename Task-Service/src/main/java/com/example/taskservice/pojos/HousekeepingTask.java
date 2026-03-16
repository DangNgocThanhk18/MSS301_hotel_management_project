package com.example.taskservice.pojos;

import com.example.taskservice.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "housekeeping_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HousekeepingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    private Long roomId;

    private Long assignedTo;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String notes;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}