package grails.plugins.raven

import net.kencochrane.raven.Raven
import net.kencochrane.raven.event.Event
import net.kencochrane.raven.event.EventBuilder
import net.kencochrane.raven.event.interfaces.ExceptionInterface
import net.kencochrane.raven.log4j.SentryAppender
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.log4j.Level
import org.apache.log4j.spi.LoggingEvent

class GrailsLog4jSentryAppender extends SentryAppender {

    def config

    GrailsLog4jSentryAppender(Raven raven, config) {
        super(raven)
        this.config = config
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        if (config.containsKey('active') && !config.active) {
            return
        }

        def level = loggingEvent.getLevel()
        if (level.equals(Level.ERROR) || level.equals(Level.FATAL) || level.equals(Level.WARN)) {
            super.append(loggingEvent)
        }
    }

    @Override
    protected Event buildEvent(LoggingEvent loggingEvent) {
        EventBuilder eventBuilder = new EventBuilder(
                level: formatLevel(loggingEvent.level),
                logger: loggingEvent.loggerName,
                message: loggingEvent.renderedMessage,
                timestamp: new Date(loggingEvent.timeStamp)
        ).addExtra(THREAD_NAME, loggingEvent.threadName)

        if (loggingEvent.throwableInformation) {
            Throwable throwable = loggingEvent.throwableInformation.throwable
            eventBuilder.addSentryInterface(new ExceptionInterface(throwable))
            // Determine culprit
            Throwable rootCause = ExceptionUtils.getRootCause(throwable)
            StackTraceElement[] elements = rootCause ? rootCause.getStackTrace() : throwable.getStackTrace()
            if (elements) {
                StackTraceElement trace = elements[0]
                eventBuilder.setCulprit("${trace.className}.${trace.methodName}")
            }
        } else {
            // Set default culprit (do not use locationInformation since it returns SLF4JLog class)
            eventBuilder.setCulprit(loggingEvent.loggerName)
        }

        if (loggingEvent.NDC) {
            eventBuilder.addExtra(LOG4J_NDC, loggingEvent.NDC)
        }

        Map<String, Object> properties = (Map<String, Object>) loggingEvent.properties
        for (Map.Entry<String, Object> mdcEntry : properties.entrySet()) {
            //if (extraTags.contains(mdcEntry.key)) {
            //    eventBuilder.addTag(mdcEntry.key, mdcEntry.value.toString())
            //} else {
                eventBuilder.addExtra(mdcEntry.key, mdcEntry.value)
            //}
        }

        for (Map.Entry<String, String> tagEntry : tags.entrySet()) {
            eventBuilder.addTag(tagEntry.key, tagEntry.value)
        }

        raven.runBuilderHelpers(eventBuilder)
        return eventBuilder.build()
    }

}
