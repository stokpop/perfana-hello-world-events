package nl.stokpop.helloworld.event;

/*-
 * #%L
 * test-events-hello-world
 * %%
 * Copyright (C) 2019 Stokpop
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import nl.stokpop.eventscheduler.api.CustomEvent;
import nl.stokpop.eventscheduler.api.config.TestConfig;
import nl.stokpop.eventscheduler.log.EventLoggerStdOut;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StokpopHelloEventTest {

    @Test
    public void beforeTest() {
        StokpopHelloEventConfig stokpopHelloEventConfig = new StokpopHelloEventConfig();
        stokpopHelloEventConfig.setEventFactory(StokpopHelloEventFactory.class.getSimpleName());
        stokpopHelloEventConfig.setHelloMessage("hello");
        stokpopHelloEventConfig.setName("myEvent1");
        stokpopHelloEventConfig.setEnabled(true);
        stokpopHelloEventConfig.setTestConfig(TestConfig.builder().build());

        StokpopHelloEvent event = new StokpopHelloEvent(stokpopHelloEventConfig, EventLoggerStdOut.INSTANCE);
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
