package com.backend.roomie.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public ResponseEntity<ApiError> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        String message = (String) request.getAttribute("javax.servlet.error.message");
        
        if (message == null || message.isEmpty()) {
            message = "An error occurred";
        }
        
        HttpStatus status = HttpStatus.valueOf(statusCode != null ? statusCode : 500);
        
        ApiError apiError = new ApiError(
                status,
                message,
                exception != null ? exception.getMessage() : "No additional details available"
        );
        
        return new ResponseEntity<>(apiError, status);
    }
}