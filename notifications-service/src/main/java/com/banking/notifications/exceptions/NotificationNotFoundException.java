package com.banking.notifications.exceptions;

public class NotificationNotFoundException extends RuntimeException{

    public NotificationNotFoundException(String message){
        super(message);
    }
}
