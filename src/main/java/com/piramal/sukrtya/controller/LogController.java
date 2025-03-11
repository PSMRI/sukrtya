package com.piramal.sukrtya.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;

@Controller
@RequestMapping("/api")
public class LogController {

    @GetMapping("/logs")
    public String getLogsPage() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Sukrtya Logs</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 20px;
                        background-color: #f5f5f5;
                    }
                    .container {
                        max-width: 800px;
                        margin: 0 auto;
                        background-color: white;
                        padding: 20px;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    h1 {
                        color: #333;
                        margin-bottom: 20px;
                    }
                    .log-link {
                        display: inline-block;
                        background-color: #007bff;
                        color: white;
                        padding: 10px 20px;
                        text-decoration: none;
                        border-radius: 4px;
                        margin: 10px 0;
                    }
                    .log-link:hover {
                        background-color: #0056b3;
                    }
                    .icon {
                        margin-right: 8px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Sukrtya Application Logs</h1>
                    <a href="/api/logs/html" class="log-link" target="_blank">
                        <span class="icon">📋</span>View HTML Logs in New Tab
                    </a>
                </div>
            </body>
            </html>
            """;
    }

    @GetMapping(value = "/logs/html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<FileSystemResource> getHtmlLogs() {
        File logFile = new File("./logs/sukrtya.html");
        if (!logFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=sukrtya.html")
                .contentType(MediaType.TEXT_HTML)
                .body(new FileSystemResource(logFile));
    }
} 