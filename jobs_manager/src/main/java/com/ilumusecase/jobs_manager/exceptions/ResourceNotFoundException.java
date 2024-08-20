package com.ilumusecase.jobs_manager.exceptions;

public class ResourceNotFoundException extends ResponseException {


    private String resource;
    private String id;

    public ResourceNotFoundException(String resource, String id){
        this.resource = resource;
        this.id = id;
    }

    @Override
    public String getMessage() {
        if(resource == null) return "Resourse was not found";
        if(id == null) return resource + " was not found";
        return resource + " with id " + id + " was not found";
    }
    
}
