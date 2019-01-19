package nl.stokpop.perfana.event;

import io.perfana.client.api.PerfanaTestContext;
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
    public void beforeTest(PerfanaTestContext context, Map<String,String> properties) {
        say("Hello before test [" + context.getTestRunId() + "]");
        say("Perfana event properties: " + properties);

        try {
            say("Sleep for 2 seconds");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            say("Thread sleep interrupted");
            Thread.currentThread().interrupt();
        }
        say("Wakeup after 2 seconds");
    }

    @Override
    public void afterTest(PerfanaTestContext context, Map<String,String> properties) {
        say("Hello after test [" + context.getTestRunId() + "]");
    }
    
    @Override
    public void keepAlive(PerfanaTestContext context, Map<String, String> eventProperties) {
        say("Hello keep alive for test [" + context.getTestRunId() + "]");
    }

    @Override
    public void customEvent(PerfanaTestContext context, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {

        String eventName = scheduleEvent.getName();
        
        if ("fail-over".equalsIgnoreCase(eventName)) {
            failOverEvent(context, eventProperties, scheduleEvent);
        }
        else if ("scale-down".equalsIgnoreCase(eventName)) {
            scaleDownEvent(context, eventProperties, scheduleEvent);
        }
        else if ("heapdump".equalsIgnoreCase(eventName)) {
            heapdumpEvent(context, eventProperties, scheduleEvent);
        }
        else if ("restart".equalsIgnoreCase(eventName)) {
            restart(context, eventProperties, scheduleEvent);
        }
        else {
            say("WARNING: ignoring unknown event [" + eventName + "]");
        }
    }

    private void restart(PerfanaTestContext context, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        Map<String, String> settings = parseSettings(scheduleEvent.getSettings());
        int durationInMillis = Integer.valueOf(settings.getOrDefault("durationInMillis", "10000"));
        say("Start " + scheduleEvent);
        {
            try {
                Thread.sleep(durationInMillis);
            } catch (InterruptedException e) {
                say("WARNING: Restart thread was interrupted!");
                Thread.currentThread().interrupt();
            }
        }
        say("Finish " + scheduleEvent);

    }

    private void heapdumpEvent(PerfanaTestContext context, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
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

    private void scaleDownEvent(PerfanaTestContext context, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        say("dispatched scale-down event for test [" + context.getTestRunId() + "] with settings [" + scheduleEvent.getSettings() + "]");
    }

    private void failOverEvent(PerfanaTestContext context, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        Map<String, String> parsedSettings = parseSettings(scheduleEvent.getSettings());
        say("dispatched fail-over event for test [" + context.getTestRunId() + "] with parsed settings: " + parsedSettings);
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
