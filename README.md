# test-events-hello-world

This project shows an example implementation of the `Event` interface
and the `EventGenerator` interface.

The `StokpopHelloEvent` shows when events are called in `System.out`.
It also prints out some system information at the start.

The `StokpopEventGenerator` shows how to create a custom list of events from
a generator class with properties.

## to try it out

If you just want to experiment with the events-gatling-maven-plugin and test-events-hello-world,
just add the test-events-hello-world to plugin to the dependencies of the events-gatling-maven-plugin.
For instance add the following to the Afterburner Gatling script:

```xml 
<plugin>
    <groupId>nl.stokpop</groupId>
    <artifactId>events-gatling-maven-plugin</artifactId>
    <configuration>
        <simulationClass>afterburner.AfterburnerBasicSimulation</simulationClass>
        <eventScheduleScript>
            PT5S|restart|{ server:'myserver' replicas:2 tags: [ 'first', 'second' ] }
            PT10S|scale-down
            PT30S|heapdump|server=myserver.example.com;port=1567
            PT1M|scale-up|{ replicas:2 }
        </eventScheduleScript>
        <events>
            <StokpopHelloEvent1>
                <eventFactory>nl.stokpop.helloworld.event.StokpopHelloEventFactory</eventFactory>
                <myRestServer>https://my-rest-api</myName>
                <myCredentials>${ENV.SECRET}</myCredentials>
            </StokpopHelloEvent1>
        </events>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>nl.stokpop</groupId>
            <artifactId>test-events-hello-world</artifactId>
            <version>1.0.2</version>
        </dependency>
    </dependencies>
</plugin>
```

See also: 
* https://github.com/stokpop/events-gatling-maven-plugin
* https://github.com/stokpop/event-scheduler
*  

## for event builders

Add a dependency to the event-scheduler jar, just for compile.

Example:

```xml
<dependencies>
   <dependency>
       <groupId>nl.stokpop</groupId>
       <artifactId>event-scheduler</artifactId>
       <version>2.1.0</version>
       <scope>compile</scope>
   </dependency>
</dependencies>
```

Then you can your own implementations of the `Event` and the `EventFactory` interface.
And of the `EventGenerator` and the `EventGeneratorFactory` interface.

For convenience you can use the `EventAdapter` abstract class 
to implementing `Event` inteface. Only implement the method you want to override.

## add services files                               

The magic happens when you add the appropriate file
in `META-INF/services`. See the sample in this project.

You need to specify the fully qualified name of your implementation
(e.g. `nl.stokpop.helloworld.event.StokpopHelloEventFactory`) 
in a file called `nl.stokpop.eventscheduler.api.EventFactory`. 

And for the event schedule generators use
(e.g. `nl.stokpop.helloworld.event.StokpopEventGeneratorFactory`) 
a file called `nl.stokpop.eventscheduler.api.EventGeneratorFactory`.

Note that both can contain multiple lines with different implementations.
 
