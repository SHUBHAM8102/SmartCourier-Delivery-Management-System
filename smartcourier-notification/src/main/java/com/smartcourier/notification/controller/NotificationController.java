package com.smartcourier.notification.controller;

import com.smartcourier.notification.dto.EmailLogDto;
import com.smartcourier.notification.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailLogRepository emailLogRepository;

    @GetMapping("/logs")
    public ResponseEntity<Page<EmailLogDto>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EmailLogDto> logs = emailLogRepository.findAll(pageRequest)
                .map(EmailLogDto::fromEntity);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/{email}")
    public ResponseEntity<Page<EmailLogDto>> getLogsByEmail(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EmailLogDto> logs = emailLogRepository.findByRecipientEmailContainingIgnoreCase(email, pageRequest)
                .map(EmailLogDto::fromEntity);
        return ResponseEntity.ok(logs);
    }
}
