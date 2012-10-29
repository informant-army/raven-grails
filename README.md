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

    compile ":sentry:0.1"

Add your Sentry DSN to Config.groovy:

    grails.plugins.sentry.dsn = "https://{PUBLIC_KEY}:{SECRET_KEY}@app.getsentry.com/{PATH}{PROJECT_ID}"

And configure the ExceptionHandler on grails-app/conf/spring/resources.groovy:

    import grails.plugins.sentry.exception.handler.SentryExceptionResolver

    beans = {
        exceptionHandler(SentryExceptionResolver) {
            exceptionMappings = ['java.lang.Exception': '/error']
        }
    }

You can also use the SentryService:

    import grails.plugins.sentry.SentryService

    SentryService sentryService

    sentryService.logInfo(String message)
    sentryService.logMessage(String message, String loggerClass, String logLevel)
    sentryService.logException(Throwable exception)
    sentryService.logException(Throwable exception, String loggerClass, String logLevel)

TODO
----

* Log4j Appender
* Implement sentry.interfaces.User
* Update to Grails 2.x
* Remove raven-java dependency
* sentry.interfaces.Template
* sentry.interfaces.Query
