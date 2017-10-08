package com.wpengine.merger.exceptions;

import java.io.IOException;

public class UnknownException extends RuntimeException {
    public UnknownException(String msg, IOException e) {
        super(msg, e);
    }

    public UnknownException(String msg, Throwable t) {
        super(msg, t);
    }
}
