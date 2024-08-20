package com.ilumusecase.jobs_manager.exceptions;

public class WrongResourcesInheritanceInUrlException extends ResponseException{


    private String resource1;
    private String resource2;

    public WrongResourcesInheritanceInUrlException(String resource1, String resource2){
        this.resource1 = resource1;
        this.resource2 = resource2;
    }

    @Override
    public String getMessage() {
        if(resource1 == null || resource2 == null) return "Wrong resource inheritance in provided url";
        return "Wrong resource inheritance in provided url: " +resource2 + " does not belong to "  + resource1;
    }
    
}
