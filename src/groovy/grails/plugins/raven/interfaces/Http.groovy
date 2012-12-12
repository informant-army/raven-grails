package grails.plugins.raven.interfaces

import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.web.json.*
import static java.util.Collections.list

/*
 * sentry.interfaces.Http
 * {
 *   "url": "http://absolute.uri/foo",
 *   "method": "POST',
 *   "data": {
 *       "foo": "bar"
 *   },
 *   "query_string": "hello=world",
 *   "cookies": "foo=bar",
 *   "headers": {
 *       "Content-Type": "text/html"
 *   },
 *   "env": {
 *       "REMOTE_ADDR": "192.168.0.1"   
 *   }
 * }
 */
class Http {

    Map data = [:]
    Map headers = [:]
    Map env = [:]
    String url
    String method
    String query_string
    String cookies

    Http(HttpServletRequest request) {
        this.headers = buildHeaders(request)
        this.url = filterURL(request.getRequestURL())
        this.method = request.getMethod()
        this.query_string = request.getQueryString()

        def serverName = request.getServerName()
        if (serverName) { this.env['SERVER_NAME'] = serverName } 
        def serverPort = request.getServerPort()
        if (serverPort) { this.env['SERVER_PORT'] = serverPort } 
        def remoteAddr = request.getRemoteAddr()
        if (remoteAddr) { this.env['REMOTE_ADDR'] = remoteAddr }

        def requestParams = request.getParameterMap()
        if (requestParams) {
            requestParams.each {
                this.data[it.key] = it.value.first()
            }
        }

        this.cookies = buildCookies(request)
    }

    public JSONObject toJSONObject() {
        return new JSONObject([
            url: this.url,
            method: this.method,
            query_string: this.query_string,
            cookies: this.cookies,
            data: new JSONObject(this.data),
            headers: new JSONObject(this.headers),
            env: new JSONObject(this.env)
        ])
    }

    private Map buildHeaders(HttpServletRequest request) {
        def headers = [:]
        list(request.getHeaderNames()).each {
            headers[it] = request.getHeader(it)
        }
        headers
    }

    private String filterURL(url) {
        String urlString = url.toString()
        if (urlString.contains('.dispatch')) {
            def first = urlString.split('.dispatch').first()
            urlString = (first ? first : urlString)
        }
        urlString
    }

    private String buildCookies(HttpServletRequest request) {
        def cookies = []
        request.getCookies().each {
            cookies << "${it.getName()}=${it.getValue()}"
        }
        return cookies.join(',')
    }
}
