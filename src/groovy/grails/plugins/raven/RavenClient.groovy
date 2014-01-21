package grails.plugins.raven

import grails.plugins.raven.interfaces.User
import org.apache.commons.lang.time.DateFormatUtils
import org.apache.log4j.Level
import org.apache.log4j.spi.LoggingEvent
import org.springframework.web.context.request.RequestContextHolder

import javax.servlet.http.HttpServletRequest

class RavenClient {

    private URL endpoint
    private Connection connection
    private Configuration config

    public RavenClient(String dsn) {
        this(new Configuration([dsn:dsn]))
    }

    public RavenClient(Configuration config) {
        this.config = config
        this.connection = new Connection(config)
    }

    def captureMessage(String message) {
        send(message, null, 'root', 'info', null, null)
    }

    def captureMessage(String message, String loggerName, String logLevel) {
        send(exception.getMessage(), null, loggerName, logLevel, null, null)
    }

    def captureException(Throwable exception) {
        send(exception.getMessage(), null, 'root', 'error', null, null)
    }

    def captureException(Throwable exception, String loggerName, String logLevel, HttpServletRequest request) {
        send(exception.getMessage(), exception, loggerName, logLevel, request, null)
    }

    def captureEvent(LoggingEvent event, HttpServletRequest request, Map currentUser) {
        long timestamp = timestampLong()
        Level level = event.getLevel()
        String logLevel = (level ? level.toString().toLowerCase() : "root")
        String message = event.message.toString()
        String loggerName = event.getLoggerName()
        def exception = event.throwableInformation?.throwable

        send(message, exception, loggerName, logLevel, request, currentUser)
    }

    private void send(String message, Throwable exception, String loggerName, String logLevel, HttpServletRequest request, Map userData) {
        if (config.active) {
            String eventId = generateEventId()
            String checksum = generateChecksum(message)
            long timestamp = timestampLong()
            User user = (userData ? new User(userData.is_authenticated, userData) : null)

            String body = buildMessage(eventId, message, checksum, exception, loggerName, logLevel, request, user, timestamp)
            doSend(body, timestamp)
        }
    }

    private void doSend(String message, long timestamp) {
        try {
            connection.send(message, timestamp)
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    private String buildMessage(String eventId, String message, String checksum, Throwable exception, String loggerName, String logLevel, HttpServletRequest request, User user, Long timestamp) {
        String jsonMessage = Events.build(eventId, message, checksum, exception, loggerName, logLevel, request, user, timestampString(timestamp), config)

        return buildMessageBody(jsonMessage)
    }

    private String buildMessageBody(String jsonMessage) {
        return jsonMessage.bytes.encodeBase64().toString()
    }

    def setUserData(Map user) {
        def request = RequestContextHolder.currentRequestAttributes().getRequest()
        request['sentryUserData'] = user
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
