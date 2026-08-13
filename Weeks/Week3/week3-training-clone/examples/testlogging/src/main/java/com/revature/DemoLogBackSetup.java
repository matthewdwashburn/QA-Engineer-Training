package com.revature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoLogBackSetup {



    /**
     * SLF4J + Logback: console + file appenders (written/4-thursday/java-introduction-to-logback.md).
     * Place {@code logback.xml} on classpath (same folder as classes when using {@code -cp code}).
     */
        private static final Logger log = LoggerFactory.getLogger("com.revature.DemoLogBackSetup");

        public static void main(String[] args) {
            log.trace("trace (often below root threshold)");
            log.debug("debug: configuration loaded");
            log.info("info: demo main started");
            log.warn("warn: example warning");
            log.error("error: example error without exception");
            System.out.println("\nCheck logs/demo-logback.log relative to process working directory.");
        }
    }


