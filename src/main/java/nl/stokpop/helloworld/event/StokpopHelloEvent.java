package nl.stokpop.helloworld.event;

import nl.stokpop.eventscheduler.api.EventLogger;
import nl.stokpop.eventscheduler.api.TestContext;
import nl.stokpop.eventscheduler.api.CustomEvent;
import nl.stokpop.eventscheduler.api.EventAdapter;
import nl.stokpop.eventscheduler.api.EventProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class StokpopHelloEvent extends EventAdapter {

    private static final int TO_MB = 1024 * 1024;
    
    static {
        sayStatic("Class loaded");

        //System.getenv().forEach((key, value) -> sayStatic(String.format("env: %s=%s", key, value)));
    }

    public StokpopHelloEvent(String eventName, TestContext testContext, EventProperties eventProperties, EventLogger logger) {
        super(eventName, testContext, eventProperties, logger);
        logger.info("Default constructor called.");
        printSystemInfo();
    }

    private void printSystemInfo() {
        int processors = Runtime.getRuntime().availableProcessors();
        long maxMemoryBytes = Runtime.getRuntime().maxMemory();
        long totalMemoryBytes = Runtime.getRuntime().totalMemory();
        long freeMemoryBytes = Runtime.getRuntime().freeMemory();

        logger.info(String.format("Number of processors: %-6d cores", processors));
        logger.info(String.format("Max memory:           %-6d MB", maxMemoryBytes/ TO_MB));
        logger.info(String.format("Total memory:         %-6d MB", totalMemoryBytes/ TO_MB));
        logger.info(String.format("Free memory:          %-6d MB", freeMemoryBytes/ TO_MB));
    }
    
    @Override
    public void beforeTest() {
        logger.info("Hello before test [" + testContext.getTestRunId() + "]");
        logger.info("Event properties: " + eventProperties);

        try {
            logger.info("Sleep for 2 seconds");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.info("Thread sleep interrupted");
            Thread.currentThread().interrupt();
        }
        logger.info("Wakeup after 2 seconds");
    }

    @Override
    public void afterTest() {
        logger.info("Hello after test [" + testContext.getTestRunId() + "]");
    }
    
    @Override
    public void keepAlive() {
        logger.info("Hello keep alive for test [" + testContext.getTestRunId() + "]");
    }

    @Override
    public void customEvent(CustomEvent scheduleEvent) {

        String eventName = scheduleEvent.getName();
        
        if ("fail-over".equalsIgnoreCase(eventName)) {
            failOverEvent(scheduleEvent);
        }
        else if ("scale-down".equalsIgnoreCase(eventName)) {
            scaleDownEvent(scheduleEvent);
        }
        else if ("heapdump".equalsIgnoreCase(eventName)) {
            heapdumpEvent(scheduleEvent);
        }
        else if ("restart".equalsIgnoreCase(eventName)) {
            restart(scheduleEvent);
        }
        else {
            logger.info("WARNING: ignoring unknown event [" + eventName + "]");
        }
    }

    private void restart(CustomEvent scheduleEvent) {
        Map<String, String> settings = parseSettings(scheduleEvent.getSettings());
        int durationInMillis = Integer.parseInt(settings.getOrDefault("durationInMillis", "10000"));
        logger.info("Start " + scheduleEvent);
        {
            try {
                Thread.sleep(durationInMillis);
            } catch (InterruptedException e) {
                logger.info("WARNING: Restart thread was interrupted!");
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Finish " + scheduleEvent);

    }

    private void heapdumpEvent(CustomEvent scheduleEvent) {
        Map<String, String> settings = parseSettings(scheduleEvent.getSettings());
        int durationInMillis = Integer.parseInt(settings.getOrDefault("durationInMillis", "4000"));
        logger.info("Start " + scheduleEvent);
        {
            try {
                Thread.sleep(durationInMillis);
            } catch (InterruptedException e) {
                logger.info("WARNING: Heap dump thread was interrupted!");
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Finish " + scheduleEvent);
    }

    private void scaleDownEvent(CustomEvent scheduleEvent) {
        logger.info("dispatched scale-down event for test [" + testContext.getTestRunId() + "] with settings [" + scheduleEvent.getSettings() + "]");
    }

    private void failOverEvent(CustomEvent scheduleEvent) {
        Map<String, String> parsedSettings = parseSettings(scheduleEvent.getSettings());
        logger.info("dispatched fail-over event for test [" + testContext.getTestRunId() + "] with parsed settings: " + parsedSettings);
    }

    static Map<String, String> parseSettings(String eventSettings) {
        if (eventSettings == null || eventSettings.trim().length() == 0) {
            return Collections.emptyMap();
        }
        return Arrays.stream(eventSettings.split(";"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(k -> k[0], v -> v.length == 2 ? v[1] : ""));
    }

    private static void sayStatic(String something) {
        System.out.printf("[%s] %s%n", StokpopHelloEvent.class.getSimpleName(), something);
    }
}
