package grails.plugins.sentry

import java.util.Date
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import org.apache.commons.lang.time.DateFormatUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.apache.log4j.Level
import org.apache.log4j.spi.LoggingEvent
import static org.apache.commons.codec.binary.Base64.encodeBase64String
import grails.plugins.sentry.interfaces.User
import java.util.UUID

class SentryClient {

    private URL endpoint
    private SentryConnection connection
    private SentryConfiguration config

    public SentryClient(String dsn) {
        this(new SentryConfiguration([dsn:dsn]))
    }

    public SentryClient(SentryConfiguration config) {
        this.config = config
        this.connection = new SentryConnection(config)
    }

    def logInfo(String message) {
        send(message, null, 'root', 'info', null, null)
    }

    def logMessage(String message, String loggerName, String logLevel) {
        send(exception.getMessage(), null, loggerName, logLevel, null, null)
    }

    def logException(Throwable exception) {
        send(exception.getMessage(), null, 'root', 'error', null, null)
    }

    def logException(Throwable exception, String loggerName, String logLevel, HttpServletRequest request) {
        send(exception.getMessage(), exception, loggerName, logLevel, request, null)
    }

    def logEvent(LoggingEvent event, HttpServletRequest request, Map currentUser) {
        long timestamp = timestampLong()
        Level level = event.getLevel()
        String logLevel = (level ? level.toString().toLowerCase() : "root")
        String message = event.message.toString()
        String loggerName = event.getLoggerName()
        def exception = event.throwableInformation?.throwable

        send(message, exception, loggerName, logLevel, request, currentUser)
    }

    private void send(String message, Throwable exception, String loggerName, String logLevel, HttpServletRequest request, Map userData) {
        String eventId = generateEventId()
        String checksum = generateChecksum(message)
        long timestamp = timestampLong()
        User user = (userData ? new User(userData.is_authenticated, userData) : null)

        String body = buildMessage(eventId, message, checksum, exception, loggerName, logLevel, request, user, timestamp)
        doSend(body, timestamp)
    }

    private void doSend(String message, long timestamp) {
        try {
            connection.send(message, timestamp)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    private String buildMessage(String eventId, String message, String checksum, Throwable exception, String loggerName, String logLevel, HttpServletRequest request, User user, Long timestamp) {
        String jsonMessage = SentryJSON.build(eventId, message, checksum, exception, loggerName, logLevel, request, user, timestampString(timestamp), config)

        return buildMessageBody(jsonMessage)
    }

    private String buildMessageBody(String jsonMessage) {
        return encodeBase64String(jsonMessage.getBytes())
    }

//// Utils

    private String generateEventId() {
        return (UUID.randomUUID() as String).replaceAll("-", "")
    }

    private String generateChecksum(String message) {
        return message.encodeAsMD5()
    }

    private long timestampLong() {
        return System.currentTimeMillis()
    }

    private String timestampString(long timestamp) {
        java.util.Date date = new java.util.Date(timestamp)
        return DateFormatUtils.formatUTC(date, DateFormatUtils.ISO_DATETIME_FORMAT.getPattern())
    }
}
