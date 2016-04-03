package org.bloostatics.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by stdima on 03.04.16.
 */
@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Email already exists")
public class EmailAlreadyExistsException extends RuntimeException
{
    public EmailAlreadyExistsException() {
        super();
    }
}
