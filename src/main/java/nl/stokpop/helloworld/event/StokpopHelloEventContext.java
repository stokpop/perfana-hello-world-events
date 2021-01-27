package nl.stokpop.helloworld.event;

import net.jcip.annotations.Immutable;
import nl.stokpop.eventscheduler.api.config.EventContext;

import java.time.Duration;

@Immutable
public class StokpopHelloEventContext extends EventContext {

    private final String myRestService;
    private final Duration helloInitialSleep;
    private final String helloMessage;
    private final String myCredentials;
    private final String myEventTags;

    protected StokpopHelloEventContext(EventContext context, String myRestService, Duration helloInitialSleep, String helloMessage, String myCredentials, String myEventTags) {
        super(context, StokpopHelloEventFactory.class.getName(), true);
        this.myRestService = myRestService;
        this.helloInitialSleep = helloInitialSleep;
        this.helloMessage = helloMessage;
        this.myCredentials = myCredentials;
        this.myEventTags = myEventTags;
    }

    public String getMyRestService() {
        return myRestService;
    }

    public Duration getHelloInitialSleep() {
        return helloInitialSleep;
    }

    public String getHelloMessage() {
        return helloMessage;
    }

    public String getMyCredentials() {
        return myCredentials;
    }

    public String getMyEventTags() {
        return myEventTags;
    }

    @Override
    public String toString() {
        return "StokpopHelloEventConfig{" +
            "myRestService='" + myRestService + '\'' +
            ", helloInitialSleep=" + helloInitialSleep +
            ", helloMessage='" + helloMessage + '\'' +
            ", myCredentials='" + myCredentials + '\'' +
            ", myEventTags='" + myEventTags + '\'' +
            "} " + super.toString();
    }
}
