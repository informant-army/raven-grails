package grails.plugins.sentry

import grails.test.*
import grails.util.Environment

class SentryServiceTests extends GrailsUnitTestCase {

   def sentryService

    protected void setUp() {
        sentryService = new SentryService()
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    public void testActiveEnvironments() {
        def sentryClient = mockFor(SentryClient)
        sentryClient.metaClass.logInfo = {
            return true
        }

        sentryService.metaClass.sentryClient = {
            return sentryClient
        }
        sentryService.metaClass.activeEnvironments = {
            return ['production']
        }

        Environment.metaClass.static.getName = {
            return 'production'
        }

        def result = sentryService.logInfo("Test Active Environments.")
        assertTrue result

        Environment.metaClass.static.getName = {
            return 'test'
        }

        result = sentryService.logInfo("Test Active Environments.")
        assertNull result
    }
}
