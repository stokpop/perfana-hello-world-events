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
import nl.stokpop.eventscheduler.api.EventProperties;
import nl.stokpop.eventscheduler.api.TestContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.stokpop.helloworld.event.StokpopHelloEvent.AllowedCustomEvents.failOver;
import static nl.stokpop.helloworld.event.StokpopHelloEvent.AllowedCustomEvents.heapdump;
import static nl.stokpop.helloworld.event.StokpopHelloEvent.AllowedCustomEvents.restart;
import static nl.stokpop.helloworld.event.StokpopHelloEvent.AllowedCustomEvents.scaleDown;

public class StokpopHelloEvent extends EventAdapter {

    private static final int TO_MB = 1024 * 1024;

    enum AllowedProperties {

        initialSleepSeconds("helloInitialSleepSeconds"),
        helloMessage("helloMessage"),
        myRestServer("myRestServer");

        private String propertyName;

        AllowedProperties(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public static Stream<AllowedProperties> stream() {
            return Stream.of(AllowedProperties.values());
        }
    }


    enum AllowedCustomEvents {

        failOver("fail-over"),
        scaleDown("scale-down"),
        heapdump("heapdump"),
        restart("restart");

        private String eventName;

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

    private Set<String> allowedProperties = setOf(AllowedProperties.stream()
            .map(AllowedProperties::getPropertyName)
            .toArray(String[]::new));

    private Set<String> allowedCustomEvents = setOf(AllowedCustomEvents.stream()
            .map(AllowedCustomEvents::getEventName)
            .toArray(String[]::new));

    static {
        sayStatic("Class loaded");

        //System.getenv().forEach((key, value) -> sayStatic(String.format("env: %s=%s", key, value)));
    }

    public StokpopHelloEvent(String eventName, TestContext testContext, EventProperties eventProperties, EventLogger logger) {
        super(eventName, testContext, eventProperties, logger);
        logger.info("Default constructor called.");
        printSystemInfo();

        String helloMessage =
                eventProperties.getPropertyOrDefault(
                        AllowedProperties.helloMessage.getPropertyName(),
                        "Default Hello Message");

        logger.info("Message: " + helloMessage);
    }

    @Override
    public Collection<String> allowedProperties() {
        return allowedProperties;
    }

    @Override
    public Collection<String> allowedCustomEvents() {
        return allowedCustomEvents;
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

        String helloInitialSleepSeconds =
                eventProperties.getPropertyOrDefault(
                        AllowedProperties.initialSleepSeconds.getPropertyName(),
                        "2");

        int sleepSeconds = Integer.parseInt(helloInitialSleepSeconds);

        try {
            logger.info("Sleep for " + sleepSeconds + " seconds");
            Thread.sleep(sleepSeconds * 1000);
        } catch (InterruptedException e) {
            logger.info("Thread sleep interrupted");
            Thread.currentThread().interrupt();
        }
        logger.info("Wakeup after " + sleepSeconds + " seconds");
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
