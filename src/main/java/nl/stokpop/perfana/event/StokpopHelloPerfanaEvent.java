package nl.stokpop.perfana.event;

import io.perfana.client.api.PerfanaClientLogger;
import io.perfana.client.api.PerfanaTestContext;
import io.perfana.event.PerfanaEventAdapter;
import io.perfana.event.ScheduleEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class StokpopHelloPerfanaEvent extends PerfanaEventAdapter {

    static {
        sayStatic("Class loaded");
        
        System.getenv().forEach((key, value) -> sayStatic(String.format("env: %s=%s", key, value)));
    }

    public StokpopHelloPerfanaEvent() {
        sayStatic("Default constructor called.");
    }

    @Override
    public String getName() {
        return "StokpopHelloPerfanaEvent";
    }

    @Override
    public void beforeTest(PerfanaTestContext context, Map<String,String> properties) {
        say(context.getLogger(), "Hello before test [" + context.getTestRunId() + "]");
        say(context.getLogger(), "Perfana event properties: " + properties);

        try {
            say(context.getLogger(), "Sleep for 2 seconds");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            say(context.getLogger(), "Thread sleep interrupted");
            Thread.currentThread().interrupt();
        }
        say(context.getLogger(), "Wakeup after 2 seconds");
    }

    @Override
    public void afterTest(PerfanaTestContext context, Map<String,String> properties) {
        say(context.getLogger(), "Hello after test [" + context.getTestRunId() + "]");
    }
    
    @Override
    public void keepAlive(PerfanaTestContext context, Map<String, String> eventProperties) {
        say(context.getLogger(), "Hello keep alive for test [" + context.getTestRunId() + "]");
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
            say(context.getLogger(), "WARNING: ignoring unknown event [" + eventName + "]");
        }
    }

    private void restart(PerfanaTestContext context, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        Map<String, String> settings = parseSettings(scheduleEvent.getSettings());
        int durationInMillis = Integer.valueOf(settings.getOrDefault("durationInMillis", "10000"));
        say(context.getLogger(), "Start " + scheduleEvent);
        {
            try {
                Thread.sleep(durationInMillis);
            } catch (InterruptedException e) {
                say(context.getLogger(), "WARNING: Restart thread was interrupted!");
                Thread.currentThread().interrupt();
            }
        }
        say(context.getLogger(), "Finish " + scheduleEvent);

    }

    private void heapdumpEvent(PerfanaTestContext context, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        Map<String, String> settings = parseSettings(scheduleEvent.getSettings());
        int durationInMillis = Integer.valueOf(settings.getOrDefault("durationInMillis", "4000"));
        say(context.getLogger(), "Start " + scheduleEvent);
        {
            try {
                Thread.sleep(durationInMillis);
            } catch (InterruptedException e) {
                say(context.getLogger(), "WARNING: Heap dump thread was interrupted!");
                Thread.currentThread().interrupt();
            }
        }
        say(context.getLogger(), "Finish " + scheduleEvent);
    }

    private void scaleDownEvent(PerfanaTestContext context, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        say(context.getLogger(), "dispatched scale-down event for test [" + context.getTestRunId() + "] with settings [" + scheduleEvent.getSettings() + "]");
    }

    private void failOverEvent(PerfanaTestContext context, Map<String, String> eventProperties, ScheduleEvent scheduleEvent) {
        Map<String, String> parsedSettings = parseSettings(scheduleEvent.getSettings());
        say(context.getLogger(), "dispatched fail-over event for test [" + context.getTestRunId() + "] with parsed settings: " + parsedSettings);
    }

    static Map<String, String> parseSettings(String eventSettings) {
        if (eventSettings == null || eventSettings.trim().length() == 0) {
            return Collections.emptyMap();
        }
        return Arrays.stream(eventSettings.split(";"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(k -> k[0], v -> v.length == 2 ? v[1] : ""));
    }

    private void say(PerfanaClientLogger logger, String something) {
        logger.info(String.format("[%s] %s", getName(), something));
    }

    private static void sayStatic(String something) {
        System.out.println(String.format("[%s] %s%n", StokpopHelloPerfanaEvent.class.getSimpleName(), something));
    }
}
