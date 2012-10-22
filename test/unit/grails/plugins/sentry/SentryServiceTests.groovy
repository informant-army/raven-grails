package grails.plugins.sentry

import grails.test.*

class SentryServiceTests extends GrailsUnitTestCase {

   def sentryService

    protected void setUp() {
        sentryService = new SentryService()
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

   void testCaptureMessage() {
        String result = sentryService.captureMessage("Message.")
        println result
    }
}
