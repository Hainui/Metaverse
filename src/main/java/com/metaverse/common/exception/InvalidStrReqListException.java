package com.metaverse.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InvalidStrReqListException extends RuntimeException {

    private final List<String> invalidLocations;

    public InvalidStrReqListException(List<String> invalidReqList) {
        super("The following request parameters are abnormal: " + invalidReqList);
        this.invalidLocations = invalidReqList;
    }

}