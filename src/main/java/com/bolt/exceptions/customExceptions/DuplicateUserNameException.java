package com.bolt.exceptions.customExceptions;

public class DuplicateUserNameException extends RuntimeException{
    public DuplicateUserNameException() {
        super("Duplicated Username");
    }

    public DuplicateUserNameException(String message) {
        super(message);
    }
}
