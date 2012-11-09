# raven-grails

raven-grails is a Grails client for integration apps with [Sentry](http://www.getsentry.com).

## Installation

Clone the repository and build the plugin:

    $ git@github.com:informant-army/raven-grails.git
    $ cd raven-grails
    $ grails package-plugin

Copy the generated .zip to your applications /lib directory add the following to your `BuildConfig.groovy`:

```groovy
compile ":sentry:0.1"
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

To let raven-grails catch all the exceptions in your applicaiton, configure the `ExceptionHandler` in `grails-app/conf/spring/resources.groovy` file:

```groovy
import grails.plugins.sentry.exception.handler.SentryExceptionResolver

beans = {
    exceptionHandler(SentryExceptionResolver) {
        exceptionMappings = ['java.lang.Exception': '/error']
    }
}
```

## Usage

If you configured the exception handler, all the Exceptions will be send to Sentry. You can also use the plugin as follows:

### Log4j Appender

The Log4j Appender is automatically configured by plugin, you have just to set the enabled environments in `Config.groovy` file as shown in Configuration section.

### SentryService

```groovy
import grails.plugins.sentry.SentryService

def sentryService

sentryService.logInfo(String message)
sentryService.logMessage(String message, String loggerClass, String logLevel)
sentryService.logException(Throwable exception)
sentryService.logException(Throwable exception, String loggerClass, String logLevel)
```
