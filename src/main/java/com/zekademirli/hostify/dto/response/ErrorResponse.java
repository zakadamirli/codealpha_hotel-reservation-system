package com.zekademirli.hostify.dto.response;



import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponse {

    private Integer status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(HttpStatus status, Throwable ex, HttpServletRequest request) {
        this.status = status.value();
        this.error = ex.getClass().getSimpleName();
        this.message = ex.getMessage();
        this.path = request.getRequestURI();
    }
}



