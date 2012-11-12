# raven-grails

raven-grails is a Grails client for integrating apps with [Sentry](http://www.getsentry.com).

## Installation

Clone the repository and build the plugin:

    $ git@github.com:informant-army/raven-grails.git
    $ cd raven-grails
    $ grails package-plugin

Copy the generated .zip to your applications /lib directory add the following to your `BuildConfig.groovy`:

```groovy
compile ":sentry:0.2"
```

## Configuration

You need to provide your Sentry DSN and activate notifications for environments in `Config.groovy` file:

```groovy
grails.plugins.sentry.dsn = "https://{PUBLIC_KEY}:{SECRET_KEY}@app.getsentry.com/{PATH}{PROJECT_ID}"

environments {
    test {
        grails.plugins.sentry.active = false
    }
    development {
        grails.plugins.sentry.active = false
    }
    production {
        grails.plugins.sentry.active = true
    }
}
```

## Usage

### Log4j Appender

The Log4j Appender is automatically configured by plugin, you have just to set enabled environments in `Config.groovy` file as shown in Configuration section. All application exceptions will be logged on sentry by the appender. The appender is configured to log just the ERROR, WARN and FATAL levels. To log manually just use the `log.error` method.

### SentryService

You also can use the sentryService to sent info messages to Sentry:

```groovy
import grails.plugins.sentry.SentryService

def sentryService

sentryService.logInfo(String message)
sentryService.logMessage(String message, String loggerClass, String logLevel)
sentryService.logException(Throwable exception)
sentryService.logException(Throwable exception, String loggerClass, String logLevel)
```
