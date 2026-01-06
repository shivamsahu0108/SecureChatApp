package com.SecureChatApp.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ApiError {

    private boolean ok;
    private String message;
    private int status;
    private Instant timestamp;
}
