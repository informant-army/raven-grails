# Raven-Grails

[![Build Status](https://secure.travis-ci.org/agorapulse/grails-raven.png?branch=master)](https://travis-ci.org/agorapulse/grails-raven)

Raven is a Grails client for integrating apps with [Sentry](http://www.getsentry.com). [Sentry](http://www.getsentry.com) is an event logging platform primarily focused on capturing and aggregating exceptions.

It uses the official [Raven-java](https://github.com/getsentry/raven-java) client under the cover.

## Installation

Add the following to your `BuildConfig.groovy`:

```groovy
plugins {
    compile ":raven:6.0.0"
}
```

## Configuration

You need to provide your Sentry DSN in `Config.groovy` file. The plugin will sent notifications to Sentry by default, if you want to disable notifications for an specific environment set the active option as false.
You can also configure the logger to which you want to append the sentry appender.

```groovy
grails.plugin.raven.dsn = "https://{PUBLIC_KEY}:{SECRET_KEY}@app.getsentry.com/{PATH}{PROJECT_ID}"

environments {
    test {
        grails.plugin.raven.active = false
    }
    development {
        grails.plugin.raven.active = false
    }
    production {
    }
}
// optional
grails.plugin.raven.logger = "LOGGER_NAME"
```

Check [Raven-java](https://github.com/getsentry/raven-java) documentation to configure connection, protocol and async options in your DSN.


## Usage

### Log4j Appender

The Log4j Appender is automatically configured by plugin, you have just to set enabled environments in `Config.groovy` file as shown in Configuration section.
All application exceptions will be logged on sentry by the appender.
The appender is configured to log just the ERROR, WARN and FATAL levels.
To log manually just use the `log.error` method.

### ravenClient

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

* 2015-01-29 **V6.0.0** : major refactor to use the official [Raven-java](https://github.com/getsentry/raven-java) client

**WARNING**: Breaking change, since V6.0.0, if you were using the legacy groovy-based `ravenClient` spring bean, you must replace it with the new java-based `raven` client spring bean and `sendMessage` or `sendException` methods.

* 2014-03-08 **V0.5.8** : PR by jglapa for improved user data handling + bug fix
* 2014-03-05 **V0.5.7** : PR by Logicopolis for async execution
* 2014-01-03 **V0.5.4** : PR by benorama to remove commons codec Base64 dependency when building message body (for Grails 2.3 compatibility)
* 2012-12-12 **V0.5** : refactoring
* 2012-12-10 **V0.4** : sentryClient Spring Bean
* 2012-11-23 **V0.2** : user interface + bug fixes
* 2012-10-29 **V0.1** : initial release

# Bugs

To report any bug, please use the project [Issues](http://github.com/agorapulse/grails-raven/issues) section on GitHub.