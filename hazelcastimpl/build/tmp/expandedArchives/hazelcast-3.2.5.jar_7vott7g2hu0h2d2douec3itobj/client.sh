#!/bin/sh

java -server -Djava.net.preferIPv4Stack=true -cp ../lib/hazelcast-client-3.2.5.jar:../lib/hazelcast-3.2.5.jar com.hazelcast.client.examples.ClientTestApp