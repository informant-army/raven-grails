package grails.plugins.sentry.test

import grails.plugins.sentry.SentryClient

class TestController {
    def sentryClient

    def clientInfo = {
        sentryClient.logInfo("SentryClient logInfo test.")
        render(view:'/index')
    }

    def clientMessage = {
        sentryClient.logMessage("SentryClient logMessage test.", "root", "info")
        render(view:'/index')
    }

    def clientExcetion = {
        sentryClient.logException(new Exception("SentryClient logExcetion test."))
        render(view:'/index')
    }

    def error = {
        throw new Exception("Exception test.")
        render(view:'/index')
    }

    def testLog = {
        log.error("Test Sentry Log4j Appender.")
        render(view:'/index')
    }
}
