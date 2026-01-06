package com.SecureChatApp.Controller;

import com.SecureChatApp.Dto.Response.ApiResponse;
import com.SecureChatApp.Model.Message;
import com.SecureChatApp.Security.UserPrincipal;
import com.SecureChatApp.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /* ----------------------------------------
     * GET /api/messages/{id}
     * ---------------------------------------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConversation(
            @PathVariable("id") Long otherUserId,
            @RequestParam(value = "limit", defaultValue = "50") int limit,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }

        if (limit <= 0 || limit > 100) {
            limit = 50;
        }

        List<Message> messages =
                messageService.getConversation(
                        principal.getUserId(),
                        otherUserId,
                        limit
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "ok",
                        Map.of("messages", messages)
                )
        );
    }
}
