package org.pkwmtt.exceptions;

import org.springframework.data.crossstore.ChangeSetPersister;

public class OTPCodeNotFoundException
  extends IllegalArgumentException {
    public OTPCodeNotFoundException () {
        super("Provided isn't assigned to any group.");
    }
}
