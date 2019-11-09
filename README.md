# test-events-hello-world

This project shows an example implementation of the `Event` interface
and the `EventGenerator` interface.

The `StokpopHelloEvent` shows when events are called in `System.out`.
Also prints out the env variables and the `EventProperties` in 
the start test event call.

The `StokpopEventGenerator` shows how to create a custom list of events from
a generator class with properties. 

## event-scheduler

Add a dependency to the event-scheduler jar, just for compile.

Example:

```xml
<dependencies>
   <dependency>
       <groupId>nl.stokpop</groupId>
       <artifactId>event-scheduler</artifactId>
       <version>2.0.0</version>
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
 
