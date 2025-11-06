package com.example.orderservice.exceptions;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
	
	private HttpStatusCode status;
    private String message;
    private LocalDateTime timestamp;
    private List<String> errors;
    
    public ErrorResponse(HttpStatusCode status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

}
