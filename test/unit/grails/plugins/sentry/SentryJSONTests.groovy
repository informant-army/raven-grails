package grails.plugins.sentry

import grails.test.*

class SentryJSONTests extends GroovyTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    def void testBuildException() {
        Exception testException = new Exception('Message')

        SentryJSON json = new SentryJSON()
        def result = json.buildException(testException)

        assertNotNull result
        assertEquals 3, result.size()
        assertEquals 'Exception', result.type
        assertEquals 'Message', result.value
        assertEquals 'java.lang', result.module
    }

    def void testBuildStacktrace() {
        Exception testException = new Exception('Message')

        SentryJSON json = new SentryJSON()
        def result = json.buildStacktrace(testException)

        assertNotNull result
        assertEquals 'Caused by: java.lang.Exception ("Message")', result.get('frames').get(0).get('filename')
    }

    def void testDetermineCulprit() {
        Exception testException = new Exception('Message')
        SentryJSON json = new SentryJSON()
        String result = json.determineCulprit(testException)
       // println result
    }
    def void testBuildJSON() {
        Exception testException = new Exception('Message')
        SentryJSON json = new SentryJSON()
        String result = json.buildJSON('message', 'timestamp', 'logClass', 50, 'com.test.someMethod', testException)

        assert result =~ /\{\"message\":\"message\",\"timestamp\":\"timestamp\",\"level\":50,\"logger\":\"logClass\"/
        assert result =~ /\"culprit\":\"sun.reflect.NativeConstructorAccessorImpl.newInstance0\"/
        assert result =~ /\"sentry\.interfaces\.Stacktrace\"/
    }
}
