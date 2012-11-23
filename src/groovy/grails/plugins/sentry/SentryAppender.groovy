package grails.plugins.sentry

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.Level
import org.apache.log4j.spi.LoggingEvent
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class SentryAppender extends AppenderSkeleton {

    SentryClient sentryClient

    public SentryAppender() {
        super()
        sentryClient = new SentryClient(getDSN())
        setThreshold(Level.ERROR)
    }

    void append(LoggingEvent event) {
        if (ConfigurationHolder.config.grails.plugins.sentry.active == false) { return }

        def level = event.getLevel()

        if (level.equals(Level.ERROR) || level.equals(Level.FATAL) || level.equals(Level.WARN)) {
            def grailsRequest = (GrailsWebRequest) RequestContextHolder.requestAttributes
            def request = grailsRequest?.getRequest()
            def currentUser = request ? request['sentryUserData'] : null
            sentryClient.logEvent(event, request, currentUser)
        }
    }

    void close() { }

    boolean requiresLayout() {
        return false
    }

    String getDSN() {
        return ConfigurationHolder.config.grails.plugins.sentry.dsn
    }
}
