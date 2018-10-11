embedded-jms-junit
=================

[![Build Status](https://travis-ci.org/zapodot/embedded-jms-junit.svg)](https://travis-ci.org/zapodot/embedded-jms-junit)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.zapodot/embedded-jms-junit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.zapodot/embedded-jms-junit)
[![Apache V2 License](http://img.shields.io/badge/license-Apache%20V2-blue.svg)](//github.com/zapodot/embedded-jms-junit/blob/master/LICENSE)
[![Libraries.io for GitHub](https://img.shields.io/librariesio/github/zapodot/embedded-jms-junit.svg)](https://libraries.io/github/zapodot/embedded-db-junit)
[![Coverage Status](https://coveralls.io/repos/github/zapodot/embedded-jms-junit/badge.svg?branch=master)](https://coveralls.io/github/zapodot/embedded-jms-junit?branch=master)

[JUnit](http://junit.org/) Rule that provides a [ActiveMQ Embedded in-memory JMS Broker](http://activemq.apache.org/). 
It should be compatible with all the usual JMS integration tools such as Apache Camel and Spring JMS Template. Inspired by the [Embedded DB JUnit Rule](//github.com/zapodot/embedded-db-junit) project.

## Why?
* because you want to test your JMS integration code without being dependent on a running external JMS Broker
* setting up the broker manually for all your tests requires a lot of boilerplate code
* I found myself repeating myself and decided that creating this library would make my life easier :-)

## Status
This library is distributed through the [Sonatype OSS repo](https://oss.sonatype.org/) and should thus be widely available. Java 8 or higher is required.
Feedback is more than welcome. Feel free to create issues if you find bugs or have feature requests for future releases :-)

## Changelog
* version 0.2: rewritten core and added support for JUnit 5 Jupiter
* version 0.1: initial release

# Usage
## JUnit 5 (v. 0.2+)
More information on JMS support is found in the [project WIKI](//github.com/zapodot/embedded-jms-junit/wiki/Using-with-JUnit-5-Jupiter)
### Add dependency
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-jms-junit5</artifactId>
    <version>0.2</version>
    <scope>test</scope>
</dependency>
```
### Use ExtendWith to enable the extension and inject the connection factory using @EmbeddedJms
```java 
@ExtendWith(EmbeddedJmsBroker.class)
class EmbeddedJmsBrokerRequestReplySpringTest {

    private static final String TEST_MESSAGE = "Test message";

    private static final String DESTINATION = "queue:destination";

    /**
    *  The type of property must be either ConnectionFactory, ActiveMQFactory or URI.
    *  If it is a URI to the broker is injected
    */
    @EmbeddedJms 
    private ConnectionFactory connectionFactory;

    @DisplayName("My test")
    @Test
    void testJmsLogic() throws Exception {
       // make JMS magic
    }
    
    @DisplayName("parameterized test")
    @Test
    void connectionFactoryParameter(@EmbeddedJms ConnectionFactory connectionFactory) {
        // perform JMS magic
    }

}
``` 
## JUnit 4
### Add dependency
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-jms-junit</artifactId>
    <version>0.2</version>
    <scope>test</scope>
</dependency>
```

### Add to JUnit4 test
```java
    @Rule
    public EmbeddedJmsRule embeddedJmsRule = EmbeddedJmsRule.builder().build();

    @Test
    public void jmsTest() throws Exception {
        final ConnectionFactory connectionFactory = embeddedJmsRule.connectionFactory();
        
        // work your JMS magic

    }
```
For more examples check the JUnit tests in this project
