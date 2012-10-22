package grails.plugins.sentry

import grails.test.*
import net.kencochrane.sentry.RavenConfig

class SentryConnectionTests extends GroovyTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

   def void testSentryConnectionConstructor() {
        String dsn = 'https://PUBLIC_KEY:SECRET_KEY@app.getsentry.com/id'
        RavenConfig config = new RavenConfig(dsn)
        SentryConnection connector = new SentryConnection(config)

        assertNotNull connector.endpoint
        assertEquals config.getSentryURL().toString(), connector.config.getSentryURL().toString()
    }
}
