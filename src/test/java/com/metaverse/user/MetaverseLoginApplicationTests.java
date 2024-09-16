package com.metaverse.user;

import com.metaverse.common.permission.AnotherService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.security.SecureRandom;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class MetaverseLoginApplicationTests {

    //    @Autowired
    private final AnotherService anotherService;

    @Test
    void contextLoads() {
        anotherService.callYourMethod();

        BigInteger bigInteger = BigInteger.probablePrime(2, new SecureRandom());
        System.out.println(bigInteger);

    }

}
