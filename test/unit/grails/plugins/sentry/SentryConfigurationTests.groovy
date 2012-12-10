package grails.plugins.sentry

import grails.test.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SentryConfigurationTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    public void testConfiguration() {
        String testDSN = "https://abc:efg@app.getsentry.com/123"
        String testClientVersion = 'Raven-Grails test'

        mockConfig('''
            grails.plugins.sentry.dsn = "https://abc:efg@app.getsentry.com/123"
            grails.plugins.sentry.active = true
        ''')

        SentryConfiguration config = new SentryConfiguration([clientVersion:testClientVersion] + ConfigurationHolder.config.grails.plugins.sentry)

        assertTrue config.active
        assertEquals testDSN, config.dsn
        assertEquals "app.getsentry.com", config.host
        assertEquals "https", config.protocol
        assertEquals "abc", config.publicKey
        assertEquals "efg", config.secretKey
        assertEquals "Raven-Grails test", config.clientVersion
        assertEquals "123", config.projectId
        assertEquals "", config.path
    }

    public void testConfigurationWithPath() {
        mockConfig('''
            grails.plugins.sentry.dsn = "https://abc:efg@app.getsentry.com/path/to/api/123"
            grails.plugins.sentry.active = true
        ''')

        SentryConfiguration config = new SentryConfiguration(ConfigurationHolder.config.grails.plugins.sentry)

        assertTrue config.active
        assertEquals "123", config.projectId
        assertEquals "/path/to/api", config.path
    }

    public void testConfigurationWithPort() {
        mockConfig('''
            grails.plugins.sentry.dsn = "https://abc:efg@app.getsentry.com:666/123"
        ''')

        SentryConfiguration config = new SentryConfiguration(ConfigurationHolder.config.grails.plugins.sentry)

        assertEquals 666, config.port
    }

    /*public void testNullDSNException() {
        mockConfig('''
            grails.plugins.sentry.active = true
        ''')

        String message = shouldFail(SentryException) {
            SentryConfiguration config = new SentryConfiguration(ConfigurationHolder.config.grails.plugins.sentry)
        }

        assertEquals "The DSN address is required. Get this from the project's page.", message
    }*/

    public void testServerName() {
        mockConfig('''
            grails.plugins.sentry.dsn = "https://abc:efg@app.getsentry.com/path/to/api/123"
        ''')
        SentryConfiguration config = new SentryConfiguration(ConfigurationHolder.config.grails.plugins.sentry)

        assertEquals InetAddress.localHost?.canonicalHostName, config.serverName
    }

    public void testServerNameOnConfiguration() {
        mockConfig('''
            grails.plugins.sentry.dsn = "https://abc:efg@app.getsentry.com/path/to/api/123"
            grails.plugins.sentry.serverName = "testServerName"
        ''')
        SentryConfiguration config = new SentryConfiguration(ConfigurationHolder.config.grails.plugins.sentry)

        assertEquals "testServerName", config.serverName
    }
}
