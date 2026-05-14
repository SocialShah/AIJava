package com.example.demo.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final BuildProperties buildProperties;

    public CustomHealthIndicator(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Override
    public Health health() {
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

        return Health.up()
                .withDetail("service", serviceName)
                .withDetail("buildDateTime", formattedBuildTime)
                .withDetail("processUptime", uptime)
                .withDetail("message", statusMessage)
                .build();
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
