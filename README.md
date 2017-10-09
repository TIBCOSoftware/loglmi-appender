# LogLogic Java Logging Toolkit
## Using the LogLogic Java logging extensions
### Introduction

LogLogic Java logging extension supports the following logging backend:

* Java JDK Logging framework
* Log4j (1.x)
* Log4j 2 (2.x)
* Logback

LogLogic Java logging extension requires at least Java 7.

### Choosing between TCP or ULDP transport
  
Many logging framework are provided with a UDP Syslog implementation, which suffers some shortcomings for deployment in enterprise systems.

The appenders provided by LogLogic overcome those limitations, using two protocols. 

The standard Syslog over TCP (RFC 6587, RFC 5425), which works with LogLogic Log Management Intelligence (LMI) and is interoperable with other applications supporting the syslog framework.
This protocol uses a reliable transport, can support any message length that the receiver can accept, and can be optionally secured by a TLS encapsulation.

Also provided is an appender for Syslog/ULDP. ULDP is a protocol created by LogLogic to add some additional reliability to the exchange of log messages: the receiving end is sending periodic acknowledgements of messages received once they are processed properly. Messages sent and not yet acknowledged are put in a memory buffer, when the buffer is full forwarding is paused until an acknowledgement is received (this situation happens if the receiving end cannot cope well with the pace at which events are sent).

Comparison of the features of the protocols.

Property          | Syslog UDP | Syslog TCP | ULDP
----------------| -----------| -----------|-----
Messages>65k    |No          |Yes         |Yes
Acknowledgement of processing of the messages by receiver | No | No | Yes
Supports TLS encryption of connection|No|Yes|Yes

### Properties of the LogLogic appenders

Property Name|Default Value|Type|Syslog/TCP|Syslog/ULDP
-------------|-------------|-------|----------|------------
host         |             |String |X         |X
port|514|Integer|X|X
maxQueueSize|500kb|String|X|X
appName||String|X|X
source||String|X|X
rawMode|false|Boolean|X|X
facility|16|Integer|X|X
useCompression|false|Boolean|X|X
useTls|false|Boolean|X|X
useEncryption|true|Boolean|X|X
keystorePath||String|X|X
keystorePassword||String|X|X
tlsProtocolName|TLS|X|X
cipherSuite|(*)|String|X|X
domainName||String||X
noServerAuth|false|Boolean|X|X
useOctetCounting|False|Boolean|X|
soTimeout|0|Long|X|X
acceptedCertificateFingerpints|X|String|X|X
ignoreHostnameValidation|True(ULDP) False(TCP)|Boolean|X|X


(*): For Syslog/TCP and ULDP (with encryption), default is TLS_RSA_WITH_AES_128_CBC_SHA.

For the accepted certificate fingerprints, the list of the fingerprints of the accepted certificates are separated by ‘,’. 
The fingerprint is prepended with an ASCII label identifying the hash function followed by a colon.
Implementations MUST support SHA-1 as the hash algorithm and use the ASCII label "sha-1" to identify the SHA-1
algorithm. The length of a SHA-1 hash is 20 bytes and the length of the corresponding fingerprint string is 65
characters. An example certificate fingerprint is:
`sha-1:E1:2D:53:2B:7C:6B:8A:29:A2:76:C8:64:36:0B:08:4B:7A:F1:9E:9D`

**Note on best practice for appName field:**
We recommend you use a compound value for the appName field, that contains a protuct type and an app-name separated by an hyphen: `<productType>-<applicationName>`. This helps creating data model that are generic for a given product, while keeping the notion of
the application name available for further refinement.

## Use LogLogic appender with Java logging framework

Adding loglmi-appender-1.0.0.jar in the classpath of your application.

### Configuration

Set the JVM property `java.util.logging.config.file` to point to the configuration file.

The file is a standard property file. 
Properties for configuring a Syslog TCP connections are prefixed with :
com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.

Properties for configuration an ULDP connection are prefixed with:
com.tibco.loglogic.logging.handlers.jdk.UldpHandler.

Below is an example of a configuration file which defines two handlers, one for TCP, one for ULDP on their default port. The format of the log message can be defined using a SimpleFormater if necessary, as it is the case in the example.

````xml
# Properties file which configures the operation of the JDK
# logging facility. This file contains setting for Syslog TCP Appender

# The system will look for this config file, first using
# a System property specified at startup:
#
# >java -Djava.util.logging.config.file=myLoggingConfigFilePath
#
# If this property is not specified, then the config file is
# retrieved from its default location at:
#
# JDK_HOME/jre/lib/logging.properties

# The set of handlers to be loaded upon startup.
# Comma-separated list of class names.
handlers = com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler, com.tibco.loglogic.logging.handlers.jdk.UldpHandler

# Default global logging level.
# Loggers and Handlers may override this level
.level = INFO

# Set the default logging level for the special LMI logger
lmi.logger = INFO

# Set the default logging level for new SyslogTcpHandler instances
com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.level=INFO
com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.host=192.168.7.170
com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.port=514
com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.maxQueueSize=5MB
com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.appName=MyFancyApp
com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.source=MyMachine
com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.facility=16
com.tibco.loglogic.logging.handlers.jdk.SyslogTcpHandler.formatter= java.util.logging.SimpleFormatter
# Set the default logging level for new SyslogTcpHandler instances
com.tibco.loglogic.logging.handlers.jdk.UldpHandler.level=INFO
com.tibco.loglogic.logging.handlers.jdk.UldpHandler.host=192.168.5.5
com.tibco.loglogic.logging.handlers.jdk.UldpHandler.maxQueueSize=5MB
com.tibco.loglogic.logging.handlers.jdk.UldpHandler.appName=MyFancyAppUldp
com.tibco.loglogic.logging.handlers.jdk.UldpHandler.source=MyMachineUldp
com.tibco.loglogic.logging.handlers.jdk.UldpHandler.facility=16
com.tibco.loglogic.logging.handlers.jdk.UldpHandler.formatter= java.util.logging.SimpleFormatter

java.util.logging.SimpleFormatter.format = "%1$F %1$r %4$s: %6$s"
````

## Use LogLogic appenders with Log4j (1.x)

Adding loglmi-appender-1.0.0.jar in the classpath of your application.

### Configuration

By default, the LogManager looks for a file named `log4j.xml` in the CLASSPATH.

Below is an example of a configuration file, which defines two Appenders, one for TCP, one for ULDP on their default port. The format of the log message can be defined using a Layout if necessary, as it is the case in the example.

````xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration>

    <appender name="LoglogicAppender" class="com.tibco.loglogic.logging.appenders.log4j.SyslogTcpAppender">
        <param name="host" value="192.168.7.170"/>
        <param name="appName" value="myApp"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%-5p %c{1} - %m"/>
        </layout>
    </appender>

    <appender name="LoglogicUldp" class="com.tibco.loglogic.logging.appenders.log4j.UldpAppender">
        <param name="host" value="192.168.7.170"/>
        <param name="appName" value="myAppUldp"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%-5p %c{1} - %m"/>
        </layout>
    </appender>

    <root>
        <level value="INFO" />
        <appender-ref ref="LoglogicAppender" />
        <appender-ref ref="LoglogicUldp" />
    </root>

</log4j:configuration>
````

## Use LogLogic appenders with Log4j 2 (2.x)

Adding loglmi-appender-1.0.0.jar in the classpath of your application.

### Configuration

Log4j will inspect log4j.configurationFile system property to determine log4j2 configuration file. Log4j configuration can be written in JSON, YAML and XML

In case *no system property is defined* the configuration order takes below precedence:
1. Property ConfigurationFactory will look for `log4j2-test.properties` in the classpath.
1. YAML ConfigurationFactory will look for `log4j2-test.yaml` or `log4j2-test.yml` in the classpath.
1. JSON ConfigurationFactory will look for `log4j2-test.jsn` or `log4j2-test.json` in the classpath.
1. XML ConfigurationFactory will look for `log4j2-test.xml` in the classpath.
1. Property ConfigurationFactory will look for `log4j2.properties` on the classpath
1. YAML ConfigurationFactory will look for `log4j2.yml` or `log4j2.yaml` in the classpath.
1. JSON ConfigurationFactory will look for `log4j2.jsn` or `log4j2.json` in the classpath.
1. XML ConfigurationFactory will look for `log4j2.xml` in the classpath.

Below is an example of an XML configuration file, which defines two Appenders, one for TCP, one for ULDP on their default port. The format of the log message can be defined using a PatternLayout if necessary, as it is the case in the example.

````xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration packages="com.tibco.loglogic.logging.appenders.log4j2">
    <Appenders>
        <SyslogTcpAppender name="Loglogic"
                           host="192.168.7.170"
                           port="514"
                           appName="myApp">
            <PatternLayout pattern="%X %x %m"/>
        </SyslogTcpAppender>
        <UldpAppender name="LoglogicUldp"
                           host="192.168.7.170"
                           appName="myAppUldp">
            <PatternLayout pattern="%X %x %m"/>
        </UldpAppender>
    </Appenders>

    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="Loglogic"/>
            <AppenderRef ref="LoglogicUldp"/>
        </Root>
    </Loggers>
</Configuration>
````

## Using LogLogic appenders with logback

Adding loglmi-appender-1.0.0.jar in the classpath of your application.

### Configuration

1. Logback tries to find a file called `logback-test.xml` in the classpath.
1. If no such file is found, logback tries to find a file called `logback.groovy` in the classpath.
1. If no such file is found, it checks for the file `logback.xml` in the classpath..
1. If no such file is found, service-provider loading facility (introduced in JDK 1.6) is used to resolve the implementation of `com.qos.logback.classic.spi.Configurator` interface by looking up the file `META-INF\services\ch.qos.logback.classic.spi.Configurator` in the class path. Its contents should specify the fully qualified class name of the desired Configurator implementation. 

Below is an example of a configuration file, which defines two Appenders, one for TCP, one for ULDP on their default port. The format of the log message can be defined using a layout if necessary, as it is the case in the example.

````xml
<configuration>
    <appender name="loglogic" class="com.tibco.loglogic.logging.appenders.logback.SyslogTcpAppender">
        <host>192.168.7.170</host>
        <Port>514</Port>
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>[%thread] %level: %msg</pattern>
        </layout>
        <source>MySource</source>
        <appName>MyApp</appName>
    </appender>

    <appender name="uldp" class="com.tibco.loglogic.logging.appenders.logback.UldpAppender">
        <host>192.168.5.5</host>
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>[%thread] %level: %msg</pattern>
        </layout>
        <source>MySourceForULDP</source>
        <appName>MyAppForULDP</appName>
    </appender>

    <logger name="uldp.logger" additivity="false" level="INFO">
        <appender-ref ref="uldp"/>
    </logger>

    <root level="INFO">
        <appender-ref ref="uldp"/>
    </root>

</configuration>
````

## Using the LmiLogEvent class

The toolkit also provide a helper class to generate well-formatted log messages, using a pre-defined set of properties that advanced parsers in LMI natively understands.

Logs are easier to query and act upon with correlation rules and reports if they share a common set of properties. The LogLogic LogLab team has came up with a pre-defined set of properties that suits a broad range of log messages from various kinds of product. This is however extensible to suit your own needs, so you can define your own attributes.

We recommend that each log message is associated with an eventName and eventId that will help quickly extract the semantic of each log.

Here is a typical use of that class:

````java
Logger logger = LoggerFactory.getLogger(...);
LmiLogEvent event = new LmiLogEvent("Failed Login","sshd:failure");
event.setTargetUser("joe");
event.addKVP("myOwnFieldName", "some value");
logger.info(event.toString());
````
