package com.revature.LoggingFun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoLogbackSetup {

    private static final Logger log  = LoggerFactory.getLogger("com.revature.LoggingFun.DemoLogbackSetup");

    public static void main(String[] args) {
        log.trace("trace (often below root threshold)");
        log.debug("debug: configuration loaded");
        log.info("info: demo main started");
        log.warn("warn: example");
        log.error("error: example error without exception");
        System.out.println("\n check logs/demo-logback.log relative to process working directory");
    }

}
