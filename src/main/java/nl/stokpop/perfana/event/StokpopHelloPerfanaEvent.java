package nl.stokpop.perfana.event;

import io.perfana.event.PerfanaTestEventAdapter;

import java.util.Map;

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
    public void failover(String testId, Map<String,String> properties) {
        say("Hello failover " + testId + "]");
    }

    @Override
    public void keepAlive(final String testId, final Map<String, String> eventProperties) {
        say("Hello keep alive for test [" + testId + "]");
    }

    private static void say(String something) {
        System.out.printf("[%s] %s%n", PERFANA_EVENT_NAME, something);
    }
}
