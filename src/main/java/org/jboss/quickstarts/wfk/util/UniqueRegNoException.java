package org.jboss.quickstarts.wfk.util;

import javax.validation.ValidationException;

/**
 * <p>ValidationException caused if a Taxi's reg number conflicts with that of another taxi.</p>
 *
 * <p>This violates the uniqueness constraint.</p>
 *
 */
public class UniqueRegNoException extends ValidationException {

    public UniqueRegNoException(String message) {
        super(message);
    }

    public UniqueRegNoException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueRegNoException(Throwable cause) {
        super(cause);
    }
}
