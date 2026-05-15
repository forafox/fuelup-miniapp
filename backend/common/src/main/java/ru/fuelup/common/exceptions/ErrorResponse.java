package ru.fuelup.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final ExceptionType type;
    private final String message;
    private final long timestamp;

    public static ErrorResponse of(ExceptionType type, String message) {
        return new ErrorResponse(type, message, System.currentTimeMillis());
    }
}
