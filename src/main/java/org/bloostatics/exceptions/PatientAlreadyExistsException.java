package org.bloostatics.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by stdima on 03.04.16.
 */
    @ResponseStatus(value= HttpStatus.CONFLICT, reason="Patient with same email already exists")
public class PatientAlreadyExistsException extends RuntimeException {

    public PatientAlreadyExistsException() {
        super();
    }
}
