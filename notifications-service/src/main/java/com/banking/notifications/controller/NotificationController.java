package com.banking.notifications.controller;

import com.banking.notifications.dto.ApiResponse;
import com.banking.notifications.dto.NotificationDTO;
import com.banking.notifications.dto.NotificationResponseDTO;
import com.banking.notifications.service.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final INotificationService notificationService;

    
    @Operation(
            summary = "Send a notification",
            description = "Send an Email or SMS notification to a user. The notification is saved to the database and sent asynchronously."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Notification sent successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - validation error",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> sendNotification(
            @Valid @RequestBody NotificationDTO notificationDTO) {

        log.info("POST /api/notifications/send - Sending notification");

        NotificationResponseDTO response = notificationService.sendNotification(notificationDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification sent successfully", response));
    }


    @Operation(
            summary = "Get unsent notifications by user ID",
            description = "Retrieve all unsent notifications for a specific user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved unsent notifications",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })

    
    @GetMapping("/getUnsendNotificationById/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getUnsendNotification(
            @PathVariable Long userId) {

        log.info("GET /api/notifications/getUnsendNotificationById/{} - Fetching unsent notifications", userId);

        List<NotificationResponseDTO> notifications = notificationService.getUnsentNotificationsByUser(userId);

      
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Retrieved " + notifications.size() + " unsent notification(s)",
                        notifications
                ));
    }

}