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
    def defaultLoggingLevels = [Level.ERROR, Level.FATAL, Level.WARN]

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

        if(config.levels) {
            // getting the user defined logging levels and capitalizing them
            def configLoggingLevels = config.levels.tokenize(',').collect { Level.toLevel(it.replaceAll('\\s','')) }

            if(configLoggingLevels && configLoggingLevels.contains(level)) {
                super.append(loggingEvent)
            }
        } else if (defaultLoggingLevels.contains(level)) {
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
        ).withExtra(THREAD_NAME, loggingEvent.threadName)

        if (loggingEvent.throwableInformation) {
            Throwable throwable = loggingEvent.throwableInformation.throwable
            eventBuilder.withSentryInterface(new ExceptionInterface(throwable))
            // Determine culprit
            Throwable rootCause = ExceptionUtils.getRootCause(throwable)
            StackTraceElement[] elements = rootCause ? rootCause.getStackTrace() : throwable.getStackTrace()
            if (elements) {
                StackTraceElement trace = elements[0]
                eventBuilder.withCulprit("${trace.className}.${trace.methodName}")
            }
        } else {
            // Set default culprit (do not use locationInformation since it returns SLF4JLog class)
            eventBuilder.withCulprit(loggingEvent.loggerName)
        }

        if (loggingEvent.NDC) {
            eventBuilder.withExtra(LOG4J_NDC, loggingEvent.NDC)
        }

        Map<String, Object> properties = (Map<String, Object>) loggingEvent.properties
        for (Map.Entry<String, Object> mdcEntry : properties.entrySet()) {
            //if (extraTags.contains(mdcEntry.key)) {
            //    eventBuilder.addTag(mdcEntry.key, mdcEntry.value.toString())
            //} else {
                eventBuilder.withExtra(mdcEntry.key, mdcEntry.value)
            //}
        }

        for (Map.Entry<String, String> tagEntry : tags.entrySet()) {
            eventBuilder.withTag(tagEntry.key, tagEntry.value)
        }

        if(config.serverName) {
            eventBuilder.withServerName(config.serverName)
        }

        if(config.tags) {
            def tags = config.tags

            tags.each { tagKey, tagVal ->
                // removing all spaces and splitting by ':'
                if (tagKey && tagVal)
                    eventBuilder.withTag(tagKey.replaceAll('\\s', ''),
                            tagVal.replaceAll('\\s', ''))
            }
        }

        if (config.subsystems) {
            def loggingCategory = loggingEvent.logger?.name
            def subsystems = config.subsystems

            // Set default subsystem as MISC
            def subsystemName = 'MISC'

            if (loggingCategory) {
                // if package name starts with org its a lib exception
                if (loggingCategory.startsWith('org.')) {
                    subsystemName = 'LIBRARY'
                } else {
                    subsystems.each { name, packageList ->
                        packageList.each { packageName ->
                            if (loggingCategory.contains(packageName)) {
                                subsystemName = name
                            }
                        }
                    }
                }
            }
            eventBuilder.withTag('subsystem', subsystemName)
        }

        raven.runBuilderHelpers(eventBuilder)
        return eventBuilder.build()
    }

}
