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
       <version>1.0.0</version>
       <scope>compile</scope>
   </dependency>
</dependencies>
```

Then you are able to implement the `Event` and the `EventGenerator` interface.

## add services file

The magic happens when you add the appropriate file
in `META-INF/services`. See the sample in this project.

You need to specify the fully qualified name of your implementation
(e.g. `nl.stokpop.helloworld.event.StokpopHelloEvent`) 
in a file called `nl.stokpop.eventscheduler.event.Event`. 

And for the event schedule generators use
(e.g. `nl.stokpop.helloworld.event.StokpopEventGenerator`) 
a file called `nl.stokpop.eventscheduler.event.EventGenerator`.

Note that both can contain multiple lines with different implementations.
 
