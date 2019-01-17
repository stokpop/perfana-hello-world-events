package nl.stokpop.perfana.event;

import io.perfana.event.PerfanaTestEventAdapter;
import io.perfana.event.ScheduleEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class StokpopHelloPerfanaEvent extends PerfanaTestEventAdapter {

    private final static String PERFANA_EVENT_NAME = StokpopHelloPerfanaEvent.class.getSimpleName();

    static {
        say("Class loaded");
        System.getenv().forEach((key, value) -> say(String.format("env: %s=%s", key, value)));
    }

    public StokpopHelloPerfanaEvent() {
        say("Default constructor called.");
    }

    @Override
    public void beforeTest(String testId, Map<String,String> properties) {
        say("Hello before test  [" + testId + "]");
        say("Perfana event properties: " + properties);
    }

    @Override
    public void afterTest(String testId, Map<String,String> properties) {
        say("Hello after test [" + testId + "]");
    }
    
    @Override
    public void keepAlive(String testId, Map<String, String> eventProperties) {
        say("Hello keep alive for test [" + testId + "]");
    }

    @Override
    public void customEvent(String testId, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {

        String eventName = scheduleEvent.getName();
        
        if ("fail-over".equalsIgnoreCase(eventName)) {
            failOverEvent(testId, eventProperties, scheduleEvent);
        }
        else if ("scale-down".equalsIgnoreCase(eventName)) {
            scaleDownEvent(testId, eventProperties, scheduleEvent);
        }
        else if ("heapdump".equalsIgnoreCase(eventName)) {
            heapdumpEvent(testId, eventProperties, scheduleEvent);
        }
        else {
            say("WARNING: ignoring unknown event [" + eventName + "]");
        }
    }

    private void heapdumpEvent(String testId, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        Map<String, String> settings = parseSettings(scheduleEvent.getSettings());
        int durationInMillis = Integer.valueOf(settings.getOrDefault("durationInMillis", "4000"));
        say("Start " + scheduleEvent);
        {
            try {
                Thread.sleep(durationInMillis);
            } catch (InterruptedException e) {
                say("WARNING: Heap dump thread was interrupted!");
                Thread.currentThread().interrupt();
            }
        }
        say("Finish " + scheduleEvent);
    }

    private void scaleDownEvent(String testId, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        say("dispatched scale-down event for test [" + testId + "] with settings [" + scheduleEvent.getSettings() + "]");
    }

    private void failOverEvent(String testId, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        Map<String, String> parsedSettings = parseSettings(scheduleEvent.getSettings());
        say("dispatched fail-over event for test [" + testId + "] with parsed settings: " + parsedSettings);
    }

    static Map<String, String> parseSettings(String eventSettings) {
        if (eventSettings == null || eventSettings.trim().length() == 0) {
            return Collections.emptyMap();
        }
        return Arrays.stream(eventSettings.split(";"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(k -> k[0], v -> v.length == 2 ? v[1] : ""));
    }

    private static void say(String something) {
        System.out.printf("[%s] %s%n", PERFANA_EVENT_NAME, something);
    }
}
