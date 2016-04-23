package org.bloostatics.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by stdima on 23.04.16.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Analysis not found")
public class NoSuchAnalysisException extends RuntimeException {
    public NoSuchAnalysisException() {
        super();
    }
}
