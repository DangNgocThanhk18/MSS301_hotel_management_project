package com.example.taskservice.controllers;

import com.example.taskservice.enums.TaskStatus;
import com.example.taskservice.pojos.HousekeepingTask;
import com.example.taskservice.repository.HousekeepingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final HousekeepingTaskRepository housekeepingTaskRepository;

    @GetMapping("/housekeeping")
    public ResponseEntity<List<HousekeepingTask>> getAllTasks() {
        return ResponseEntity.ok(housekeepingTaskRepository.findAll());
    }

    @PostMapping("/housekeeping")
    public ResponseEntity<HousekeepingTask> createManualTask(@RequestBody HousekeepingTask task) {
        task.setCreatedAt(LocalDateTime.now());
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        return ResponseEntity.ok(housekeepingTaskRepository.save(task));
    }

    @DeleteMapping("/housekeeping/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        housekeepingTaskRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-cleaning-task")
    public ResponseEntity<?> createCleaningTask(@RequestParam("reservationId") Long reservationId) {
        HousekeepingTask task = new HousekeepingTask();
        task.setReservationId(reservationId);
        task.setStatus(TaskStatus.PENDING);
        task.setType("Dọn phòng sau Check-out");
        task.setPriority("HIGH");
        task.setCreatedAt(LocalDateTime.now());
        task.setNotes("Hệ thống tạo tự động: Cần dọn phòng sau khi khách Check-out");

        housekeepingTaskRepository.save(task);
        return ResponseEntity.ok("Đã nhận yêu cầu và tạo Task dọn phòng thành công!");
    }
}