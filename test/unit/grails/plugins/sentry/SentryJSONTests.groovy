package grails.plugins.sentry

import grails.test.*
import grails.plugins.sentry.TestUtils
import org.codehaus.groovy.grails.web.json.JSONObject
import net.kencochrane.sentry.RavenConfig
import net.kencochrane.sentry.RavenUtils
import javax.servlet.http.HttpServletRequest

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

    def void testBuildHttp() {
        Map map = TestUtils.reloadFossilizedHttpServletRequest(this)
        HttpServletRequest request = (HttpServletRequest) TestUtils.getHttpServletRequest(map)

        JSONObject json = json.buildHttp(request)
        assertNotNull json
        assertEquals map.url.toString().split('.dispatch').first(), json.get('url')
    }

    def void testBuildJSON() {
        String result = json.build('message', testException, 'logClass', 'error', null, 'timestamp')
        assertBaseJSONString(result)
    }

    def void testBuildJSONWithHttpServletRequest() {
        Map map = TestUtils.reloadFossilizedHttpServletRequest(this)
        HttpServletRequest request = (HttpServletRequest) TestUtils.getHttpServletRequest(map)

        JSONObject httpJSON = json.buildHttp(request)
        String result = json.build('message', testException, 'logClass', 'error', request, 'timestamp')
        println httpJSON.toString()

        assertBaseJSONString(result)
        assert result =~ /"sentry.interfaces.Http"/
        assert result =~ /"url":"http:\/\/localhost:8080\/sentry\/grails\/test\/error"/
        assert result =~ /"query_string":"test=lala&test2=lolo"/
        assert result =~ /"cookies":"key1=value1,key2=value2"/
        assert result =~ /"env":\{"SERVER_PORT":8080,"REMOTE_ADDR":"10\.10\.10\.10","SERVER_NAME":"localhost"\}/
        assert result =~ /"data":\{"test":"lala","test2":"lolo"\}/
        assert result =~ /"method":"GET"/
    }

// LOCAL ASSERTION

    private void assertBaseJSONString(String result) {
        assert result =~ /"server_name":"${RavenUtils.getHostname()}"/
        assert result =~ /"message":"message"/
        assert result =~ /"timestamp":"timestamp"/
        assert result =~ /"project":"id"/
        assert result =~ /"level":"error","logger":"logClass"/
        assert result =~ /"culprit":"sun.reflect.NativeConstructorAccessorImpl.newInstance0"/
        assert result =~ /"sentry\.interfaces\.Stacktrace":/
    }
}
