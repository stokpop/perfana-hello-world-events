# perfana-hello-world-events

This project shows an example implementation of the `PerfanaEvent` interface
and the `EventScheduleGenerator` interface.

The `StokpopHelloPerfanaEvent` shows when events are called in `System.out`.
Also prints out the env variables and the `EventProperties` in 
the start test event call.

The `StokpopEventGenerator` shows how to create a custom list of events from
a generator class with properties. 

## perfana-java-client

Add a dependency to the perfana-java-client jar, just for compile.

Example:

```xml
<dependencies>
   <dependency>
       <groupId>io.perfana</groupId>
       <artifactId>perfana-java-client</artifactId>
       <version>1.3.0</version>
       <scope>compile</scope>
   </dependency>
</dependencies>
```

Then you are able to implement the `PerfanaEvent` and the 
`EventScheduleGenerator` interface.

## add services file

The magic happens when you add the appropriate file
in `META-INF/services`. See the sample in this project.

You need to specify the fully qualified name of your implementation
(e.g. `nl.stokpop.perfana.event.StokpopHelloPerfanaEvent`) 
in a file called `io.perfana.event.PerfanaEvent`. 

And for the event schedule generators use
(e.g. `nl.stokpop.perfana.event.StokpopEventGenerator`) 
a file called `io.perfana.event.EventScheduleGenerator`.

Note that both can contain multiple lines with different implementations.
 
