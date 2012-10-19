import org.apache.log4j.Logger
import grails.util.Environment

class SentryGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.9 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/**",
            "grails-app/controllers/**",
            "grails-app/services/test/**",
            "test/**",
            "web-app/**"
    ]

    def title = "Sentry Plugin"
    def author = "Alan Fachini"
    def authorEmail = "alfakini@gmail.com"
    def description = "Sentry Client for Grails"
    def documentation = "http://grails.org/plugin/sentry"

    //def license = "APACHE"
    //def developers = [ [ name: "Name", email: "email@to.com" ] ]
    //def issueManagement = [ system: "GitHub", url: "https://github.com/alfakini/sentry-grails/issues" ]
    //def scm = [ url: "https://github.com/alfakini/sentry-grails" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
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
