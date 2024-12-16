package com.piramal.sukrtya.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {
    private static final Logger logger = LogManager.getLogger(LogController.class);

    @GetMapping("/log")
    public String logExample() {
        logger.info("INFO level log");
        logger.debug("DEBUG level log");
        logger.error("ERROR level log");
        return "Logs have been printed!";
    }
}

