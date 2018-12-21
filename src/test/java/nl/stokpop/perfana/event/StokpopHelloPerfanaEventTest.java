package nl.stokpop.perfana.event;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class StokpopHelloPerfanaEventTest {

    @Test
    public void beforeTest() {
        Map<String,String> properties = new HashMap<>();
        properties.put("prop1", "name1");
        properties.put("prop2", "name2");

        StokpopHelloPerfanaEvent event = new StokpopHelloPerfanaEvent();
        event.beforeTest("my-test-id", properties);
        event.keepAlive("my-test-id", properties);
        event.failover("my-test-id", properties);
        event.afterTest("my-test-id", properties);

        // not much to assert really... just look at System.out and
        // check it does not blow with an Exception...

    }
}