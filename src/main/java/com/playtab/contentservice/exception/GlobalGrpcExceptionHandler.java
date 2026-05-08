package com.playtab.contentservice.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

@Component
public class GlobalGrpcExceptionHandler {

    public StatusRuntimeException toStatusRuntimeException(Exception e) {
        if (e instanceof ContentServiceException ex) {
            return ex.getErrorCode().getStatus()
                    .withDescription(ex.getMessage())
                    .asRuntimeException();
        }

        return Status.INTERNAL
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}
