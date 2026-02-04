package com.heony.flowteam.enterprisedevrecruitmenttask._common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionDto> handleCustomExceptions(CustomException e) {
        return ResponseEntity.status(e.httpStatusCode())
                .body(
                        new ExceptionDto(
                                HttpStatus.valueOf(e.httpStatusCode()).name(),
                                e.getMessage()
                        )
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ExceptionDto(
                                HttpStatus.BAD_REQUEST.name(),
                                e.getMessage()
                        )
                );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionDto> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ExceptionDto(
                                HttpStatus.CONFLICT.name(),
                                e.getMessage()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleValidationExceptions(MethodArgumentNotValidException e) {
        ExceptionDto exceptionDto = e.getBindingResult().getFieldErrors().stream().findFirst().map(error ->
                new ExceptionDto(
                        HttpStatus.BAD_REQUEST.name(),
                        error.getDefaultMessage()
                )
        ).orElse(
                new ExceptionDto(
                        HttpStatus.BAD_REQUEST.name(),
                        ErrorCode.INVALID_REQUEST.message()
                )
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionDto> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.error("MaxUploadSizeExceededException", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ExceptionDto(
                                HttpStatus.BAD_REQUEST.name(),
                                e.getMessage()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleAll(Exception e) {
        log.error("Exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ExceptionDto(
                                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                                e.getMessage()
                        )
                );
    }
}
