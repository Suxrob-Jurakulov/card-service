package org.company.exp;

public class NotPermissionException extends RuntimeException{
    public NotPermissionException(String message) {
        super(message);
    }
}
