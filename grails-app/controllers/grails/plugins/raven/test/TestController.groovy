package grails.plugins.raven.test

import net.kencochrane.raven.Raven
import net.kencochrane.raven.event.Event
import net.kencochrane.raven.event.EventBuilder
import net.kencochrane.raven.event.interfaces.ExceptionInterface

class TestController {

    Raven raven

    def clientInfo = {
        raven?.sendMessage("RavenClient logInfo test.")
        render(view:'/index')
    }

    def clientEvent = {
        try {
            unsafeMethod()
        } catch (Exception e) {
            // This adds an exception to the logs
            EventBuilder eventBuilder = new EventBuilder(
                    level: Event.Level.ERROR,
                    logger: TestController.class.name,
                    message: "Exception caught"
            ).addSentryInterface(
                    new ExceptionInterface(e)
            )

            raven?.runBuilderHelpers(eventBuilder) // Optional
            raven?.sendEvent(eventBuilder.build())
        }
        render(view:'/index')
    }

    def clientException = {
        raven?.sendException(new Exception("RavenClient logException test."))
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

    private def unsafeMethod() {
        throw new UnsupportedOperationException("You shouldn't call that");
    }
}
