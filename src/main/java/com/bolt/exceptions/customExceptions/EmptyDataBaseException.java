package com.bolt.exceptions.customExceptions;

import com.bolt.exceptions.MessageException;

public class EmptyDataBaseException extends RuntimeException{
    public EmptyDataBaseException() {
        super(MessageException.EMPTY_DATABASE);
    }

    public EmptyDataBaseException(String message) {
        super(message);
    }
}
