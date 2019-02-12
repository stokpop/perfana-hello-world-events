package nl.stokpop.perfana.event;

import io.perfana.client.api.PerfanaTestContext;
import io.perfana.event.EventScheduleGenerator;
import io.perfana.event.ScheduleEvent;
import io.perfana.event.generator.GeneratorProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;

public class StokpopEventGenerator implements EventScheduleGenerator {

    private static final String SLOWBACKEND_FILE_TAG = "slowbackend-file";

    @Override
    public List<ScheduleEvent> generateEvents(PerfanaTestContext context, GeneratorProperties props) {

        String slowbackendFilePath = props.getProperty(SLOWBACKEND_FILE_TAG);

        if (slowbackendFilePath == null) {
            throw new RuntimeException("unable to find setting for: " + SLOWBACKEND_FILE_TAG);
        }
        
        context.getLogger().info("StokpopEventScheduleGenerator: using slowbackend path: " + slowbackendFilePath);

        // TODO: use slowbackend file path to generate events...
        List<ScheduleEvent> events = new ArrayList<>();
        events.add(new ScheduleEvent(
                Duration.of(1, SECONDS),
                "set-backend-delay",
                "phase 1: 50% of timeout",
                "{}"));
        return events;
    }
}
