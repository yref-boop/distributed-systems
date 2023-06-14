package es.udc.ws.app.model.util;

import es.udc.ws.util.exceptions.InputValidationException;

import java.time.LocalDateTime;

public final class OtherPropertiesValidator {
    private OtherPropertiesValidator(){
    }

    public static void validateLocalDateTime(String propertyName, LocalDateTime value) throws InputValidationException {
        if(value == null){
            throw new InputValidationException("Invalid " + propertyName + " value (it cannot be null)");
        }
    }

    public static void validateBoolean(String propertyName, Boolean value) throws InputValidationException {
        if(value == null){
            throw new InputValidationException("Invalid " + propertyName + " value (it cannot be null)");
        }
    }

    public static void validateEmail(String email) throws InputValidationException {
        if (!(email.contains("@")))
            throw new InputValidationException("Invalid email value");
    }

}
