package com.example.taskservice.controllers;

import com.example.taskservice.enums.TaskStatus;
import com.example.taskservice.pojos.HousekeepingTask;
import com.example.taskservice.repository.HousekeepingTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private HousekeepingTaskRepository housekeepingTaskRepository;

    @PostMapping("/create-cleaning-task")
    public ResponseEntity<?> createCleaningTask(@RequestParam("reservationId") Long reservationId) {

        HousekeepingTask task = new HousekeepingTask();
        task.setReservationId(reservationId);
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(new Date());
        task.setNotes("Dọn phòng sau khi khách Check-out");

        housekeepingTaskRepository.save(task);

        return ResponseEntity.ok("Đã nhận yêu cầu và tạo Task dọn phòng thành công!");
    }

    // 2. DÀNH CHO NHÂN VIÊN DỌN PHÒNG: Lấy danh sách việc cần làm
    @GetMapping("/housekeeping/pending")
    public ResponseEntity<List<HousekeepingTask>> getPendingTasks() {
        List<HousekeepingTask> tasks = housekeepingTaskRepository.findByStatus(TaskStatus.PENDING);
        return ResponseEntity.ok(tasks);
    }

    // 3. DÀNH CHO NHÂN VIÊN DỌN PHÒNG: Bấm nút "Hoàn thành"
    @PutMapping("/housekeeping/{taskId}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Long taskId) {
        Optional<HousekeepingTask> taskOpt = housekeepingTaskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HousekeepingTask task = taskOpt.get();
        task.setStatus(TaskStatus.COMPLETED);
        task.setUpdatedAt(new Date());

        housekeepingTaskRepository.save(task);

        // TODO: (Tương lai) Gọi OpenFeign sang Room-Service để đổi trạng thái Phòng thành CLEAN (Sạch sẽ/Sẵn sàng đón khách mới)

        return ResponseEntity.ok("Đã xác nhận hoàn thành dọn phòng!");
    }
}