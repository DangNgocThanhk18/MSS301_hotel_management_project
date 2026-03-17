package com.example.taskservice.controllers;

import com.example.taskservice.dto.TaskStatusUpdateDTO;
import com.example.taskservice.pojos.HousekeepingTask;
import com.example.taskservice.repository.HousekeepingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/tasks/housekeeping/staff")
@RequiredArgsConstructor
public class HousekeepingStaffController {

    private final HousekeepingTaskRepository housekeepingTaskRepository;

    @GetMapping("/{staffId}")
    public ResponseEntity<List<HousekeepingTask>> getAssignedTasks(@PathVariable Long staffId) {
        List<HousekeepingTask> tasks = housekeepingTaskRepository.findByAssignedTo(staffId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatusAndLog(
            @PathVariable Long taskId,
            @RequestBody TaskStatusUpdateDTO updateDTO) {

        HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy task với ID: " + taskId));

        if (updateDTO.getStatus() != null) {
            task.setStatus(updateDTO.getStatus());
        }

        if (updateDTO.getNotes() != null && !updateDTO.getNotes().trim().isEmpty()) {
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String existingNotes = task.getNotes() == null ? "" : task.getNotes() + "\n";

            task.setNotes(existingNotes + "[" + timeStamp + "] " + updateDTO.getNotes());
        }

        HousekeepingTask updatedTask = housekeepingTaskRepository.save(task);

        return ResponseEntity.ok(updatedTask);
    }
}