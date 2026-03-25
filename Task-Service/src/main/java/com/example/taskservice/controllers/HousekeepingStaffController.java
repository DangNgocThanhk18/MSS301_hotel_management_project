package com.example.taskservice.controllers;

import com.example.taskservice.dto.TaskStatusUpdateDTO;
import com.example.taskservice.enums.TaskStatus;
import com.example.taskservice.pojos.HousekeepingTask;
import com.example.taskservice.repository.HousekeepingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks/housekeeping/staff")
@RequiredArgsConstructor
public class HousekeepingStaffController {

    private final HousekeepingTaskRepository housekeepingTaskRepository;

    // Lấy tasks được giao cho staff
    @GetMapping("/{staffId}")
    public ResponseEntity<List<HousekeepingTask>> getAssignedTasks(@PathVariable Long staffId) {
        List<HousekeepingTask> tasks = housekeepingTaskRepository.findByAssignedTo(staffId);
        return ResponseEntity.ok(tasks);
    }

    // Lấy tasks theo status cho staff cụ thể
    @GetMapping("/{staffId}/status/{status}")
    public ResponseEntity<List<HousekeepingTask>> getAssignedTasksByStatus(
            @PathVariable Long staffId,
            @PathVariable TaskStatus status) {
        List<HousekeepingTask> tasks = housekeepingTaskRepository.findByAssignedToAndStatus(staffId, status);
        return ResponseEntity.ok(tasks);
    }

    // Cập nhật task status và log
    @PutMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatusAndLog(
            @PathVariable Long taskId,
            @RequestBody TaskStatusUpdateDTO updateDTO) {

        try {
            HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy task với ID: " + taskId));

            if (updateDTO.getStatus() != null) {
                task.setStatus(updateDTO.getStatus());
                task.setUpdatedAt(LocalDateTime.now());

                // Nếu task hoàn thành, ghi nhận thời gian hoàn thành
                if (updateDTO.getStatus() == TaskStatus.COMPLETED) {
                    task.setCompletedAt(LocalDateTime.now());
                }
            }

            if (updateDTO.getNotes() != null && !updateDTO.getNotes().trim().isEmpty()) {
                String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                String existingNotes = task.getNotes() == null ? "" : task.getNotes() + "\n";

                task.setNotes(existingNotes + "[" + timeStamp + "] " + updateDTO.getNotes());
            }

            HousekeepingTask updatedTask = housekeepingTaskRepository.save(task);

            return ResponseEntity.ok(Map.of(
                    "message", "Task updated successfully",
                    "task", updatedTask
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update task: " + e.getMessage()));
        }
    }

    // Nhận task (assign to staff)
    @PutMapping("/{taskId}/assign")
    public ResponseEntity<?> assignTask(
            @PathVariable Long taskId,
            @RequestParam Long staffId) {
        try {
            HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy task với ID: " + taskId));

            task.setAssignedTo(staffId);
            task.setStatus(TaskStatus.ASSIGNED);
            task.setUpdatedAt(LocalDateTime.now());

            HousekeepingTask updatedTask = housekeepingTaskRepository.save(task);

            return ResponseEntity.ok(Map.of(
                    "message", "Task assigned successfully",
                    "task", updatedTask
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to assign task: " + e.getMessage()));
        }
    }

    // Bắt đầu làm task
    @PutMapping("/{taskId}/start")
    public ResponseEntity<?> startTask(@PathVariable Long taskId) {
        try {
            HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy task với ID: " + taskId));

            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setStartedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());

            HousekeepingTask updatedTask = housekeepingTaskRepository.save(task);

            return ResponseEntity.ok(Map.of(
                    "message", "Task started",
                    "task", updatedTask
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to start task: " + e.getMessage()));
        }
    }

    // Hoàn thành task
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Long taskId) {
        try {
            HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy task với ID: " + taskId));

            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());

            HousekeepingTask updatedTask = housekeepingTaskRepository.save(task);

            return ResponseEntity.ok(Map.of(
                    "message", "Task completed",
                    "task", updatedTask
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to complete task: " + e.getMessage()));
        }
    }

    // Hủy task
    @PutMapping("/{taskId}/cancel")
    public ResponseEntity<?> cancelTask(@PathVariable Long taskId, @RequestBody(required = false) Map<String, String> request) {
        try {
            HousekeepingTask task = housekeepingTaskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy task với ID: " + taskId));

            task.setStatus(TaskStatus.CANCELLED);
            task.setUpdatedAt(LocalDateTime.now());

            String reason = request != null ? request.get("reason") : null;
            if (reason != null) {
                String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                String existingNotes = task.getNotes() == null ? "" : task.getNotes() + "\n";
                task.setNotes(existingNotes + "[" + timeStamp + "] [CANCELLED] " + reason);
            }

            HousekeepingTask updatedTask = housekeepingTaskRepository.save(task);

            return ResponseEntity.ok(Map.of(
                    "message", "Task cancelled",
                    "task", updatedTask
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to cancel task: " + e.getMessage()));
        }
    }

    // Lấy thống kê cho staff
    @GetMapping("/{staffId}/statistics")
    public ResponseEntity<?> getStaffStatistics(@PathVariable Long staffId) {
        try {
            long pendingTasks = housekeepingTaskRepository.countByAssignedToAndStatus(staffId, TaskStatus.PENDING);
            long assignedTasks = housekeepingTaskRepository.countByAssignedToAndStatus(staffId, TaskStatus.ASSIGNED);
            long inProgressTasks = housekeepingTaskRepository.countByAssignedToAndStatus(staffId, TaskStatus.IN_PROGRESS);
            long completedTasks = housekeepingTaskRepository.countByAssignedToAndStatus(staffId, TaskStatus.COMPLETED);

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("staffId", staffId);
            statistics.put("pending", pendingTasks);
            statistics.put("assigned", assignedTasks);
            statistics.put("inProgress", inProgressTasks);
            statistics.put("completed", completedTasks);

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to get statistics: " + e.getMessage()));
        }
    }
}