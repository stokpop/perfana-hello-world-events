package nl.stokpop.helloworld.event;

import nl.stokpop.eventscheduler.api.TestContext;
import nl.stokpop.eventscheduler.api.TestContextBuilder;
import nl.stokpop.eventscheduler.event.EventProperties;
import nl.stokpop.eventscheduler.event.ScheduleEvent;
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

        EventProperties properties = new EventProperties(props);

        TestContext context = new TestContextBuilder()
                .setTestRunId("my-test-run-id")
                .build();
        
        StokpopHelloEvent event = new StokpopHelloEvent();
        event.beforeTest(context, properties);
        event.keepAlive(context, properties);
        event.customEvent(context, properties, ScheduleEvent.createFromLine("PT3S|fail-over|debug=true;server=test"));
        event.customEvent(context, properties, ScheduleEvent.createFromLine("PT1M|scale-down"));
        event.customEvent(context, properties, ScheduleEvent.createFromLine("PT1H2M3S|scale-up|"));
        event.afterTest(context, properties);

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