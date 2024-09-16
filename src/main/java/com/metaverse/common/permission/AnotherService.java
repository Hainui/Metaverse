package com.metaverse.common.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnotherService {

    private final YourService yourService;

    public void callYourMethod() {
        yourService.someMethod();
    }
}