embedded-jms-junit
=================

[![Build Status](https://travis-ci.org/zapodot/embedded-jms-junit.svg)](https://travis-ci.org/zapodot/embedded-jms-junit)
[![Apache V2 License](http://img.shields.io/badge/license-Apache%20V2-blue.svg)](//github.com/zapodot/embedded-jms-junit/blob/master/LICENSE)
[![Libraries.io for GitHub](https://img.shields.io/librariesio/github/zapodot/embedded-jms-junit.svg)](https://libraries.io/github/zapodot/embedded-db-junit)
[![Coverage Status](https://coveralls.io/repos/github/zapodot/embedded-jms-junit/badge.svg?branch=master)](https://coveralls.io/github/zapodot/embedded-jms-junit?branch=master)

[JUnit](http://junit.org/) Rule that provides a [ActiveMQ Embedded in-memory JMS Broker](http://activemq.apache.org/). 
It should be compatible with all the usual JMS integration tools such as Apache Camel and Spring JMS Template.

## Why?
* because you want to test your JMS integration code without being dependent on a running external JMS Broker
* setting up the broker manually for all your test leads requires a lot of boilerplate code 

## Status
This library will in the future be distributed through the [Sonatype OSS repo](https://oss.sonatype.org/) and should thus be widely available. Java 8 or higher is required.

## Changelog
* version 0.1: initial release

## Usage
### Add dependency
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-jms-junit</artifactId>
    <version>0.1</version>
    <scope>test</scope>
</dependency>
```

#### SBT
```scala
libraryDependencies += "org.zapodot" % "embedded-jms-junit" % "0.1" % "test"
```

### Add to Junit test
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
