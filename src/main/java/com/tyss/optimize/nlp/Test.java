package com.tyss.optimize.nlp;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
@Slf4j
public class Test {

    public static void main(String[] args) throws InterruptedException {
        String date = Instant.now().toString();



        Instant oldDate = Instant.parse(date);

        Thread.sleep(3000);

        Instant newDate = Instant.now();

        log.info(String.valueOf(ChronoUnit.MILLIS.between(oldDate, newDate)));

    }
}
