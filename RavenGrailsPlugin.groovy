import org.apache.log4j.Logger
import grails.util.Environment

import grails.plugins.raven.SentryAppender
import grails.plugins.raven.RavenClient
import grails.plugins.raven.Configuration

class RavenGrailsPlugin {
    def version = "0.5.6-SNAPSHOT"
    def clientVersion = "Raven-grails $version"
    def grailsVersion = "1.3.9 > *"
    def dependsOn = [:]
    def pluginExcludes = [
        "grails-app/conf/SentryFilters.groovy",
        "grails-app/views/**",
        "grails-app/controllers/**",
        "grails-app/services/test/**",
        "test/**",
        "web-app/**"
    ]

    def title = "Sentry Client Plugin"
    def author = "Alan Fachini"
    def authorEmail = "alfakini@gmail.com"
    def description = "Sentry Client for Grails"
    def documentation = "http://github.com/agorapulse/grails-raven/blob/master/README.md"

    def license = "APACHE"
    def developers = [ [ name: "Benoit Hediard", email: "ben@benorama.com" ] ]
    def issueManagement = [ system: "GitHub", url: "http://github.com/agorapulse/grails-raven/issues" ]
    def scm = [ url: "http://github.com/agorapulse/grails-raven" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        ConfigObject config = new ConfigObject()
        config['clientVersion'] = clientVersion
        def configuration = new Configuration(config.merge(application.config.grails.plugins.raven.clone()))

        ravenClient(RavenClient, configuration) { bean ->
            bean.autowire = "byName"
        }

        sentryAppender(SentryAppender, ref('ravenClient'))
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        def appender = applicationContext.sentryAppender
        appender.activateOptions()
        Logger.rootLogger.addAppender(appender)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
