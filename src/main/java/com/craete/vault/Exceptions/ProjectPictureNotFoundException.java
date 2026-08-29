package com.craete.vault.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProjectPictureNotFoundException extends RuntimeException {

    public ProjectPictureNotFoundException(String message) {
        super(message);
    }

    public ProjectPictureNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
