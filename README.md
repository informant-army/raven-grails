Raven Grails Plugin
===================

[![Build Status](https://secure.travis-ci.org/agorapulse/grails-raven.png?branch=master)](https://travis-ci.org/agorapulse/grails-raven)

# Introduction

Raven is a Grails client for integrating apps with [Sentry](http://www.getsentry.com). 
[Sentry](http://www.getsentry.com) is an event logging platform primarily focused on capturing and aggregating exceptions.

It uses the official [Raven-java](https://github.com/getsentry/raven-java) client under the cover.

# Installation

Declare the plugin dependency in the _build.gradle_ file, as shown here:

```groovy
repositories {
    ...
    maven { url "http://dl.bintray.com/agorapulse/plugins" }
}
dependencies {
    ...
    compile "org.grails.plugins:raven:6.0.0"
}
```

# Config

Add your Sentry DSN to your _grails-app/conf/application.yml_.

```yml
grails:
    plugin:
        raven:
            dsn: https://{PUBLIC_KEY}:{SECRET_KEY}@app.getsentry.com/{PATH}{PROJECT_ID}
```

The plugin will sent notifications to Sentry by default, if you want to disable notifications for an specific environment set the `active` option as false.

```yml
environments:
    development:
        grails:
            plugin:
                raven:
                    active: false
    test:
        grails:
            plugin:
                raven:
                    active: false
```

You can also configure the multiple logger to which you want to append the sentry appender.
You can also set the server name, but it is recommended to don't set this configuration and let the plugin to resolve it.


## Optional configurations

```yml
# Not tested on Grails 3 plugin...
grails:
    plugin:
        raven:
            loggers: [LOGGER1, LOGGER2, LOGGER3]
            serverName: dev.server.com
            levels: [ERROR, FATAL]
            tags: {tag1: val1,  tag2: val2, tag3: val3}
            subsystems: 
                MODULE1: [com.company.services.module1, com.company.controllers.module1]
                MODULE2: [com.company.services.module2, com.company.controllers.module2]
                MODULE3: [com.company.services.module3, com.company.controllers.module3]
            logClassName: true
            priorities: 
                HIGH: [java.lang, com.microsoft.sqlserver.jdbc.SQLServerException]
                MID: [com.company.exception]
                LOW: [java.io]
```

Check [Raven-java](https://github.com/getsentry/raven-java) documentation to configure connection, protocol and async options in your DSN. If you are sending extra tags from the plugin for the exceptions, make sure to enable the corresponding tag on sentry tag settings for the particular project to see the tag as a filter on the exception stream on sentry.


# Usage

## Logback Appender

The Logback Appender is automatically configured by the plugin, you just have to set enabled environments as shown in Configuration section.

All application exceptions will be logged on sentry by the appender.
The appender is configured to log just the `ERROR`, `WARN` and `FATAL` levels.
To log manually just use the `log.error()` method.

## ravenClient

You also can use `raven` client to sent info messages to Sentry:

```groovy
import net.kencochrane.raven.Raven
import net.kencochrane.raven.event.Event
import net.kencochrane.raven.event.EventBuilder

Raven raven // To inject Spring bean raven client in your controllers or services

// Send simple message
raven?.sendMessage("some message")

// Send exception
raven?.sendException(new Exception("some exception"))

// Custom event
EventBuilder eventBuilder = new EventBuilder(
        message: "Hello from Raven!",
        level: Event.Level.ERROR,
        logger: TestController.class.name
).addSentryInterface(
        new ExceptionInterface(
                new Exception("some exception")
        )
)
raven?.sendEvent(eventBuilder.build())
```

# Latest releases

* 2015-08-31 **V6.0.0** : initial release for Grails 3.x.

# Bugs

To report any bug, please use the project [Issues](http://github.com/agorapulse/grails-raven/issues) section on GitHub.
