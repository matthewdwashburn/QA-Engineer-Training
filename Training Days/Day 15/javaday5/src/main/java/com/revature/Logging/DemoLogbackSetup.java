package com.revature.Logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoLogbackSetup {
    private static final Logger log = LoggerFactory.getLogger("com.revature.Logging.DemoLogbackSetup");
    
    public static void main(String[] args) {
        log.trace("TRACE: Often below root threshold");
        log.debug("DEBUG: Configuration loaded");
        log.info("INFO: Demo main started");
        log.warn("WARN: Something unexpected occured");
        log.error("ERROR: Example error without exception");
        System.out.println("\n Check logs/demo-logback.log relative to process working directory");
    }
}
