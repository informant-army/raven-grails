# raven-grails

[![Build Status](https://secure.travis-ci.org/informant-army/raven-grails.png?branch=master)](https://travis-ci.org/informant-army/raven-grails)

raven-grails is a Grails client for integrating apps with [Sentry](http://www.getsentry.com).

## Installation

Clone the repository and build the plugin:

    $ git@github.com:informant-army/raven-grails.git
    $ cd raven-grails
    $ grails package-plugin

Copy the generated .zip to your applications /lib directory add the following to your `BuildConfig.groovy`:

```groovy
compile ":sentry:0.4.2"
```

## Configuration

You need to provide your Sentry DSN in `Config.groovy` file. The plugin will sent notifications to Sentry by default, if you want to disable notifications for an specific environment set the active option as false.

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
    }
}
```

To set the current user data to be included in the logged messages sent to Sentry use the method `sentryClient.setUserData` passing a Map containing the user data. The supported keys are id, username, email and is\_authenticated. The only key that is mandatory is is\_authenticated. If you are using SpringSecurity in your application, you can get the current user using the method `springSecurityService.currentUser`. Above an example of Grails filter to set the user data:

```groovy
import grails.plugins.sentry.SentryClient

class SentryFilters {
    def sentryClient
    def springSecurityService

    def filters = {
        all(uri: '/**') {
            before = {
                if (springSecurityService.isLoggedIn()) {
                    def user = springSecurityService.currentUser
                    def userData = [id: user.id, is_authenticated: true, email: user.email, username: user.username]
                    sentryClient.setUserData(userData)
                } else {
                    sentryClient.setUserData([is_authenticated:false])
                }
            }
        }
    }
}
```

You can also set the server name, but it is recommended to don't set this configuration and let the plugin to resolve it.

```groovy
grails.plugins.sentry.serverName = 'dev.server.com'
```

## Usage

### Log4j Appender

The Log4j Appender is automatically configured by plugin, you have just to set enabled environments in `Config.groovy` file as shown in Configuration section. All application exceptions will be logged on sentry by the appender. The appender is configured to log just the ERROR, WARN and FATAL levels. To log manually just use the `log.error` method.

### SentryClient

You also can use the sentryClient to sent info messages to Sentry:

```groovy
import grails.plugins.sentry.SentryClient

def sentryClient

sentryClient.logInfo(String message)
sentryClient.logMessage(String message, String loggerClass, String logLevel)
sentryClient.logException(Throwable exception)
sentryClient.logException(Throwable exception, String loggerClass, String logLevel, HttpServletRequest request)
```
