## This directory contains Camel related demos

### Camel-Fabric-Master-Component/
uses the Camel Fabric master component to create an exclusive consumer endpoint


### Camel-HTTP-Proxy
quick demo that show how to proxy HTTP requests. 


### Camel-HornetQ-AMQ
routes messages from an external HornetQ broker to an embedded ActiveMQ broker 
(non transactional)

### Camel-JMS-LocalTX
consumes messages from one JMS broker and optionally routes to another JMS broker
using local JMS transactions only. 

### Camel-JMS-JDBC 
shows how to route messages in Camel from a JMS broker into a JDBC database.


### Camel-JMS-JDBC-XA-TX 
similar to above demo but using XA transactions. To be used on 
JBoss Fuse 6.1 or higher


### Camel-JMS-JDBC-XA-TX-JBossFuse6.0
as above but specifically for JBoss Fuse 6.0 to workaround bug ENTESB-633
https://issues.jboss.org/browse/ENTESB-633


### Camel-WMQ-AMQ-XA-TX
shows how to route messages in Camel using XA transactions from WebSphere MQ
to ActiveMQ.


## Camel-Jetty-Basic-Auth-Demo
programmatically configures a camel-jetty endpoint for HTTP Basic Authentication 
against the JAAS authentication realm provided by the Karaf OSGi container.

### Camel-Jetty-Client-IP
Is a simple Camel JUnit test that shows how to extract the client's IP address 
in a Camel route that consumes from a Jetty HTTP endpoint.

