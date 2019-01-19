package nl.stokpop.perfana.event;

import io.perfana.client.api.PerfanaTestContext;
import io.perfana.client.api.PerfanaTestContextBuilder;
import io.perfana.event.ScheduleEvent;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StokpopHelloPerfanaEventTest {

    @Test
    public void beforeTest() {
        Map<String,String> properties = new HashMap<>();
        properties.put("prop1", "name1");
        properties.put("prop2", "name2");

        PerfanaTestContext context = new PerfanaTestContextBuilder()
                .setTestRunId("my-test-run-id")
                .build();
        
        StokpopHelloPerfanaEvent event = new StokpopHelloPerfanaEvent();
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
        Map<String, String> emptyMap = StokpopHelloPerfanaEvent.parseSettings("");
        assertEquals(0, emptyMap.size());
    }

    @Test
    public void parseSettingsOne() {
        Map<String, String> emptyMap = StokpopHelloPerfanaEvent.parseSettings("foo=bar");
        assertEquals(1, emptyMap.size());
        assertEquals("bar", emptyMap.get("foo"));
    }

    @Test
    public void parseSettingsTwo() {
        Map<String, String> emptyMap = StokpopHelloPerfanaEvent.parseSettings("foo=bar;name=stokpop");
        assertEquals(2, emptyMap.size());
        assertEquals("bar", emptyMap.get("foo"));
        assertEquals("stokpop", emptyMap.get("name"));
    }

    @Test
    public void parseSettingsNoValue() {
        Map<String, String> emptyMap = StokpopHelloPerfanaEvent.parseSettings("foo=bar;name");
        assertEquals(2,emptyMap.size());
        assertEquals("bar", emptyMap.get("foo"));
        assertEquals("", emptyMap.get("name"));
    }

    @Test
    public void parseSettingsNoEntry() {
        Map<String, String> emptyMap = StokpopHelloPerfanaEvent.parseSettings("foo=bar;");
        assertEquals(1,emptyMap.size());
        assertEquals("bar", emptyMap.get("foo"));
    }

}