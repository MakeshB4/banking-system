package com.banking.useraccounts.exceptions;

public class DetailsNotFoundException extends RuntimeException{
    public DetailsNotFoundException(String message){
        super(message);
    }

}
