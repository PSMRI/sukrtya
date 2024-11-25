package com.piramal.sukrtya.exceptions.handler;

public class ErrorResponse {
    private String error;


    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
