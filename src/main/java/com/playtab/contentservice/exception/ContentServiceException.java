package com.playtab.contentservice.exception;

import lombok.Getter;

@Getter
public class ContentServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public ContentServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}