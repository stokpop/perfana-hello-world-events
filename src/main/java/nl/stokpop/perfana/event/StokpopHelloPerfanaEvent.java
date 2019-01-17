package nl.stokpop.perfana.event;

import io.perfana.event.PerfanaTestEventAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class StokpopHelloPerfanaEvent extends PerfanaTestEventAdapter {

    private final static String PERFANA_EVENT_NAME = StokpopHelloPerfanaEvent.class.getCanonicalName();

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
    public void customEvent(String testId, Map<String, String> eventProperties, String eventName, String eventSettings) {
        if ("fail-over".equalsIgnoreCase(eventName)) {
            failOverEvent(testId, eventProperties, eventSettings);
        }
        else if ("scale-down".equalsIgnoreCase(eventName)) {
            scaleDownEvent(testId, eventProperties, eventSettings);
        }
        else {
            say("WARNING: ignoring unknown event [" + eventName + "]");
        }
    }

    private void scaleDownEvent(String testId, Map<String, String> eventProperties, String eventSettings) {
        say("dispatched scale-down event for test [" + testId + "] with settings [" + eventSettings + "]");
    }

    private void failOverEvent(String testId, Map<String, String> eventProperties, String eventSettings) {
        Map<String, String> settings = parseSettings(eventSettings);
        say("dispatched fail-over event for test [" + testId + "] with settings [" + eventSettings + "] with settings: " + settings);
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
