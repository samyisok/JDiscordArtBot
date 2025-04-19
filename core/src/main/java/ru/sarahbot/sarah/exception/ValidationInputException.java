package ru.sarahbot.sarah.exception;

public class ValidationInputException extends RuntimeException {
    public ValidationInputException(String message) {
        super(message);
    }
}
