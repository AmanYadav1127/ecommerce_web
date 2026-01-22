package com.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    String ResourceName;
    String fieldName;
    String field;
    Long fieldId;
    public ResourceNotFoundException(){
    }
    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldId) {
        super(String.format("%s not found with %s : %d",resourceName,fieldName,fieldId));
        this.ResourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldId = fieldId;
    }
    public ResourceNotFoundException(String resourceName, String fieldName, String field) {
        super(String.format("%s not found with %s : %s",resourceName,fieldName,field));
        this.ResourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
    }
}
