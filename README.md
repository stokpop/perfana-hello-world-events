# perfana-hello-world-events

Example implementation of the `PerfanaTestEvent` interface.

Shows when events are called in `System.out`.

Also prints out the env variables and the `PerfanaEventProperties` in 
the start test event call.

## add dependency to perfana-java-client

Add a dependency to the perfana-java-client jar, just for compile.

Example:

```xml
<dependencies>
   <dependency>
       <groupId>io.perfana</groupId>
       <artifactId>perfana-java-client</artifactId>
       <version>1.2.1</version>
       <scope>compile</scope>
   </dependency>
</dependencies>
```

Then you are able to implement the `PerfanaTestEvent`.

## add services file

The magic happens when you add the appropriate file
in `META-INF/services`. See the sample in this project.

You need to specify the fully qualified name of your implementation
(e.g. `nl.stokpop.perfana.event.StokpopHelloPerfanaEvent`) 
in a file called `io.perfana.event.PerfanaTestEvent`. 

