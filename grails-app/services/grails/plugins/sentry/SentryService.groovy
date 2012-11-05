package grails.plugins.sentry

import grails.util.Environment
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SentryService {
    static transactional = false

    def logInfo(String message) {
        if (activeEnvironments().contains(Environment.current.getName())) {
            sentryClient().logInfo(message)
        }
    }

    def logMessage(String message, String loggerClass, String logLevel) {
        if (activeEnvironments().contains(Environment.current.getName())) {
            sentryClient().logInfo(message, loggerClass, logLevel)
        }
    }

    def logException(Throwable exception) {
        if (activeEnvironments().contains(Environment.current.getName())) {
            sentryClient().logException(exception, "root", "error")
        }
    }

    def logException(Throwable exception, String loggerClass, String logLevel) {
        if (activeEnvironments().contains(Environment.current.getName())) {
            sentryClient().logException(exception, loggerClass, logLevel)
        }
    }

    private SentryClient sentryClient() {
        new SentryClient(getDSN())
    }

    private String getDSN() {
        return ConfigurationHolder.config.grails.plugins.sentry.dsn
    }

    private List<String> activeEnvironments() {
        return ConfigurationHolder.config.grails.plugins.sentry.environments
    }
}
