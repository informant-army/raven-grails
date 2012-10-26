package grails.plugins.sentry

import grails.test.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Cookie
import java.io.ObjectInputStream

class TestUtils {

    public static final String TESTREMOTEADDR = '10.10.10.10'
    public static final Cookie[] TESTCOOKIES = [new Cookie('key1', 'value1'), new Cookie('key2', 'value2')]

    public static Map reloadFossilizedHttpServletRequest(clazz) throws Exception {
        return (new ObjectInputStream(clazz.getClass().classLoader.getResourceAsStream('resources/request.dat'))).readObject()
    }

    public static HttpServletRequest getHttpServletRequest(requestMap) {
        return [
            getHeaderNames: { return getHeaderNames(requestMap) },
            getRequestURL: { return requestMap.url },
            getRequestURI: { return requestMap.uri },
            getQueryString: { return requestMap.query },
            getMethod: { return requestMap.method },
            getProtocol: { return requestMap.protocol },
            getParameterMap: { return requestMap.parameterMap },
            getRemoteAddr: { return TESTREMOTEADDR },
            getHeader: { name -> return requestMap.headers[name] },
            getCookies: { return TESTCOOKIES },
            getServerName: { return "localhost" },
            getServerPort: { return 8080 },
        ] as HttpServletRequest;
    }

    public static Enumeration<String> getHeaderNames(requestMap) {
        return (Enumeration<String>) Collections.enumeration(requestMap.headersNames)
    }
}
