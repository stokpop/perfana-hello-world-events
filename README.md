# test-events-hello-world

This project shows an example implementation of the `Event` interface
and the `EventGenerator` interface.

The `StokpopHelloEvent` shows when events are called in `System.out`.
It also prints out some system information at the start.

The `StokpopEventGenerator` shows how to create a custom list of events from
a generator class with properties.

This event is an `isReadyForStartParticipant`. Only when this event reports
back a `Go!` message, the event-scheduler is allowed to start.
It will do so after waiting `helloInitialSleepSeconds` seconds.

## to try it out

If you just want to experiment with the `event-scheduler-maven-plugin` and
sub-plugins `test-events-hello-world` and `perfana-java-client`,
just add the `test-events-hello-world` and `perfana-java-client` plugin to the dependencies
of the `event-scheduler-maven-plugin`.

For instance add the following:

```xml
<plugins>
    <plugin>
        <groupId>nl.stokpop</groupId>
        <artifactId>event-scheduler-maven-plugin</artifactId>
        <configuration>
            <eventSchedulerConfig>
                <debugEnabled>true</debugEnabled>
                <schedulerEnabled>true</schedulerEnabled>
                <failOnError>true</failOnError>
                <continueOnEventCheckFailure>true</continueOnEventCheckFailure>
                <eventScheduleScript>
                    PT5S|restart|{ server:'myserver' replicas:2 tags: [ 'first', 'second' ] }
                    PT10S|scale-down
                    PT30S|heapdump|server=myserver.example.com;port=1567
                    PT1M|scale-up|{ replicas:2 }
                </eventScheduleScript>
                <testConfig>
                    <systemUnderTest>${systemUnderTest}</systemUnderTest>
                    <version>${version}</version>
                    <workload>${workload}</workload>
                    <testEnvironment>${testEnvironment}</testEnvironment>
                    <testRunId>${testRunId}</testRunId>
                    <buildResultsUrl>${buildResultsUrl}</buildResultsUrl>
                    <rampupTimeInSeconds>${rampupTimeInSeconds}</rampupTimeInSeconds>
                    <constantLoadTimeInSeconds>${constantLoadTimeInSeconds}</constantLoadTimeInSeconds>
                    <annotations>${annotations}</annotations>
                    <tags>${tags}</tags>
                </testConfig>
                <scheduleScript>
                    ${eventScheduleScript}
                </scheduleScript>
                <eventConfigs>
                    <eventConfig implementation="nl.stokpop.helloworld.event.StokpopHelloEventConfig">
                        <name>StokpopHelloEvent1</name>
                        <helloInitialSleepSeconds>40</helloInitialSleepSeconds>
                        <myRestService>https://my-rest-api</myRestService>
                        <myCredentials>${ENV.SECRET}</myCredentials>
                    </eventConfig>
                    <eventConfig implementation="io.perfana.event.PerfanaEventConfig">
                        <name>PerfanaEvent1</name>
                        <perfanaUrl>http://localhost:8888</perfanaUrl>
                        <assertResultsEnabled>false</assertResultsEnabled>
                        <variables>
                            <_var1>my_value</_var1>
                        </variables>
                    </eventConfig>
                </eventConfigs>
            </eventSchedulerConfig>
        </configuration>
        <dependencies>
            <dependency>
                <groupId>nl.stokpop</groupId>
                <artifactId>test-events-hello-world</artifactId>
                <version>${test-events-hello-world.version}</version>
            </dependency>
            <dependency>
                <groupId>io.perfana</groupId>
                <artifactId>perfana-java-client</artifactId>
                <version>${perfana-java-client.version}</version>
            </dependency>
        </dependencies>
    </plugin>
</plugins>
```

See also: 
* https://github.com/stokpop/event-scheduler-maven-plugin
* https://github.com/stokpop/event-scheduler
*  

## create your own plugin

Add a dependency to the `event-scheduler` jar, just for compile.

Example:

```xml
<dependencies>
   <dependency>
       <groupId>nl.stokpop</groupId>
       <artifactId>event-scheduler</artifactId>
       <version>3.0.0</version>
       <scope>compile</scope>
   </dependency>
</dependencies>
```

Then you can create your own implementations of the `Event` and the `EventFactory` interface.
The same goes for the `EventGenerator` and the `EventGeneratorFactory` interface.

For convenience, you can use the `EventAdapter` abstract class 
to implementing `Event` interface. Only implement the method you want to override.

Create an `*EventConfig` calls for the configuration with only setters.
Create an immutable `*EventContext` class with only getter.
In the EventConfig class override the 2 `toContext` methods.

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
 
