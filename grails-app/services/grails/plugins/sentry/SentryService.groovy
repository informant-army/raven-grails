package grails.plugins.sentry

import grails.util.Environment
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.web.context.request.RequestContextHolder

class SentryService {
    static transactional = false

    def logInfo(String message) {
        if (isActive()) {
            sentryClient().logInfo(message)
        }
    }

    def logMessage(String message, String loggerClass, String logLevel) {
        if (isActive()) {
            sentryClient().logInfo(message, loggerClass, logLevel)
        }
    }

    def logException(Throwable exception) {
        if (isActive()) {
            sentryClient().logException(exception, "root", "error")
        }
    }

    def logException(Throwable exception, String loggerClass, String logLevel) {
        if (isActive()) {
            sentryClient().logException(exception, loggerClass, logLevel)
        }
    }

    def setUserData(Map user) {
        def request = RequestContextHolder.currentRequestAttributes().getRequest()
        request['sentryUserData'] = user
    }

    private SentryClient sentryClient() {
        new SentryClient(getDSN())
    }

    private String getDSN() {
        return ConfigurationHolder.config.grails.plugins.sentry.dsn
    }

    private boolean isActive() {
        return ConfigurationHolder.config.grails.plugins.sentry.active
    }
}
