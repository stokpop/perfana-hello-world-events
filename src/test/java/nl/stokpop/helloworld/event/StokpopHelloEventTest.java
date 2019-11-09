package nl.stokpop.helloworld.event;

import nl.stokpop.eventscheduler.api.TestContext;
import nl.stokpop.eventscheduler.api.TestContextBuilder;
import nl.stokpop.eventscheduler.api.EventProperties;
import nl.stokpop.eventscheduler.api.CustomEvent;
import nl.stokpop.eventscheduler.log.EventLoggerStdOut;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StokpopHelloEventTest {

    @Test
    public void beforeTest() {
        Map<String,String> props = new HashMap<>();
        props.put("prop1", "name1");
        props.put("prop2", "name2");
        props.put("eventFactory", "testme");

        EventProperties properties = new EventProperties(props);

        TestContext context = new TestContextBuilder()
                .setTestRunId("my-test-run-id")
                .build();


        StokpopHelloEvent event = new StokpopHelloEvent("myEvent1", context, properties, EventLoggerStdOut.INSTANCE);
        event.beforeTest();
        event.keepAlive();
        event.customEvent(CustomEvent.createFromLine("PT3S|fail-over|debug=true;server=test"));
        event.customEvent(CustomEvent.createFromLine("PT1M|scale-down"));
        event.customEvent(CustomEvent.createFromLine("PT1H2M3S|scale-up|"));
        event.afterTest();

        // not much to assert really... just look at System.out and
        // check it does not blow with an Exception...

    }

    @Test
    public void parseSettingsZero() {
        Map<String, String> emptyMap = StokpopHelloEvent.parseSettings("");
        assertEquals(0, emptyMap.size());
    }

    @Test
    public void parseSettingsOne() {
        Map<String, String> emptyMap = StokpopHelloEvent.parseSettings("foo=bar");
        assertEquals(1, emptyMap.size());
        assertEquals("bar", emptyMap.get("foo"));
    }

    @Test
    public void parseSettingsTwo() {
        Map<String, String> emptyMap = StokpopHelloEvent.parseSettings("foo=bar;name=stokpop");
        assertEquals(2, emptyMap.size());
        assertEquals("bar", emptyMap.get("foo"));
        assertEquals("stokpop", emptyMap.get("name"));
    }

    @Test
    public void parseSettingsNoValue() {
        Map<String, String> emptyMap = StokpopHelloEvent.parseSettings("foo=bar;name");
        assertEquals(2,emptyMap.size());
        assertEquals("bar", emptyMap.get("foo"));
        assertEquals("", emptyMap.get("name"));
    }

    @Test
    public void parseSettingsNoEntry() {
        Map<String, String> emptyMap = StokpopHelloEvent.parseSettings("foo=bar;");
        assertEquals(1,emptyMap.size());
        assertEquals("bar", emptyMap.get("foo"));
    }

}