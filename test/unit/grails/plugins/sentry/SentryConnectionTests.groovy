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
        SentryConfiguration config = new SentryConfiguration([dsn:dsn, active:true])
        SentryConnection connector = new SentryConnection(config)

        assertEquals config.getSentryURL().toString(), connector.config.getSentryURL().toString()
    }
}
