package grails.plugins.raven

import grails.test.*
import grails.plugins.raven.Events
import grails.plugins.raven.TestUtils
import grails.plugins.raven.interfaces.User
import org.codehaus.groovy.grails.web.json.JSONObject
import javax.servlet.http.HttpServletRequest

class EventsTests extends GroovyTestCase {

    String dsn = 'https://PUBLIC_KEY:SECRET_KEY@app.getsentry.com/123'
    Configuration  config
    Exception testException

    protected void setUp() {
        config = new Configuration([dsn:dsn, serverName:'serverName'])
        testException = new Exception('Message')

        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    def void testBuildException() {
        def result = Events.buildException(testException)

        assertNotNull result
        assertEquals 3, result.size()
        assertEquals 'Exception', result.type
        assertEquals 'Message', result.value
        assertEquals 'java.lang', result.module
    }

    def void testBuildStacktrace() {
        def result = Events.buildStacktrace(testException)

        assertNotNull result
        assertNotNull result.get('frames')
        assertEquals 'Caused by: java.lang.Exception ("Message")', result.get('frames').get(0).get('filename')
    }

    def void testDetermineCulprit() {
        String result = Events.determineCulprit(testException)

        assertEquals "sun.reflect.NativeConstructorAccessorImpl.newInstance0", result
    }

    def void testBuildHttp() {
        Map map = TestUtils.reloadFossilizedHttpServletRequest(this)
        HttpServletRequest request = (HttpServletRequest) TestUtils.getHttpServletRequest(map)

        JSONObject json = Events.buildHttp(request)
        assertNotNull json
        assertEquals map.url.toString().split('.dispatch').first(), json.get('url')
    }

    def void testBuildJSON() {
        String result = Events.build('eventId', 'message', 'checksum', testException, 'logClass', 'error', null, null, 'timestamp', config)
        assertBaseJSONString(result)
    }

    def void testBuildJSONWithHttpServletRequest() {
        Map map = TestUtils.reloadFossilizedHttpServletRequest(this)
        HttpServletRequest request = (HttpServletRequest) TestUtils.getHttpServletRequest(map)

        JSONObject httpJSON = Events.buildHttp(request)
        String result = Events.build('eventId', 'message', 'checksum', testException, 'logClass', 'error', request, null, 'timestamp', config)

        assertBaseJSONString(result)
        assert result =~ /"sentry.interfaces.Http"/
        assert result =~ /"url":"http:\/\/localhost:8080\/sentry\/grails\/test\/error"/
        assert result =~ /"query_string":"test=lala&test2=lolo"/
        assert result =~ /"cookies":"key1=value1,key2=value2"/
        assert result =~ /"env":\{"SERVER_PORT":8080,"REMOTE_ADDR":"10\.10\.10\.10","SERVER_NAME":"localhost"\}/
        assert result =~ /"data":\{"test":"lala","test2":"lolo"\}/
        assert result =~ /"method":"GET"/
    }

    def void testBuildJSONWithUserData() {
        User user = new User(true, [id: 123, is_authenticated: true, username: 'username', email: 'user@email.com'])

        String result = Events.build('eventId', 'message', 'checksum', testException, 'logClass', 'error', null, user, 'timestamp', config)

        assertBaseJSONString(result)
        assert result =~ /\"sentry\.interfaces\.User\":\{\"id\":\"123\",\"username\":\"username\",\"email\":\"user@email.com\",\"is_authenticated\":true\}/
    }

    def void testServerName() {
        String result = Events.build('eventId', 'message', 'checksum', testException, 'logClass', 'error', null, null, 'timestamp', config)
        assert result =~ /"server_name":"serverName"/

        config = new Configuration([dsn:dsn])
        result = Events.build('eventId', 'message', 'checksum', testException, 'logClass', 'error', null, null, 'timestamp', config)
        assert result =~ /"server_name":"${InetAddress.localHost?.canonicalHostName}"/
    }

// LOCAL ASSERTION

    private void assertBaseJSONString(String result) {
        assert result =~ /"event_id":"eventId"/
        assert result =~ /"message":"message"/
        assert result =~ /"checksum":"checksum"/
        assert result =~ /"timestamp":"timestamp"/
        assert result =~ /"project":"$config.projectId"/
        assert result =~ /"level":"error","logger":"logClass"/
        assert result =~ /"culprit":"sun.reflect.NativeConstructorAccessorImpl.newInstance0"/
        assert result =~ /"sentry\.interfaces\.Stacktrace":/
    }
}
