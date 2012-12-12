package grails.plugins.raven

import grails.test.*

class ConnectionTests extends GroovyTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

   def void testSentryConnectionConstructor() {
        String dsn = 'https://PUBLIC_KEY:SECRET_KEY@app.getsentry.com/id'
        Configuration config = new Configuration([dsn:dsn, active:true])
        Connection connector = new Connection(config)

        assertEquals config.getSentryURL().toString(), connector.config.getSentryURL().toString()
    }
}
