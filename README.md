Sentry Grails Plugin
====================

[![Build Status](https://secure.travis-ci.org/agorapulse/grails-raven.png?branch=master)](https://travis-ci.org/agorapulse/grails-raven)
[![Download](https://api.bintray.com/packages/agorapulse/plugins/sentry/images/download.svg)](https://bintray.com/agorapulse/plugins/sentry/_latestVersion)

# Introduction

Sentry plugin provides a Grails client for integrating apps with [Sentry](http://www.getsentry.com). 
[Sentry](http://www.getsentry.com) is an event logging platform primarily focused on capturing and aggregating exceptions.

It uses the official [Raven-java](https://github.com/getsentry/raven-java) client under the cover.

# Installation

Declare the plugin dependency in the _build.gradle_ file, as shown here:

```groovy
dependencies {
    ...
    compile("org.grails.plugins:sentry:11.3.0")
    ...
}
```

# Config

Add your Sentry DSN to your _grails-app/conf/application.yml_.

```yml
grails:
    plugin:
        sentry:
            dsn: https://{PUBLIC_KEY}:{SECRET_KEY}@app.getsentry.com/{PATH}{PROJECT_ID}
```

The plugin will sent notifications to Sentry by default, if you want to disable notifications for an specific environment set the `active` option as false.

```yml
environments:
    development:
        grails:
            plugin:
                sentry:
                    active: false
    test:
        grails:
            plugin:
                sentry:
                    active: false
```

You can also configure the multiple logger to which you want to append the sentry appender.
You can also set the server name, but it is recommended to don't set this configuration and let the plugin to resolve it.


## Optional configurations

```yml
# Not tested on Grails 3 plugin...
grails:
    plugin:
        sentry:
            dsn: https://foo:bar@api.sentry.io/123
            loggers: [LOGGER1, LOGGER2, LOGGER3]
            environment: staging
            serverName: dev.server.com
            levels: [ERROR]
            tags: {tag1: val1,  tag2: val2, tag3: val3}
            subsystems: 
                MODULE1: [com.company.services.module1, com.company.controllers.module1]
                MODULE2: [com.company.services.module2, com.company.controllers.module2]
                MODULE3: [com.company.services.module3, com.company.controllers.module3]
            logClassName: true
            logHttpRequest: true
            disableMDCInsertingServletFilter: true
            springSecurityUser: true
            springSecurityUserProperties:
                id: 'id'
                email: 'emailAddress'
                username: 'login'
            priorities: 
                HIGH: [java.lang, com.microsoft.sqlserver.jdbc.SQLServerException]
                MID: [com.company.exception]
                LOW: [java.io]
```

Check [Sentry-java](https://github.com/getsentry/sentry-java) documentation to configure connection, protocol and async options in your DSN. If you are sending extra tags from the plugin for the exceptions, make sure to enable the corresponding tag on sentry tag settings for the particular project to see the tag as a filter on the exception stream on sentry.


# Usage

## Logback Appender

The Logback Appender is automatically configured by the plugin, you just have to set enabled environments as shown in Configuration section.

All application exceptions will be logged on sentry by the appender.
The appender is configured to log just the `ERROR` and `WARN` levels.
To log manually just use the `log.error()` method.

## sentryClient

You also can use `sentryClient` to sent info messages to Sentry:

```groovy
import io.sentry.SentryClient
import io.sentry.event.Event
import io.sentry.event.EventBuilder
import io.sentry.event.interfaces.ExceptionInterface

SentryClient sentryClient // To inject Spring bean raven client in your controllers or services

// Send simple message
sentryClient?.sendMessage("some message")

// Send exception
sentryClient?.sendException(new Exception("some exception"))

// Custom event
EventBuilder eventBuilder = new EventBuilder()
           .withMessage("This is a test")
           .withLevel(Event.Level.INFO)
           .withLogger(MyClass.class.name)

sentryClient?.sendEvent(eventBuilder.build())
```

# Latest releases

* 2017-07-17 **V11.3.0** : upgrade Sentry java lib to 1.3.0 + bug fix, thanks to [donbeave](https://github.com/donbeave) PR #34
* 2017-07-04 **V11.2.0** : upgrade Sentry java lib to 1.2.0 (which replaces the deprecated Raven java lib)
* 2017-06-06 **V8.0.3** : upgrade Raven java lib to 8.0.3
* 2017-02-01 **V7.8.1** : upgrade Raven java lib to 7.8.1
* 2016-11-22 **V7.8.0.2** : event environment support 
* 2016-10-29 **V7.8.0.1** : minor bug fix, thanks to [donbeave](https://github.com/donbeave) PR
* 2016-10-19 **V7.8.0** : upgrade Raven java lib to 7.8.0
* 2016-10-10 **V7.7.1** : upgrade Raven java lib to 7.7.1
* 2016-09-27 **V7.7.0.1** : bug fix
* 2016-09-26 **V7.7.0** : upgrade Raven java lib to 7.7.0, release support added to events
* 2016-08-22 **V7.6.0** : upgrade Raven java lib to 7.6.0, Spring Security integration improvements, thanks to [donbeave](https://github.com/donbeave) PR
* 2016-07-22 **V7.4.0** : upgrade Raven java lib to 7.4.0, better logging and support for Spring Security Core , thanks to [donbeave](https://github.com/donbeave) PR
* 2016-06-22 **V7.3.0** : upgrade Raven java lib to 7.3.0
* 2016-05-03 **V7.2.1** : upgrade Raven java lib to 7.2.1
* 2016-04-12 **V7.1.0.1** : minor update
* 2016-04-06 **V7.1.0** : upgrade Raven java lib to 7.1.0, thanks to [donbeave](https://github.com/donbeave) PR (WARNING: Raven package has been renamed from `net.kencochrane.raven` to `com.getsentry.raven`)
* 2015-08-31 **V6.0.0** : initial release for Grails 3.x

## Bugs

To report any bug, please use the project [Issues](https://github.com/agorapulse/grails-raven/issues/new) section on GitHub.

## Contributing

Please contribute using [Github Flow](https://guides.github.com/introduction/flow/). Create a branch, add commits, and [open a pull request](https://github.com/agorapulse/grails-raven/compare/).

## License

Copyright © 2016 Alan Rafael Fachini, authors, and contributors. All rights reserved.

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) file for details.

## Maintained by

[![Agorapulse](https://cloud.githubusercontent.com/assets/139017/17053391/4a44735a-5034-11e6-8e72-9f4b7139d7e0.png)](https://www.agorapulse.com/) **&** [![Scentbird](https://cloud.githubusercontent.com/assets/139017/17053392/4a4f343e-5034-11e6-95c9-f6371f7848f1.png)](https://www.scentbird.com/)
