package com.heony.flowteam.enterprisedevrecruitmenttask._common.exception;

public enum ErrorCode {

    ALREADY_BLOCKED_EXTENSION(400, "이미 차단된 확장자입니다."),
    EXCEEDED_MAX_CUSTOM_EXTENSION_COUNT(400, "커스텀 확장자의 최대 개수를 초과하였습니다."),
    CANNOT_BE_BLANK(400, "NULL이거나 빈값이 될 수 없습니다."),
    INVALID_REQUEST(400, "유효하지 않은 요청입니다."),


    NOT_FOUND(404, "찾을 수 없습니다."),

    ;

    private final int httpStatusCode;
    private final String message;

    ErrorCode(int httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public int httpStatusCode() {
        return httpStatusCode;
    }

    public String message() {
        return message;
    }


}
