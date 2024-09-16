package com.metaverse.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class AccessControlPermissionListException extends RuntimeException {

    private final List<String> permissions;

    public AccessControlPermissionListException(List<String> permissions) {
        super("The following resources cannot be accessed: " + permissions);
        this.permissions = permissions;
    }

}