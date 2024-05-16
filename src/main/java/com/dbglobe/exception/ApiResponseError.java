package com.dbglobe.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApiResponseError {
    private HttpStatus status;

    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    private String message;

    private String requestURI;


    private ApiResponseError() {
        timestamp=LocalDateTime.now();
    }


    public ApiResponseError(HttpStatus status) {
        this();
        this.message="Unexpected Error";
        this.status=status;
    }

    public ApiResponseError(HttpStatus status, String message, String requestURI) {
        this(status);
        this.message=message;
        this.requestURI=requestURI;
    }


}

