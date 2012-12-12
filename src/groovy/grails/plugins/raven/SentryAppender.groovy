package grails.plugins.raven

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.Level
import org.apache.log4j.spi.LoggingEvent
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

class SentryAppender extends AppenderSkeleton {

    RavenClient ravenClient

    public SentryAppender(RavenClient client) {
        super()
        ravenClient = client
        setThreshold(Level.ERROR)
    }

    void append(LoggingEvent event) {
        if (ravenClient.config.active == false) { return }

        def level = event.getLevel()

        if (level.equals(Level.ERROR) || level.equals(Level.FATAL) || level.equals(Level.WARN)) {
            def grailsRequest = (GrailsWebRequest) RequestContextHolder.requestAttributes
            def request = grailsRequest?.getRequest()
            def currentUser = request ? request['sentryUserData'] : null
            ravenClient.captureEvent(event, request, currentUser)
        }
    }

    void close() { }

    boolean requiresLayout() {
        return false
    }
}
