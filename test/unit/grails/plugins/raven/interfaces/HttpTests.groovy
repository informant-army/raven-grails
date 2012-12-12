package grails.plugins.raven.interfaces

import grails.test.*
import grails.plugins.raven.interfaces.Http
import grails.plugins.raven.TestUtils
import org.codehaus.groovy.grails.web.json.JSONObject
import java.io.ObjectInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Cookie

class HttpTests extends GroovyTestCase {

    Map requestMap
    HttpServletRequest request

    protected void setUp() {
        requestMap = TestUtils.reloadFossilizedHttpServletRequest(this)
        assertNotNull requestMap
        request = TestUtils.getHttpServletRequest(requestMap)
        assertNotNull request
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    def void testHttpServletRequestMock() {
        assertEquals requestMap.uri, request.getRequestURI()
        assertEquals requestMap.url, request.getRequestURL()
        assertEquals requestMap.method, request.getMethod()
        assertEquals requestMap.query, request.getQueryString()
        assertEquals requestMap.parameterMap, request.getParameterMap()
        assertEquals TestUtils.getHeaderNames(requestMap).class, request.getHeaderNames().class
    }

    def void testBuildHeaders() {
        Http http = new Http(request)
        assertEquals requestMap.headers.size(), http.headers.size()
        assertEquals requestMap['accepts'], http.headers['accepts']
    }

    def void testFilterURL() {
        Http http = new Http(request)
        assertEquals requestMap.url.toString().split('.dispatch').first(), http.url
    }

    def void testData() {
        Http http = new Http(request)
        requestMap.parameterMap.each {
            assert http.data.containsKey(it.key)
            assert http.data.containsValue(it.value.first())
            assertEquals http.data[it.key], it.value.first()
        }
    }

    def void testEnv() {
        Http http = new Http(request)
        assertEquals TestUtils.TESTREMOTEADDR, http.env['REMOTE_ADDR']
    }

    def void testCookies() {
        Http http = new Http(request)
        def testCookies = []
        TestUtils.TESTCOOKIES.each { testCookies << "${it.getName()}=${it.getValue()}" }
        assertEquals testCookies.join(','), http.cookies
    }

    def void testGetJSONObject() {
        Http http = new Http(request)
        JSONObject json = http.toJSONObject()
        assertNotNull json 
        assertEquals http.url, json.url
    }
}

