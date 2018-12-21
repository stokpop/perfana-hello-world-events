# perfana-hello-world-events

Example implementation of the PerfanaTestEvent interface.

Shows when events are called in System.out.

Also prints out the env variables and the PerfanaEventProperties in 
the start test event call.

## META-INF/services

The magic happens when you add the appropriate file
in META-INF/services. See the sample in this project.

You need to specify the fully qualified name of your implementation
in a file called `nl.stokpop.perfana.event.StokpopHelloPerfanaEvent`. 