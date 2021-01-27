package nl.stokpop.helloworld.event;

import net.jcip.annotations.NotThreadSafe;
import nl.stokpop.eventscheduler.api.config.EventConfig;
import nl.stokpop.eventscheduler.api.config.TestContext;

import java.time.Duration;

@NotThreadSafe
public class StokpopHelloEventConfig extends EventConfig {

    private String myRestService;
    private int helloInitialSleepSeconds = 4;
    private String helloMessage = "Default Hello Message";
    private String myCredentials;
    private String myEventTags;

    public void setMyRestService(String myRestService) {
        this.myRestService = myRestService;
    }

    public void setHelloInitialSleepSeconds(int helloInitialSleepSeconds) {
        this.helloInitialSleepSeconds = helloInitialSleepSeconds;
    }

    public void setHelloMessage(String helloMessage) {
        this.helloMessage = helloMessage;
    }

    public void setMyCredentials(String myCredentials) {
        this.myCredentials = myCredentials;
    }

    public void setMyEventTags(String myEventTags) {
        this.myEventTags = myEventTags;
    }

    @Override
    public StokpopHelloEventContext toContext() {
        return new StokpopHelloEventContext(super.toContext(), myRestService, Duration.ofSeconds(helloInitialSleepSeconds), helloMessage, myCredentials, myEventTags);
    }

    @Override
    public StokpopHelloEventContext toContext(TestContext override) {
        return new StokpopHelloEventContext(super.toContext(override), myRestService, Duration.ofSeconds(helloInitialSleepSeconds), helloMessage, myCredentials, myEventTags);
    }

    @Override
    public String toString() {
        return "StokpopHelloEventConfig{" +
            "myRestService='" + myRestService + '\'' +
            ", helloInitialSleepSeconds=" + helloInitialSleepSeconds +
            ", helloMessage='" + helloMessage + '\'' +
            ", myCredentials='" + myCredentials + '\'' +
            ", myEventTags='" + myEventTags + '\'' +
            "} " + super.toString();
    }
}
