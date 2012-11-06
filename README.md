raven-grails
============

raven-grails is a Grails client for Sentry.

Installation
------------

Clone the repository and build the plugin:

    $ git@github.com:informant-army/raven-grails.git
    $ cd raven-grails
    $ grails package-plugin

Copy the generated .zip to your applications /lib directory and add the plugin to applications BuildConfig.groovy:

```groovy
compile ":sentry:0.1"
```

Add your Sentry DSN to Config.groovy:

```groovy
grails.plugins.sentry.dsn = "https://{PUBLIC_KEY}:{SECRET_KEY}@app.getsentry.com/{PATH}{PROJECT_ID}"
```

And configure the ExceptionHandler on grails-app/conf/spring/resources.groovy:

```groovy
import grails.plugins.sentry.exception.handler.SentryExceptionResolver

beans = {
    exceptionHandler(SentryExceptionResolver) {
        exceptionMappings = ['java.lang.Exception': '/error']
    }
}
```

You can also use the SentryService:

```groovy
import grails.plugins.sentry.SentryService

SentryService sentryService

sentryService.logInfo(String message)
sentryService.logMessage(String message, String loggerClass, String logLevel)
sentryService.logException(Throwable exception)
sentryService.logException(Throwable exception, String loggerClass, String logLevel)
```

Log4j Appender
--------------

To use the Log4j Appender, configure the appenter on log4j.appenders in the Config.groovy:

```groovy
appender name:'sentry', new grails.plugins.sentry.SentryAppender()
```

And set the log level on log4j.root, e.g. error:

```groovy
error 'sentry'
```

TODO
----

* Implement sentry.interfaces.User (0.2)
* Update to Grails 2.x (0.3)
* Remove raven-java dependency (0.4)
* sentry.interfaces.Template (0.5)
* sentry.interfaces.Query (0.6)
