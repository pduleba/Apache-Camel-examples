## Camel-WMQ-AMQ-XA-TX Demo


## This demo needs some refactoring any may not work against latest WMQ servers 
V
erified and tested against JBoss Fuse 6.1.


A Camel demo that shows how to use XA transactions with Camel
across two different JMS providers, ActiveMQ and WebSphere MQ.
ActiveMQ is embedded in JBoss Fuse ESB however a WebSphere MQ server
is not provided with this demo.
You need to have access to an external WebSphereMQ Series broker in order to 
run this demo. 


In this demo a Camel route consumes messages from a JMS queue IN
on the external WebSphere MQ broker, routes the message to a Camel 
processor and further to the embedded ActiveMQ broker on queue OUT.
The Camel route further calls a custom processor which can optionally 
be configured to raise an exception to illustrate a transaction rollback 
while processing the message
Out of the box the custom Camel processor won't raise an exception.


The Camel route simply reads:
```xml
    <camelContext xmlns="http://camel.apache.org/schema/blueprint">
      <route id="wmq-to-amq">
        <from uri="wmqxa:IN" />
        <transacted ref="requiredJta" /> 
        <log message="GOT MESSAGE ################################" />
        <log message="REDELIVERED: ${header.JMSRedelivered}" />
        <to uri="amqxa:queue:OUT" />
        <process ref="testProcessor" />
        <log message="AFTER CAMEL PROCESSOR ######################" />        
      </route>
    </camelContext>
```

This demo is not a JUnit test and can also not be run using the maven-camel-plugin 
(mvn camel:run)! In order to use XA it needs to be deployed into JBoss Fuse!

There is no JMS producer provided with this demo. In order to push messages onto 
the queue IN on WebSphere MQ I suggest to use the WebSphere MQExplorer console, 
or any other external tool (e.g. HermesJMS).

In order to connect to a WebSphere MQSeries broker, its necessary to deploy the WMQ
Java drivers into JBoss Fuse. The steps needed are shown below.

This demo contains two subdirectories:
- routing/ contains the Camel route and JMS endpoint configuration.
  Check out the files
  routing/src/main/resources/OSGI-INF/blueprint/camel-context.xml
  routing/src/main/java/com/fusesource/support/TestProcessor.java

- features/ contains an OSGi feature definition in order to deploy the 
  Camel route and all its dependencies in one go. Check out the file
  features/src/main/resources/features.xml


### Important things to notice about XA transactions:
2) There are two separate camel-jms component configurations used in this demo.
   One component uses the embedded ActiveMQ broker listening on 
   localhost:616161, the other connects to an external WebSphere MQSeries 
   instance. This is necessary for really testing XA transactions and two 
   phase commits. If only one resource was used it would not result in a 
   two phase commit.

3) In order to recover pending transactions from a crash or restart of the ESB
   you need to register a ResourceManager for *every* transactional resource
   (i.e. every JMS provider). Check the two blueprint bean definitions with 
   'resourceManager-AMQ' and 'resourceManager-WMQ'.

4) IBM WebSphereMQ does not provide any such resource manager. In addition,
   the Aries transaction manager (a wrapper around the Geronimo transaction
   manager) will write the participants of an XA transaction to its internal
   log file so that it can recover the transaction after a crash.
   So for the transaction log to be written correctly (which is a pre-
   requisite for recovering transactions after a crash), every XA resource needs
   to implement the org.apache.geronimo.transaction.manager.NamedXAResource interface.
   This is a proprietary interface and the WebSphereMQ JMS driver does not 
   implement it, nor will other third party JMS providers necessarily implement
   this interface. Note the ActiveMQ PooledConnectionFactory does implement it.
   There is a generic JMS ConnectionFactory provided by Jencks, but it also does
   not support the NamedXAResource interface.
   As a result, we created ENTESB-159 to write a generic JMS ConnectionFactory
   that supports pooling and also the NamedXAResource interface. 
   This was the start of the jmspool project hosted a github.
   https://github.com/fusesource/jmspool.
   As of ActiveMQ 5.9.0 this jmspool project was moved to the ActiveMQ source 
   code base and can be found in the activemq-jms-pool directory of the source
   tree.
   In order to have WebSphereMQ participating fully into the XA transaction,
   you need to wrap the WMQ JMS driver into this generic ConnectionFactory.  
   This is done in this demo, see camel-context.xml.

5) The name property assigned to the JcaPooledConnectionFactory needs to match the name assigned 
   to the ResourceManager. E.g. 

   <bean id="XAPooledCF" class="org.apache.activemq.pool.JcaPooledConnectionFactory">
      <property name="name" value="activemq.default" />

   uses the name="activemq.default", which is the same name as defined in 
   etc/activemq-broker.xml:

   <bean id="resourceManager" class="org.apache.activemq.pool.ActiveMQResourceManager" init-method="recoverResource">
          <property name="resourceName" value="activemq.default" /> 

   If the names don't match, transaction recovery will not work correctly.




## COMPILING:

mvn clean install


## Deploying

- Edit `src/main/resources/OSGI-INF/blueprint/camel-context.xml` of this demo
  and configure the bean with id=WMQConnectionFactory to match your WebSphereMQ 
  broker configuration and address.

 - Edit `features/src/main/resources/features.xml` and update the location of 
   your IBM WMQ Series Java driver jar files. These files are not provided 
   with the demo.
   I.e. change the file location of all bundles starting with 
   `<bundle>file:/Users/tmielke/Desktop/sandbox/WebSpherePackages/MQSeries/WMQ-JavaDriver/...</bundle>`
   The required client side IBM WMQ jar files are typically located inside your 
   WMQ installation folder in java/lib/OSGi. You need to install pretty much all
   of these files as listed in this features.xml.

- Start your WebSphere MQSeries broker and ensure the broker details match the
  configuration supplied in camel-context.xml of this demo. 

- Start JBoss Fuse 6.1

- From the Karaf shell enter:
  ```
  features:addurl  mvn:org.apache.camel.demo.camel-wmq-amq-xa-tx/features/1.0.0/xml/features
  features:install camel-wmq-amq-xa-tx-demo
  ```

- Check that all bundles get deployed and start up successfully. 



## Running

- Send a test message to the queue 'IN' on WebSphere MQ.

- Observe the JBoss Fuse log file and verify that all messages got routed to 
  the embedded ActiveMQ broker, destination OUT. You can also connect to the
  ActiveMQ broker using JMX and verify that messages got enqueued to queue IN.

- If you like to see proper logging about each XA begin/commit, then
  set the following logging configuration:

```
  log:set DEBUG org.apache.activemq.transaction
  log:set DEBUG org.springframework.transaction
  log:set DEBUG org.springframework.jms.connection.JmsTransactionManager
  log:set DEBUG log4j.logger.org.springframework.orm.jpa.JpaTransactionManager
  log:set DEBUG log4j.logger.org.springframework.transaction.jta.JtaTransactionManager
  log:set DEBUG org.jencks
  log:set TRACE org.apache.geronimo.transaction.manager.WrapperNamedXAResource
  log:set DEBUG org.apache.activemq.TransactionContext
  log:set DEBUG org.apache.geronimo.transaction.log
  log:set DEBUG org.apache.activemq.jms.pool
```



###For testing Aries transaction recoverability:

- Start JBoss Fuse 6.0 using this simple loop from command line:
  `while [ 1=1 ]; do ./fuse ;  sleep 10; done`

- Send 10000 msgs to IN on WMQ.
  The Message content does not matter.

- Check the ActiveMQ broker's OUT queue, messages should get routed rather slowly 
  to that destination.

- In a shell window run `./kill-script.sh` to kill JBoss Fuse 10 times every two 
  minutes. On the restart of JBoss Fuse it will recover any pending transactions 
  from the transaction manager recovery log.

- Allow all msgs to be routed to the ActiveMQ broker and double check that 
  queue OUT receives exactly 100000 messages despite killing JBoss Fuse, the 
  Camel route and the embedded ActiveMQ broker a couple of times.

