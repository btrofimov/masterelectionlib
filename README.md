masterelectionlib
=================

Library for distributed applications with multiple nodes to control and auto-elect master node and check if current node is master.
Current library contains two engine implementations based on Hazelcast.
JMS and Zookeeper engines will be added soon.

## Integration
The integration with hosted application is similar to slf4j (adding core lib and corresponding dependency-engine):
```
compile("org.boro.melserv:melcore:0.1")
compile("org.boro.melserv:hazelcastimpl2:0.1")
```

## Usage
```
MELService service = new MELService();
// check if current node is master
service.isMaster();
```

## Customization
Sometimes for some mel engines like Hazelcast there is need to use external HazelcastInstance object.
For this purpose it is possible to populate Map with corresponding fields (exact params list depends on particular mel engine):
```
Map<String,Object> params = ...

params.put(HAZELCAST_INSTANCE, new Hazelcast.getHazelcastInstance());
params.put(THREAD_DELAY, 5);

MELService service = new MELService(params);
```
