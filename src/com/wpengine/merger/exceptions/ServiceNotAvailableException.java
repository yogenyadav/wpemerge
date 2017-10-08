package com.wpengine.merger.exceptions;

import com.github.rholder.retry.RetryException;

public class ServiceNotAvailableException extends RuntimeException {
    public ServiceNotAvailableException(String msg, RetryException e) {
        super(msg, e);
    }
}
