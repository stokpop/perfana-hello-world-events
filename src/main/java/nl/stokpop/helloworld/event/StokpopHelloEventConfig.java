package nl.stokpop.helloworld.event;

import nl.stokpop.eventscheduler.api.config.EventConfig;

public class StokpopHelloEventConfig extends EventConfig {

    private String myRestService;
    private Long helloInitialSleepSeconds;
    private String helloMessage = "Default Hello Message";
    private String myCredentials;
    private String myEventTags;

    @Override
    public String getEventFactory() {
        return StokpopHelloEventFactory.class.getName();
    }

    public String getMyRestService() {
        return myRestService;
    }

    public void setMyRestService(String myRestService) {
        this.myRestService = myRestService;
    }

    public Long getHelloInitialSleepSeconds() {
        return helloInitialSleepSeconds;
    }

    public void setHelloInitialSleepSeconds(Long helloInitialSleepSeconds) {
        this.helloInitialSleepSeconds = helloInitialSleepSeconds;
    }

    public String getHelloMessage() {
        return helloMessage;
    }

    public void setHelloMessage(String helloMessage) {
        this.helloMessage = helloMessage;
    }

    public String getMyCredentials() {
        return myCredentials;
    }

    public void setMyCredentials(String myCredentials) {
        this.myCredentials = myCredentials;
    }

    public String getMyEventTags() {
        return myEventTags;
    }

    public void setMyEventTags(String myEventTags) {
        this.myEventTags = myEventTags;
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
