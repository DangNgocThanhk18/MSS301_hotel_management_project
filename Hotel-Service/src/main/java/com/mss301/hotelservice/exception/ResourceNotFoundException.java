// src/main/java/com/mss301/hotelservice/exception/ResourceNotFoundException.java
package com.mss301.hotelservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}