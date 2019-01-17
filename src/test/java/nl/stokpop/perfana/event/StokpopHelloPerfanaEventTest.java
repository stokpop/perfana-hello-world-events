package nl.stokpop.perfana.event;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StokpopHelloPerfanaEventTest {

    @Test
    public void beforeTest() {
        Map<String,String> properties = new HashMap<>();
        properties.put("prop1", "name1");
        properties.put("prop2", "name2");

        StokpopHelloPerfanaEvent event = new StokpopHelloPerfanaEvent();
        event.beforeTest("my-test-id", properties);
        event.keepAlive("my-test-id", properties);
        event.customEvent("my-test-id", properties, "fail-over", "debug=true;server=test");
        event.customEvent("my-test-id", properties, "scale-down", null);
        event.customEvent("my-test-id", properties, "scale-up", null);
        event.afterTest("my-test-id", properties);

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