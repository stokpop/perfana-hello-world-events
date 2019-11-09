package nl.stokpop.helloworld.event;

import nl.stokpop.eventscheduler.api.EventLogger;
import nl.stokpop.eventscheduler.api.TestContext;
import nl.stokpop.eventscheduler.api.Event;
import nl.stokpop.eventscheduler.api.EventFactory;
import nl.stokpop.eventscheduler.api.EventProperties;

public class StokpopHelloEventFactory implements EventFactory {

    @Override
    public Event create(String eventName, TestContext testContext, EventProperties eventProperties, EventLogger eventLogger) {
        return new StokpopHelloEvent(eventName, testContext, eventProperties, eventLogger);
    }
}
