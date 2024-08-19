package com.ilumusecase.jobs_manager.exceptions;

public class GeneralResponseException extends ResponseException{

    private String message = "";

    public GeneralResponseException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
    
}
