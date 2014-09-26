masterelectionlib
=================

Library for distributed applications with multiple nodes to control master node and check if current node is master.
There are two implementations based on Hazelcast.
JMS and Zookeeper implementations will be added soon.

## Integration
The integration with hosted application is similar to slf4j (adding core lib and corresponding dependency-implementation):
```
compile("org.boro.melserv:melserv:0.1")
compile("org.boro.melserv:hazelcastimpl2:0.1")
```

## Usage
```
MELService service = new MELService();
// check if current node is master
service.isMaster();
```

## Customization
For some implementation engines like Hazelcast sometimes there is need to use external HazelcastInstance object.
For this purpose it is possible to override HazelcastFactory method:
```
MELEngine.setHazelcastFactory()
```
