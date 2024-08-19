package com.ilumusecase.jobs_manager.exceptions.security;

import com.ilumusecase.jobs_manager.exceptions.ResponseException;

public class NotAuthorizedException extends  ResponseException{

    @Override
    public String getMessage() {
        return "Exception: You are not authorized to perform the action";
    }
    
}
