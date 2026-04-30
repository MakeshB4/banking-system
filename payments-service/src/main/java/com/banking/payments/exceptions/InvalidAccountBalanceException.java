package com.banking.payments.exceptions;

public class InvalidAccountBalanceException extends RuntimeException {
    public InvalidAccountBalanceException(String message) {
        super(message);
    }
}
