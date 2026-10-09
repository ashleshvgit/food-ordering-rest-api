package com.ashlesh.mycoolproject.exception;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found with id " + id);
    }
}
