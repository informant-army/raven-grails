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

    public void testNotActiveEnvironments() {
        assert 'test', Environment.getCurrent().getName()

        def sentryClient = mockFor(SentryClient)
        sentryClient.metaClass.logInfo = {
            return true
        }

        sentryService.metaClass.sentryClient = {
            return sentryClient
        }

        mockConfig('''
            grails.plugins.sentry.active = false
        ''')

        def result = sentryService.logInfo("Test Active Environments.")
        assertNull result
    }

    public void testActiveEnvironment() {
        assert 'test', Environment.getCurrent().getName()

        def sentryClient = mockFor(SentryClient)
        sentryClient.metaClass.logInfo = {
            return true
        }

        sentryService.metaClass.sentryClient = {
            return sentryClient
        }

        mockConfig('''
            grails.plugins.sentry.active = true
        ''')

        def result = sentryService.logInfo("Test Active Environments.")
        assertTrue result
    }
}
