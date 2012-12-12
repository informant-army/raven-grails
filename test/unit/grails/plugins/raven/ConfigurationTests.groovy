package grails.plugins.raven

import grails.plugins.raven.Configuration
import grails.test.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ConfigurationTests extends GrailsUnitTestCase {

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
            grails.plugins.raven.dsn = "https://abc:efg@app.getsentry.com/123"
            grails.plugins.raven.active = true
        ''')

		Configuration config = new Configuration([clientVersion:testClientVersion] + ConfigurationHolder.config.grails.plugins.raven)

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
            grails.plugins.raven.dsn = "https://abc:efg@app.getsentry.com/path/to/api/123"
            grails.plugins.raven.active = true
        ''')

		Configuration config = new Configuration(ConfigurationHolder.config.grails.plugins.raven)

		assertTrue config.active
		assertEquals "123", config.projectId
		assertEquals "/path/to/api", config.path
	}

	public void testConfigurationSentryURL() {
		// No port configuration
		mockConfig('''grails.plugins.raven.dsn = "https://abc:efg@app.getsentry.com/123"''')
		Configuration config = new Configuration(ConfigurationHolder.config.grails.plugins.raven)

		assertEquals "https://app.getsentry.com/api/store/", config.sentryURL

		// With port configuration
		mockConfig('''grails.plugins.raven.dsn = "https://abc:efg@app.getsentry.com:666/123"''')
		config = new Configuration(ConfigurationHolder.config.grails.plugins.raven)

		assertEquals "https://app.getsentry.com:666/api/store/", config.sentryURL
	}

	public void testConfigurationWithPort() {
		mockConfig('''grails.plugins.raven.dsn = "https://abc:efg@app.getsentry.com:666/123"''')
		Configuration config = new Configuration(ConfigurationHolder.config.grails.plugins.raven)

		assertEquals 666, config.port
		assertEquals "https://app.getsentry.com:666/api/store/", config.sentryURL
	}

	/*public void testNullDSNException() {
	 mockConfig('''
	 grails.plugins.raven.active = true
	 ''')
	 String message = shouldFail(RavenException) {
	 SentryConfiguration config = new SentryConfiguration(ConfigurationHolder.config.grails.plugins.raven)
	 }
	 assertEquals "The DSN address is required. Get this from the project's page.", message
	 }*/

	public void testServerName() {
		mockConfig('''grails.plugins.raven.dsn = "https://abc:efg@app.getsentry.com/path/to/api/123"''')
		Configuration config = new Configuration(ConfigurationHolder.config.grails.plugins.raven)

		assertEquals InetAddress.localHost?.canonicalHostName, config.serverName
	}

	public void testServerNameOnConfiguration() {
		mockConfig('''
            grails.plugins.raven.dsn = "https://abc:efg@app.getsentry.com/path/to/api/123"
            grails.plugins.raven.serverName = "testServerName"
        ''')
		Configuration config = new Configuration(ConfigurationHolder.config.grails.plugins.raven)

		assertEquals "testServerName", config.serverName
	}

	public void testActiveProperty() {
		// Default true
		mockConfig('''grails.plugins.raven.dsn = "https://abc:efg@app.getsentry.com/path/to/api/123"''')
		Configuration config = new Configuration(ConfigurationHolder.config.grails.plugins.raven)

		assertTrue config.active

		// grails.plugins.raven.active = false
		mockConfig('''
            grails.plugins.raven.dsn = "https://abc:efg@app.getsentry.com/path/to/api/123"
            grails.plugins.raven.active = false
        ''')
		config = new Configuration(ConfigurationHolder.config.grails.plugins.raven)

		assertFalse config.active
	}
}
