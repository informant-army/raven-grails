import org.apache.log4j.Logger
import grails.util.Environment

import grails.plugins.sentry.SentryAppender
import grails.plugins.sentry.SentryClient
import grails.plugins.sentry.SentryConfiguration

class SentryGrailsPlugin {
    def version = "0.4.1"
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
    def documentation = "http://grails.org/plugin/sentry"

    def license = "APACHE"
    def developers = [ [ name: "Alan Fachini", email: "alfakini@gmail.com" ] ]
    def issueManagement = [ system: "GitHub", url: "https://github.com/informant-army/raven-grails/issues" ]
    def scm = [ url: "https://github.com/informant-army/raven-grails" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        ConfigObject config = new ConfigObject()
        config['clientVersion'] = clientVersion
        def configuration = new SentryConfiguration(config.merge(application.config.grails.plugins.sentry.clone()))

        sentryClient(SentryClient, configuration) { bean ->
            bean.autowire = "byName"
        }

        sentryAppender(SentryAppender, ref('sentryClient'))
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
