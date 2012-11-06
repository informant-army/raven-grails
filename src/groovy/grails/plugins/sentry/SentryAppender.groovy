package grails.plugins.sentry

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.spi.LoggingEvent
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SentryAppender extends AppenderSkeleton {

    SentryClient sentryClient

    public SentryAppender() {
        super()
        sentryClient = new SentryClient(getDSN())
    }

    void append(LoggingEvent event) {
        sentryClient.logEvent(event)
    }

    void close() { }

    boolean requiresLayout() {
        return false
    }

    String getDSN() {
        return ConfigurationHolder.config.grails.plugins.sentry.dsn
    }
}
