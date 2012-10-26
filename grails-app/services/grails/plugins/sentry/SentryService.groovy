package grails.plugins.sentry

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SentryService {
    static transactional = false

    def logInfo(String message) {
        sentryClient().logInfo(message)
    }

    def logMessage(String message, String loggerClass, String logLevel) {
        sentryClient().logInfo(message, loggerClass, logLevel)
    }

    def logException(Throwable exception) {
        sentryClient().logException(exception, "root", "error")
    }

    def logException(Throwable exception, String loggerClass, String logLevel) {
        sentryClient().logException(exception, loggerClass, logLevel)
    }

    private SentryClient sentryClient() {
        new SentryClient(getDSN())
    }

    private String getDSN() {
        return ConfigurationHolder.config.grails.plugins.sentry.dsn
    }
}
