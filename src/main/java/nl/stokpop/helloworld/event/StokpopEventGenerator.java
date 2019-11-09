package nl.stokpop.helloworld.event;

import nl.stokpop.eventscheduler.api.EventGeneratorProperties;
import nl.stokpop.eventscheduler.api.EventLogger;
import nl.stokpop.eventscheduler.api.TestContext;
import nl.stokpop.eventscheduler.api.EventGenerator;
import nl.stokpop.eventscheduler.api.CustomEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class StokpopEventGenerator implements EventGenerator {

    private static final String INPUT_FILE_TAG = "input-file";
    private final EventLogger logger;
    private final EventGeneratorProperties properties;
    private final TestContext testContext;

    StokpopEventGenerator(TestContext testContext, EventGeneratorProperties properties, EventLogger logger) {
        this.testContext = testContext;
        this.properties = properties;
        this.logger = logger;
    }

    @Override
    public List<CustomEvent> generate() {
        String slowbackendFilePath = properties.getProperty(INPUT_FILE_TAG);

        if (slowbackendFilePath == null) {
            throw new RuntimeException("unable to find setting for: " + INPUT_FILE_TAG);
        }

        logger.info("StokpopEventGenerator: using input-file path: " + slowbackendFilePath);

        List<CustomEvent> events = new ArrayList<>();
        events.add(new CustomEvent(
                Duration.of(1, SECONDS),
                "hello-world",
                "phase 1",
                "{}"));
        return events;
    }
}
