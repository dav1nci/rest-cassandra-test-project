package org.bloostatics.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by stdima on 03.04.16.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Device not found")
public class NoSuchDeviceException extends RuntimeException {
    public NoSuchDeviceException() {
        super();
    }
}
