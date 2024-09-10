package com.metaverse.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InvalidStrReqListException extends RuntimeException {

    private final List<String> invalidLocations;

    public InvalidStrReqListException(List<String> invalidLocations) {
        super("The following server locations are invalid: " + invalidLocations);
        this.invalidLocations = invalidLocations;
    }

}