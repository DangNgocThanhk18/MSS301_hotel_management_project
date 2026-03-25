package com.example.taskservice.controllers;

import com.example.taskservice.dto.HousekeepingTaskDTO;
import com.example.taskservice.dto.TaskStatusUpdateDTO;
import com.example.taskservice.enums.TaskStatus;
import com.example.taskservice.pojos.HousekeepingTask;
import com.example.taskservice.repository.HousekeepingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final HousekeepingTaskRepository housekeepingTaskRepository;

    // Lấy tất cả housekeeping tasks
    @GetMapping("/housekeeping")
    public ResponseEntity<List<HousekeepingTask>> getAllTasks() {
        return ResponseEntity.ok(housekeepingTaskRepository.findAll());
    }

    // Lấy tasks theo status
    @GetMapping("/housekeeping/status/{status}")
    public ResponseEntity<List<HousekeepingTask>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<HousekeepingTask> tasks = housekeepingTaskRepository.findByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    // Lấy tasks theo room
    @GetMapping("/housekeeping/room/{roomId}")
    public ResponseEntity<List<HousekeepingTask>> getTasksByRoom(@PathVariable Long roomId) {
        List<HousekeepingTask> tasks = housekeepingTaskRepository.findByRoomId(roomId);
        return ResponseEntity.ok(tasks);
    }

    // Lấy tasks theo priority
    @GetMapping("/housekeeping/priority/{priority}")
    public ResponseEntity<List<HousekeepingTask>> getTasksByPriority(@PathVariable String priority) {
        List<HousekeepingTask> tasks = housekeepingTaskRepository.findByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    // Tạo task mới (cập nhật để nhận DTO)
    @PostMapping("/housekeeping")
    public ResponseEntity<?> createTask(@RequestBody HousekeepingTaskDTO taskDTO) {
        try {
            HousekeepingTask task = new HousekeepingTask();
            task.setRoomId(taskDTO.getRoomId());
            task.setRoomNumber(taskDTO.getRoomNumber());
            task.setAssignedTo(taskDTO.getAssignedTo());
            task.setReservationId(taskDTO.getReservationId());
            task.setType(taskDTO.getType());
            task.setPriority(taskDTO.getPriority());
            task.setStatus(taskDTO.getStatus() != null ? taskDTO.getStatus() : TaskStatus.PENDING);
            task.setNotes(taskDTO.getNotes());
            task.setCreatedAt(LocalDateTime.now());

            HousekeepingTask savedTask = housekeepingTaskRepository.save(task);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Task created successfully");
            response.put("task", savedTask);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create task: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Cập nhật task
    @PutMapping("/housekeeping/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody HousekeepingTaskDTO taskDTO) {
        try {
            HousekeepingTask existingTask = housekeepingTaskRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

            if (taskDTO.getRoomId() != null) existingTask.setRoomId(taskDTO.getRoomId());
            if (taskDTO.getRoomNumber() != null) existingTask.setRoomNumber(taskDTO.getRoomNumber());
            if (taskDTO.getAssignedTo() != null) existingTask.setAssignedTo(taskDTO.getAssignedTo());
            if (taskDTO.getType() != null) existingTask.setType(taskDTO.getType());
            if (taskDTO.getPriority() != null) existingTask.setPriority(taskDTO.getPriority());
            if (taskDTO.getStatus() != null) existingTask.setStatus(taskDTO.getStatus());
            if (taskDTO.getNotes() != null) existingTask.setNotes(taskDTO.getNotes());

            existingTask.setUpdatedAt(LocalDateTime.now());

            HousekeepingTask updatedTask = housekeepingTaskRepository.save(existingTask);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update task: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Xóa task
    @DeleteMapping("/housekeeping/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            if (!housekeepingTaskRepository.existsById(id)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Task not found"));
            }
            housekeepingTaskRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete task: " + e.getMessage()));
        }
    }

    // Tạo task dọn phòng từ reservation
    @PostMapping("/create-cleaning-task")
    public ResponseEntity<?> createCleaningTask(@RequestParam("reservationId") Long reservationId,
                                                @RequestParam(value = "roomId", required = false) Long roomId,
                                                @RequestParam(value = "roomNumber", required = false) String roomNumber) {
        try {
            HousekeepingTask task = new HousekeepingTask();
            task.setReservationId(reservationId);
            task.setRoomId(roomId);
            task.setRoomNumber(roomNumber);
            task.setStatus(TaskStatus.PENDING);
            task.setType("Dọn phòng sau Check-out");
            task.setPriority("HIGH");
            task.setCreatedAt(LocalDateTime.now());
            task.setNotes("Hệ thống tạo tự động: Cần dọn phòng sau khi khách Check-out");

            HousekeepingTask savedTask = housekeepingTaskRepository.save(task);

            return ResponseEntity.ok(Map.of(
                    "message", "Đã nhận yêu cầu và tạo Task dọn phòng thành công!",
                    "taskId", savedTask.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create cleaning task: " + e.getMessage()));
        }
    }

    // Thống kê tasks
    @GetMapping("/housekeeping/statistics")
    public ResponseEntity<?> getTaskStatistics() {
        try {
            long totalTasks = housekeepingTaskRepository.count();
            long pendingTasks = housekeepingTaskRepository.countByStatus(TaskStatus.PENDING);
            long inProgressTasks = housekeepingTaskRepository.countByStatus(TaskStatus.IN_PROGRESS);
            long completedTasks = housekeepingTaskRepository.countByStatus(TaskStatus.COMPLETED);
            long cancelledTasks = housekeepingTaskRepository.countByStatus(TaskStatus.CANCELLED);

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("total", totalTasks);
            statistics.put("pending", pendingTasks);
            statistics.put("inProgress", inProgressTasks);
            statistics.put("completed", completedTasks);
            statistics.put("cancelled", cancelledTasks);

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to get statistics: " + e.getMessage()));
        }
    }

    // Bulk create tasks (tạo nhiều task cùng lúc)
    @PostMapping("/housekeeping/bulk")
    public ResponseEntity<?> createBulkTasks(@RequestBody List<HousekeepingTaskDTO> taskDTOs) {
        try {
            List<HousekeepingTask> tasks = taskDTOs.stream().map(dto -> {
                HousekeepingTask task = new HousekeepingTask();
                task.setRoomId(dto.getRoomId());
                task.setRoomNumber(dto.getRoomNumber());
                task.setAssignedTo(dto.getAssignedTo());
                task.setReservationId(dto.getReservationId());
                task.setType(dto.getType());
                task.setPriority(dto.getPriority());
                task.setStatus(TaskStatus.PENDING);
                task.setNotes(dto.getNotes());
                task.setCreatedAt(LocalDateTime.now());
                return task;
            }).toList();

            List<HousekeepingTask> savedTasks = housekeepingTaskRepository.saveAll(tasks);
            return ResponseEntity.ok(Map.of(
                    "message", "Created " + savedTasks.size() + " tasks successfully",
                    "tasks", savedTasks
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create tasks: " + e.getMessage()));
        }
    }
}