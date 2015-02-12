import grails.plugins.raven.GrailsLog4jSentryAppender
import net.kencochrane.raven.DefaultRavenFactory
import net.kencochrane.raven.dsn.Dsn
import org.apache.log4j.Logger

class RavenGrailsPlugin {

    def version = "6.0.0"
    def clientVersion = "Raven-grails $version"
    def grailsVersion = "1.3.9 > *"
    def dependsOn = [:]
    def pluginExcludes = [
        "grails-app/views/**",
        "grails-app/controllers/**",
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

    def doWithSpring = {
        def pluginConfig = application.config.grails?.plugin?.raven
        if (!pluginConfig) {
            pluginConfig = application.config.grails?.plugins?.raven // Legacy
        }
        if (pluginConfig?.dsn) {
            log.info "Raven config found, creating Raven/Sentry client and corresponding Log4J appender"
            ravenFactory(DefaultRavenFactory)
            raven(ravenFactory: "createRavenInstance", new Dsn(pluginConfig.dsn)) { bean ->
                bean.autowire = 'byName'
            }
            sentryAppender(GrailsLog4jSentryAppender, ref('raven'), pluginConfig)
        } else {
            log.warn "Raven config not found, add 'grails.plugin.raven.dsn' to your config to enable Raven/Sentry client"
        }
    }

    def doWithApplicationContext = { applicationContext ->
        def configLogger = application.config.grails?.plugin?.raven?.logger
        GrailsLog4jSentryAppender appender = applicationContext.sentryAppender
        if (appender) {
            appender.activateOptions()
            Logger.getLogger(configLogger).addAppender(appender)
        }
    }

}
