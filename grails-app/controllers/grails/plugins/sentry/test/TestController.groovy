package grails.plugins.sentry.test

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.plugins.sentry.SentryClient

class TestController {
    def sentryService

    def error = {
        throw new Exception("Exception test")
        render(view:'/index')
    }

    def serviceMessage = {
        sentryService.logInfo("Service logInfo test")
        render(view:'/index')
    }

    def serviceException = {
        sentryService.logException(new Exception('Service logException test'))
        render(view:'/index')
    }

    def client = {
        SentryClient client = new SentryClient(getDSN())
        client.logInfo("Client logInfo test")
        render(view:'/index')
    }

    private String getDSN() {
        return ConfigurationHolder.config.grails.plugins.sentry.dsn
    }
}
