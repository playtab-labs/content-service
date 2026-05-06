package com.playtab.contentservice.exception;

import io.grpc.Status;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CONTENT_NOT_FOUND(Status.NOT_FOUND, "Content not found"),
    NOTICE_NOT_FOUND(Status.NOT_FOUND, "Notice not found"),
    MD_ITEM_NOT_FOUND(Status.NOT_FOUND, "MD item not found"),
    PUB_NOT_FOUND(Status.NOT_FOUND, "Pub not found"),
    FOOD_TRUCK_NOT_FOUND(Status.NOT_FOUND, "Food truck not found"),

    INVALID_LOCALE(Status.INVALID_ARGUMENT, "Invalid locale"),
    INTERNAL_ERROR(Status.INTERNAL, "Internal server error"),
    ADMIN_REQUIRED(Status.PERMISSION_DENIED, "Admin role required"),
    INVALID_ARGUMENT(Status.INVALID_ARGUMENT, "Invalid argument");

    private final Status status;
    private final String message;
}