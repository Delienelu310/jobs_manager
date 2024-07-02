package com.ilumusecase.data_supplier;

public class InvalidPathException extends RuntimeException{
    public InvalidPathException(String path){
        super(path);
    }   
}
