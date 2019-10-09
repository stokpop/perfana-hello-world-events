package nl.stokpop.helloworld.event;

import nl.stokpop.eventscheduler.api.TestContext;
import nl.stokpop.eventscheduler.event.EventAdapter;
import nl.stokpop.eventscheduler.event.EventProperties;
import nl.stokpop.eventscheduler.event.ScheduleEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class StokpopHelloEvent extends EventAdapter {

    static {
        sayStatic("Class loaded");
        
        System.getenv().forEach((key, value) -> sayStatic(String.format("env: %s=%s", key, value)));
    }

    public StokpopHelloEvent() {
        sayStatic("Default constructor called.");
    }

    @Override
    public String getName() {
        return "StokpopHelloEvent";
    }

    @Override
    public void beforeTest(TestContext context, EventProperties properties) {
        say("Hello before test [" + context.getTestRunId() + "]");
        say("Event properties: " + properties);

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
    public void afterTest(TestContext context, EventProperties properties) {
        say("Hello after test [" + context.getTestRunId() + "]");
    }
    
    @Override
    public void keepAlive(TestContext context, EventProperties properties) {
        say("Hello keep alive for test [" + context.getTestRunId() + "]");
    }

    @Override
    public void customEvent(TestContext context, EventProperties properties, ScheduleEvent scheduleEvent) {

        String eventName = scheduleEvent.getName();
        
        if ("fail-over".equalsIgnoreCase(eventName)) {
            failOverEvent(context, properties, scheduleEvent);
        }
        else if ("scale-down".equalsIgnoreCase(eventName)) {
            scaleDownEvent(context, properties, scheduleEvent);
        }
        else if ("heapdump".equalsIgnoreCase(eventName)) {
            heapdumpEvent(context, properties, scheduleEvent);
        }
        else if ("restart".equalsIgnoreCase(eventName)) {
            restart(context, properties, scheduleEvent);
        }
        else {
            say("WARNING: ignoring unknown event [" + eventName + "]");
        }
    }

    private void restart(TestContext context, EventProperties properties, ScheduleEvent scheduleEvent) {
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

    private void heapdumpEvent(TestContext context, EventProperties properties, ScheduleEvent scheduleEvent) {
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

    private void scaleDownEvent(TestContext context, EventProperties properties, ScheduleEvent scheduleEvent) {
        say("dispatched scale-down event for test [" + context.getTestRunId() + "] with settings [" + scheduleEvent.getSettings() + "]");
    }

    private void failOverEvent(TestContext context, EventProperties properties, ScheduleEvent scheduleEvent) {
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

    private void say(String something) {
        System.out.println(String.format("[%s] %s", getName(), something));
    }

    private static void sayStatic(String something) {
        System.out.println(String.format("[%s] %s%n", StokpopHelloEvent.class.getSimpleName(), something));
    }
}
