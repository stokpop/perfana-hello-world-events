package nl.stokpop.helloworld.event;

/*-
 *
 * test-events-hello-world
 *
 * Copyright (C) 2019 Stokpop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import nl.stokpop.eventscheduler.api.CustomEvent;
import nl.stokpop.eventscheduler.api.EventAdapter;
import nl.stokpop.eventscheduler.api.EventLogger;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.stokpop.helloworld.event.StokpopHelloEvent.AllowedCustomEvents.*;

public class StokpopHelloEvent extends EventAdapter<StokpopHelloEventConfig> {

    private static final int TO_MB = 1024 * 1024;

    enum AllowedCustomEvents {

        failOver("fail-over"),
        scaleDown("scale-down"),
        heapdump("heapdump"),
        restart("restart"),
        helloWorld("hello-world");

        private final String eventName;

        AllowedCustomEvents(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return eventName;
        }

        public static Stream<AllowedCustomEvents> stream() {
            return Stream.of(values());
        }
        
        public boolean hasEventName(String name) {
            return this.eventName.equals(name);
        }
    }

    private final Set<String> allowedCustomEvents = setOf(AllowedCustomEvents.stream()
        .map(AllowedCustomEvents::getEventName)
        .toArray(String[]::new));

    static {
        sayStatic("Class loaded");

        //System.getenv().forEach((key, value) -> sayStatic(String.format("env: %s=%s", key, value)));
    }

    public StokpopHelloEvent(StokpopHelloEventConfig eventConfig, EventLogger logger) {
        super(eventConfig, logger);
        logger.info("Default constructor called.");
        printSystemInfo();

        String helloMessage = eventConfig.getHelloMessage();
        logger.info("Message: " + helloMessage);

        logger.info("Got StokpopHelloEventConfig: " + eventConfig);
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
    public Collection<String> allowedCustomEvents() {
        return allowedCustomEvents;
    }

    @Override
    public void beforeTest() {
        logger.info("Hello before test [" + eventConfig.getTestConfig().getTestRunId() + "]");

        Long helloInitialSleepSeconds = eventConfig.getHelloInitialSleepSeconds();

        long sleepSeconds = helloInitialSleepSeconds == null ? 2 : helloInitialSleepSeconds;

        try {
            logger.info("Sleep for " + sleepSeconds + " seconds");
            Thread.sleep(Duration.ofSeconds(sleepSeconds).toMillis());
        } catch (InterruptedException e) {
            logger.info("Thread sleep interrupted");
            Thread.currentThread().interrupt();
        }
        logger.info("Wakeup after " + sleepSeconds + " seconds");
    }

    @Override
    public void afterTest() {
        logger.info("Hello after test [" + eventConfig.getTestConfig().getTestRunId() + "]");
    }
    
    @Override
    public void keepAlive() {
        logger.info("Hello keep alive for test [" + eventConfig.getTestConfig().getTestRunId() + "]");
    }

    @Override
    public void customEvent(CustomEvent scheduleEvent) {

        String eventName = scheduleEvent.getName();
        
        if (failOver.hasEventName(eventName)) {
            failOverEvent(scheduleEvent);
        }
        else if (scaleDown.hasEventName(eventName)) {
            scaleDownEvent(scheduleEvent);
        }
        else if (heapdump.hasEventName(eventName)) {
            heapdumpEvent(scheduleEvent);
        }
        else if (restart.hasEventName(eventName)) {
            restart(scheduleEvent);
        }
        else if (helloWorld.hasEventName(eventName)) {
            logger.info("Custom hello world called:" + scheduleEvent.getSettings());
        }
        else {
            logger.info("WARNING: ignoring unknown event [" + eventName + "]");
        }
    }

    private void restart(CustomEvent scheduleEvent) {
        Map<String, String> settings = parseSettings(scheduleEvent.getSettings());
        int durationInMillis = Integer.parseInt(settings.getOrDefault("durationInMillis", "10000"));
        logger.info("Start " + scheduleEvent);
        sleep(durationInMillis);
        logger.info("Finish " + scheduleEvent);
    }

    private void heapdumpEvent(CustomEvent scheduleEvent) {
        Map<String, String> settings = parseSettings(scheduleEvent.getSettings());
        int durationInMillis = Integer.parseInt(settings.getOrDefault("durationInMillis", "4000"));
        logger.info("Start " + scheduleEvent);
        sleep(durationInMillis);
        logger.info("Finish " + scheduleEvent);
    }

    private void sleep(int durationInMillis) {
        try {
            Thread.sleep(durationInMillis);
        } catch (InterruptedException e) {
            logger.info("WARNING: Heap dump thread was interrupted!");
            Thread.currentThread().interrupt();
        }
    }

    private void scaleDownEvent(CustomEvent scheduleEvent) {
        logger.info("dispatched scale-down event for test [" + eventConfig.getTestConfig().getTestRunId() + "] with settings [" + scheduleEvent.getSettings() + "]");
    }

    private void failOverEvent(CustomEvent scheduleEvent) {
        Map<String, String> parsedSettings = parseSettings(scheduleEvent.getSettings());
        logger.info("dispatched fail-over event for test [" + eventConfig.getTestConfig().getTestRunId() + "] with parsed settings: " + parsedSettings);
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
