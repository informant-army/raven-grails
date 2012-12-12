package grails.plugins.raven

import org.codehaus.groovy.grails.web.json.*
import javax.servlet.http.HttpServletRequest
import grails.plugins.raven.interfaces.*

/*
 * http://sentry.readthedocs.org/en/latest/developer/interfaces/index.html
 */
class Events {

    /*
     * Main JSON
     * {
     * "event_id": "fc6d8c0c43fc4630ad850ee518f1b9d0",
     * "project": "default",
     * "culprit": "my.module.function_name",
     * "timestamp": "2011-05-02T17:41:36",
     * "message": "SyntaxError: Wattttt!",
     * "tags": {
     *     "ios_version": "4.0"
     * },
     * "sentry.interfaces.Exception": {
     *     "type": "SyntaxError":
     *     "value": "Wattttt!",
     *     "module": "__builtins__"
     * }
     */
    public static String build(String eventId, String message, String checksum, Throwable exception, String loggerClass, String logLevel, HttpServletRequest request, User user, String timestamp, Configuration config) {
        JSONObject obj = new JSONObject([
            event_id: eventId,
            checksum: checksum,
            timestamp: timestamp,
            message: message,
            project: config.projectId,
            level: logLevel,
            logger: loggerClass,
            platform: config.platform,
            server_name: config.serverName
        ])
        if (exception) {
            obj.put("culprit", Events.determineCulprit(exception))
            obj.put("sentry.interfaces.Exception", Events.buildException(exception))
            obj.put("sentry.interfaces.Stacktrace", Events.buildStacktrace(exception))
        }
        if (request) {
            obj.put("sentry.interfaces.Http", Events.buildHttp(request))
        }
        if (user) {
            obj.put("sentry.interfaces.User", user.toJSON())
        }
        return obj.toString()
    }

    /*
     * sentry.interfaces.Exception
     * {
     *   "type": "ValueError",
     *   "value": "My exception value",
     *   "module": "__builtins__"
     * }
    */
    public static JSONObject buildException(Throwable exception) {
        return new JSONObject([
            type: exception.getClass().getSimpleName(),
            value: exception.getMessage(),
            module: exception.getClass().getPackage().getName()
        ])
    }

    /*
     * sentry.interfaces.Stacktrace
     * { "frames": [{
     *   "abs_path": "/real/file/name.py"
     *   "filename": "file/name.py",
     *   "function": "myfunction",
     *   "vars": {
     *       "key": "value"
     *   },
     *   "pre_context": [
     *      "line1",
     *      "line2"
     *   ],
     *   "context_line": "line3",
     *   "lineno": 3,
     *   "post_context": [
     *       "line4",
     *       "line5"
     *   ],
     * }]
     * }
     */
    public static JSONObject buildStacktrace(Throwable exception) {
        JSONArray array = new JSONArray()
        Throwable cause = exception
        while (cause != null) {
            StackTraceElement[] elements = cause.getStackTrace()
            elements.eachWithIndex { element, index ->
                if (index == 0) {
                    String msg = "Caused by: " + cause.getClass().getName()
                    if (cause.getMessage() != null) {
                        msg += " (\"" + cause.getMessage() + "\")"
                    }
                    JSONObject causedByFrame = new JSONObject([
                        filename: msg,
                        lineno: -1
                    ])
                    array.add(causedByFrame)
                }
                JSONObject frame = new JSONObject([
                    filename: element.getClassName(),
                    function: element.getMethodName(),
                    lineno: element.getLineNumber()
                ])
                array.add(frame)
            }
            cause = cause.getCause()
        }
        return new JSONObject([frames: array])
    }

    public static JSONObject buildHttp(HttpServletRequest request) {
        return new Http(request).toJSONObject()
    }

    public static String determineCulprit(Throwable exception) {
        Throwable cause = exception
        String culprit = null
        StackTraceElement[] elements = cause.getStackTrace()
        if (elements.length > 0) {
            StackTraceElement trace = elements[0]
            culprit = trace.getClassName() + "." + trace.getMethodName()
        }
        return culprit
    }
}
