package com.autolift.sandbox.domain.exception;

public class SandboxNotFoundException extends RuntimeException {

    public SandboxNotFoundException(String message) {
        super(message);
    }

    public static SandboxNotFoundException withId(String id) {
        return new SandboxNotFoundException("Sandbox not found with id: " + id);
    }
}