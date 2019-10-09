package nl.stokpop.helloworld.event;

import nl.stokpop.eventscheduler.api.TestContext;
import nl.stokpop.eventscheduler.event.EventGenerator;
import nl.stokpop.eventscheduler.event.ScheduleEvent;
import nl.stokpop.eventscheduler.generator.EventGeneratorProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class StokpopEventGenerator implements EventGenerator {

    private static final String INPUT_FILE_TAG = "input-file";

    @Override
    public List<ScheduleEvent> generate(TestContext context, EventGeneratorProperties props) {

        String slowbackendFilePath = props.getProperty(INPUT_FILE_TAG);

        if (slowbackendFilePath == null) {
            throw new RuntimeException("unable to find setting for: " + INPUT_FILE_TAG);
        }

        System.out.println("StokpopEventGenerator: using input-file path: " + slowbackendFilePath);

        List<ScheduleEvent> events = new ArrayList<>();
        events.add(new ScheduleEvent(
                Duration.of(1, SECONDS),
                "hello-world",
                "phase 1",
                "{}"));
        return events;
    }
}
