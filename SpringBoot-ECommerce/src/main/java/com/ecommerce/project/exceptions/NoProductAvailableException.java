package com.ecommerce.project.exceptions;

public class NoProductAvailableException extends RuntimeException{

    public NoProductAvailableException(String message){
        super(message);
    }
}
