package com.example.demo.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final BuildProperties buildProperties;

    public HealthController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping(value = "/health", produces = "text/html;charset=UTF-8")
    public String getHealthHtml() {
        // Get service name
        String serviceName = buildProperties.getName();

        // Get build time
        Instant buildTime = buildProperties.getTime();
        String formattedBuildTime = buildTime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        // Get process uptime (in seconds)
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        String uptime = formatUptime(uptimeMillis);

        // Status message
        String statusMessage = "System is up";

        // Build HTML response
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Health Check</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .health-container { border: 1px solid #ddd; padding: 20px; border-radius: 5px; background-color: #f9f9f9; }
                        h1 { color: #28a745; }
                        h3 { color: #333; margin-top: 15px; }
                        .status { color: #28a745; font-weight: bold; }
                        .detail { margin-left: 10px; }
                    </style>
                </head>
                <body>
                    <div class="health-container">
                        <h1>Service Health Status</h1>
                        <h3>Build Info: %s</h3>
                        <div class="detail">
                            <p><strong>Service Name:</strong> %s</p>
                            <p><strong>Build DateTime:</strong> %s</p>
                            <p><strong>Process Uptime:</strong> %s</p>
                            <p><strong>Status:</strong> <span class="status">%s</span></p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(formattedBuildTime, serviceName, formattedBuildTime, uptime, statusMessage);
    }

    private String formatUptime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return String.format("%d days %d hours %d minutes", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%d hours %d minutes", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d minutes %d seconds", minutes, seconds % 60);
        } else {
            return String.format("%d seconds", seconds);
        }
    }
}
