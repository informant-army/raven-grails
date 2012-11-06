package grails.plugins.sentry

import net.kencochrane.sentry.RavenConfig
import java.util.Date
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import org.apache.commons.lang.time.DateFormatUtils
import static org.apache.commons.codec.binary.Base64.encodeBase64String

class SentryClient {

    private URL endpoint
    private SentryConnection connection
    private RavenConfig config
    private String dsn

    public SentryClient(String dsn) {
        this.dsn = dsn
        this.config = new RavenConfig(dsn)
        this.connection = new SentryConnection(config)
    }

    def logInfo(String message) {
        logMessage(message, "root", 'info')
    }

    def logMessage(String message, String loggerName, String logLevel) {
        long timestamp = timestampLong()
        String body = buildMessage(message, loggerName, logLevel, timestampString(timestamp))
        send(body, timestamp)
    }

    def logException(Throwable exception) {
        logException(exception, "root", "error")
    }

    def logException(Throwable exception, String loggerName, String logLevel)  {
        logException(exception, loggerName, logLevel, null)
    }

    def logException(Throwable exception, String loggerName, String logLevel, HttpServletRequest request) {
        long timestamp = timestampLong()
        String body = buildMessage(exception.getMessage(), exception, loggerName, logLevel, request, timestampString(timestamp))
        send(body, timestamp)
    }

    private void send(String message, long timestamp) {
        try {
            connection.send(message, timestamp)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    private String buildMessage(String message, String loggerName, String logLevel, String timestamp) {
        return buildMessage(message, null, loggerName, logLevel, null, timestamp)
    }

    private String buildMessage(String message, Throwable exception, String loggerName, String logLevel, HttpServletRequest request, String timestamp) {
        SentryJSON json = new SentryJSON(this.config)
        String jsonMessage = json.build(message, exception, loggerName, logLevel, request, timestamp)

        return buildMessageBody(jsonMessage)
    }

    private String buildMessageBody(String jsonMessage) {
        return encodeBase64String(jsonMessage.getBytes())
    }

//// Utils

    private long timestampLong() {
        return System.currentTimeMillis();
    }

    private String timestampString(long timestamp) {
        java.util.Date date = new java.util.Date(timestamp)
        return DateFormatUtils.formatUTC(date, DateFormatUtils.ISO_DATETIME_FORMAT.getPattern())
    }
}
