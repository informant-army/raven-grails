package grails.plugins.sentry

import java.net.InetAddress
import java.net.UnknownHostException
import org.codehaus.groovy.grails.web.json.*
import net.kencochrane.sentry.RavenUtils

/*
 * http://sentry.readthedocs.org/en/latest/developer/interfaces/index.html
 */
class SentryJSON {

    public SentryJSON() {
    }

    String buildJSON(String message, String timestamp, String loggerClass, int logLevel, String culprit, Throwable exception) {
        JSONObject json = new JSONObject([
            event_id: RavenUtils.getRandomUUID(),
            //project: project, // lipper
            //server_name: server_name, // app.lipper.com.br
            //tags: tags, // [grails_version: 1.3.9]
            timestamp: timestamp,
            message: message,
            checksum: RavenUtils.calculateChecksum(message),
            level: logLevel,
            logger: loggerClass
        ])
        if (exception) {
            json.put('culprit', determineCulprit(exception))
            json.put('sentry.interfaces.Exception', buildException(exception))
            json.put('sentry.interfaces.Stacktrace', buildStacktrace(exception))
        } else {
            json.put('culprit', culprit)
        }
        return json.toString()
    }

    /*
     * sentry.interfaces.Exception
     * {
     *   "type": "ValueError",
     *   "value": "My exception value",
     *   "module": "__builtins__"
     * }
    */
    JSONObject buildException(Throwable exception) {
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
   def buildStacktrace(Throwable exception) {
        JSONArray array = new JSONArray()
        Throwable cause = exception
        while (cause != null) {
            StackTraceElement[] elements = cause.getStackTrace()
            elements.eachWithIndex() { element, index ->
                if (index == 0) {
                    String msg = "Caused by: ${cause.getClass().getName()}"
                    if (cause.getMessage() != null) {
                        msg += " (\"${cause.getMessage()}\")"
                    }
                    JSONObject causedByFrame = new JSONObject()
                    causedByFrame.put('filename', msg)
                    causedByFrame.put('lineno', -1)
                    array.add(causedByFrame)
                }

                JSONObject frame = new JSONObject()
                frame.put('filename', element.getClassName())
                frame.put('function', element.getMethodName())
                frame.put('lineno', element.getLineNumber())
                array.add(frame)
            }
            cause = cause.getCause()
        }

        JSONObject stacktrace = new JSONObject()
        stacktrace.put('frames', array)
        return stacktrace
    }

    def determineCulprit(Throwable exception) {
        Throwable cause = exception
        String culprit = null
            StackTraceElement[] elements = cause.getStackTrace()
            if (elements.length > 0) {
                StackTraceElement trace = elements[0]
                culprit = trace.getClassName() + "." + trace.getMethodName()
            }
        return culprit
    }

    private String getHostname() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            // can't get hostname
            hostname = "unavailable";
        }
        return hostname;
    }
}
