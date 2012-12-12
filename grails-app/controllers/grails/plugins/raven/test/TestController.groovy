package grails.plugins.raven.test

import grails.plugins.raven.RavenClient

class TestController {
    def ravenClient

    def clientInfo = {
        ravenClient.logInfo("RavenClient logInfo test.")
        render(view:'/index')
    }

    def clientMessage = {
        ravenClient.logMessage("RavenClient logMessage test.", "root", "info")
        render(view:'/index')
    }

    def clientExcetion = {
        ravenClient.logException(new Exception("RavenClient logExcetion test."))
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
