package org.pkwmtt.exceptions;

import com.mysql.cj.exceptions.WrongArgumentException;

public class IncorrectApiKeyValue extends WrongArgumentException {
    public IncorrectApiKeyValue () {
        super("API Key authentication unsuccessful");
    }
}
