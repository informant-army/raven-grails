package grails.plugins.sentry

import grails.test.*
import net.kencochrane.sentry.RavenConfig
import net.kencochrane.sentry.RavenUtils

class SentryJSONTests extends GroovyTestCase {

    String dsn = 'https://PUBLIC_KEY:SECRET_KEY@app.getsentry.com/id'
    RavenConfig config
    SentryJSON json
    Exception testException

    protected void setUp() {
        testException = new Exception('Message')
        config = new RavenConfig(dsn)
        json = new SentryJSON(config)

        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    def void testBuildException() {
        def result = json.buildException(testException)

        assertNotNull result
        assertEquals 3, result.size()
        assertEquals 'Exception', result.type
        assertEquals 'Message', result.value
        assertEquals 'java.lang', result.module
    }

    def void testBuildStacktrace() {
        def result = json.buildStacktrace(testException)

        assertNotNull result
        assertNotNull result.get('frames')
        assertEquals 'Caused by: java.lang.Exception ("Message")', result.get('frames').get(0).get('filename')
    }

    def void testDetermineCulprit() {
        String result = json.determineCulprit(testException)

        assertEquals "sun.reflect.NativeConstructorAccessorImpl.newInstance0", result
    }

    def void testBuildJSON() {
        String result = json.build('message', 'timestamp', 'logClass', 50, 'com.test.someMethod', testException)

        assert result =~ /"server_name":"${RavenUtils.getHostname()}"/
        assert result =~ /"message":"message"/
        assert result =~ /"timestamp":"timestamp"/
        assert result =~ /"project":"id"/
        assert result =~ /"level":50,"logger":"logClass"/
        assert result =~ /"culprit":"sun.reflect.NativeConstructorAccessorImpl.newInstance0"/
        assert result =~ /"sentry\.interfaces\.Stacktrace":/
    }
}
