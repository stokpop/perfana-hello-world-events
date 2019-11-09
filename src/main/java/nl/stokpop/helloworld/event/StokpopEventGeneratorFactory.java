package nl.stokpop.helloworld.event;

import nl.stokpop.eventscheduler.api.EventLogger;
import nl.stokpop.eventscheduler.api.TestContext;
import nl.stokpop.eventscheduler.api.EventGenerator;
import nl.stokpop.eventscheduler.api.EventGeneratorFactory;
import nl.stokpop.eventscheduler.api.EventGeneratorProperties;

public class StokpopEventGeneratorFactory implements EventGeneratorFactory {

    @Override
    public EventGenerator create(TestContext testContext, EventGeneratorProperties eventGeneratorProperties, EventLogger eventLogger) {
        return new StokpopEventGenerator(testContext, eventGeneratorProperties, eventLogger);
    }
}
