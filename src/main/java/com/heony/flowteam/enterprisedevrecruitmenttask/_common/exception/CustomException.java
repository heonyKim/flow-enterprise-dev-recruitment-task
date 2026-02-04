package com.heony.flowteam.enterprisedevrecruitmenttask._common.exception;


import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{
    private final int httpStatusCode;
    private final String name;
    private final String message;

    public CustomException(HttpStatus httpStatus){
        super(httpStatus.getReasonPhrase());
        this.httpStatusCode = httpStatus.value();
        this.name = httpStatus.name();
        this.message = httpStatus.getReasonPhrase();
    }

    public CustomException(HttpStatus httpStatus, String message){
        super(httpStatus.getReasonPhrase());
        this.httpStatusCode = httpStatus.value();
        this.name = httpStatus.name();
        this.message = message;
    }


    public CustomException(ErrorCode errorCode){
        super(errorCode.message());
        this.httpStatusCode = errorCode.httpStatusCode();
        this.name = errorCode.name();
        this.message = errorCode.message();
    }

    public CustomException(ErrorCode errorCode, String message){
        super(errorCode.message());
        this.httpStatusCode = errorCode.httpStatusCode();
        this.name = errorCode.name();
        this.message = message;
    }

    public CustomException(ErrorCode errorCode, boolean customWithParentheses, String message){
        super(customWithParentheses ? String.format("%s (%s)", errorCode.message(), message) : errorCode.message());
        this.httpStatusCode = errorCode.httpStatusCode();
        this.name = errorCode.name();
        this.message = customWithParentheses ? String.format("%s (%s)", errorCode.message(), message) : errorCode.message();

    }

    public CustomException(int httpStatusCode, String name, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.name = name;
        this.message = message;
    }

    public int httpStatusCode() {
        return httpStatusCode;
    }

    public String name() {
        return name;
    }
    public String message() {
        return message;
    }

}
