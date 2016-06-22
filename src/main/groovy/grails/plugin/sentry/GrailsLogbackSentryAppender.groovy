package grails.plugin.sentry

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import com.getsentry.raven.Raven
import com.getsentry.raven.event.Event
import com.getsentry.raven.event.EventBuilder
import com.getsentry.raven.event.interfaces.ExceptionInterface
import com.getsentry.raven.event.interfaces.MessageInterface
import com.getsentry.raven.event.interfaces.StackTraceInterface
import com.getsentry.raven.logback.SentryAppender

class GrailsLogbackSentryAppender extends SentryAppender {

    static defaultLoggingLevels = [Level.ERROR, Level.WARN]

    def config

    GrailsLogbackSentryAppender(Raven raven, config) {
        super(raven)
        this.config = config
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (config.containsKey('active') && !config.active) {
            return
        }

        def level = event.level
        if (config.levels) {
            // Getting the user defined logging levels and capitalizing them
            def configLoggingLevels = config.levels.collect { Level.toLevel(it.replaceAll('\\s', '')) }
            if (configLoggingLevels && configLoggingLevels.contains(level)) {
                super.append(event)
            }
        } else if (defaultLoggingLevels.contains(level)) {
            super.append(event)
        }
    }

    @Override
    protected Event buildEvent(ILoggingEvent event) {
        EventBuilder eventBuilder = new EventBuilder()
                .withTimestamp(new Date(event.getTimeStamp()))
                .withMessage(event.getFormattedMessage())
                .withLogger(event.getLoggerName())
                .withLevel(formatLevel(event.getLevel()))
                .withExtra(THREAD_NAME, event.getThreadName())

        if (event.argumentArray) {
            eventBuilder.withSentryInterface(
                    new MessageInterface(event.message, formatMessageParameters(event.argumentArray))
            )
        }

        if (event.getThrowableProxy() != null) {
            eventBuilder.withSentryInterface(new ExceptionInterface(extractExceptionQueue(event)))
        } else if (event.getCallerData().length > 0) {
            eventBuilder.withSentryInterface(new StackTraceInterface(event.getCallerData()))
        }

        if (event.getCallerData().length > 0) {
            eventBuilder.withCulprit(event.getCallerData()[0])
        } else {
            eventBuilder.withCulprit(event.getLoggerName())
        }

        for (Map.Entry<String, String> contextEntry : event.loggerContextVO.propertyMap.entrySet()) {
            eventBuilder.withExtra(contextEntry.key, contextEntry.value)
        }

        for (Map.Entry<String, String> mdcEntry : event.getMDCPropertyMap().entrySet()) {
            if (extraTags.contains(mdcEntry.key)) {
                eventBuilder.withTag(mdcEntry.key, mdcEntry.value)
            } else {
                eventBuilder.withExtra(mdcEntry.key, mdcEntry.value)
            }
        }

        if (event.marker) {
            eventBuilder.withTag(LOGBACK_MARKER, event.marker.name)
        }

        for (Map.Entry<String, String> tagEntry : tags.entrySet()) {
            eventBuilder.withTag(tagEntry.key, tagEntry.value)
        }

        // Custom
        if (config.serverName) {
            eventBuilder.withServerName(config.serverName)
        }

        raven.runBuilderHelpers(eventBuilder)
        return eventBuilder.build()
    }

}