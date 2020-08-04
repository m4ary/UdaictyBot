package com.mshlab.udacityBot.udacityapi;


import com.mshlab.udacityBot.udacityapi.model.ErrorResponse;

public class UdacityException extends Exception {

    private final int errorCode;
    private final String message;

    public UdacityException(String message) {
        this.errorCode = 0;
        this.message = message;
    }

    public UdacityException(int errorCode, ErrorResponse errorResponse) {
        this.errorCode = errorCode;
        this.message = errorResponse.getError();
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return "API Error (" + errorCode + ")- " + message + "\n" +
                "please /reset the bot if an error Keep appearing";
    }

    public interface UdacityExceptionHandler {
        void handleStatusCode(int statusCode) throws UdacityException;
    }
}
