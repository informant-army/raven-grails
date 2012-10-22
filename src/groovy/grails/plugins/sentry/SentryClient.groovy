package grails.plugins.sentry

import net.kencochrane.sentry.RavenConfig
import net.kencochrane.sentry.RavenUtils

import java.util.Date
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

    public void logMessage(String log, String loggerClass, int logLevel, String culprit, Throwable exception) {
        long timestamp = timestampLong()
        String message = buildMessage(log, timestampString(timestamp), loggerClass, logLevel, culprit, exception)
        send(message, timestamp)
    }

    def captureMessage(String message, String loggerClass, int logLevel, String culprit) {
        long timestamp = timestampLong()
        String body = buildMessage(message, timestampString(timestamp), loggerClass, logLevel, culprit, null)
        send(body, timestamp)
    }

    def captureMessage(String message) {
        captureMessage(message, "root", 50, null)
    }

    def captureException(String message, long timestamp, String loggerClass, int logLevel, String culprit, Throwable exception) {
        String body = buildMessage(message, RavenUtils.getTimestampString(timestamp), loggerClass, logLevel, culprit, exception);
        send(body, timestamp)
    }

    def captureException(Throwable exception) {
        captureException(exception.getMessage(), RavenUtils.getTimestampLong(), "root", 50, null, exception)
    }

    private void send(String message, long timestamp) {
        try {
            connection.send(message, timestamp)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    private String buildMessage(String message, String timestamp, String loggerClass, int logLevel, String culprit, Throwable exception) {
        SentryJSON json = new SentryJSON(this.config)
        String jsonMessage = json.build(message, timestamp, loggerClass, logLevel, culprit, exception)

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
