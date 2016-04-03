package org.bloostatics.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by stdima on 03.04.16.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Doctor Not Found")
public class NoSuchDoctorException extends RuntimeException{
    public NoSuchDoctorException() {
        super();
    }
}
