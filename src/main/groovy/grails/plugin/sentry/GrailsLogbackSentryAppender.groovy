/*
 * Copyright 2016 Alan Rafael Fachini, authors, and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import grails.util.Environment
import grails.util.Metadata

class GrailsLogbackSentryAppender extends SentryAppender {

    static defaultLoggingLevels = [Level.ERROR, Level.WARN]

    private static final String TAG_GRAILS_APP_NAME = 'grails_app_name'
    private static final String TAG_GRAILS_APP_VERSION = 'grails_app_version'
    private static final String TAG_GRAILS_VERSION = 'grails_version'

    def config
    String release

    GrailsLogbackSentryAppender(Raven raven, config, String release = '') {
        super(raven)
        this.config = config
        this.release = release
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
                .withRelease(release)

        // remove trash from message
        if (event.getFormattedMessage().contains(' Stacktrace follows:')) {
            eventBuilder.withMessage(event.getFormattedMessage().replace(' Stacktrace follows:', ''))
        }

        // remove trash from message
        if (event.getFormattedMessage().trim().equals('Full Stack Trace:')) {
            eventBuilder.withMessage(event.getFormattedMessage().trim().replace('Full Stack Trace:', ''))
        }

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

        // override "grails.plugin.sentry.GrailsLogbackSentryAppender" as culprit by more concrete message
        if (event.throwableProxy != null && event.throwableProxy.cause != null &&
                event.throwableProxy.cause.stackTraceElementProxyArray.length > 0) {
            eventBuilder.withCulprit(event.throwableProxy.cause.stackTraceElementProxyArray[0].toString())
            eventBuilder.withLogger(event.throwableProxy.cause.stackTraceElementProxyArray[0].stackTraceElement.className)
        } else if (event.getCallerData().length > 0) {
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

        Metadata metadata = Metadata.current
        eventBuilder.withTag(TAG_GRAILS_APP_NAME, metadata.getApplicationName())
        eventBuilder.withTag(TAG_GRAILS_APP_VERSION, metadata.getApplicationVersion())
        eventBuilder.withTag(TAG_GRAILS_VERSION, metadata.getGrailsVersion())

        if (config.environment) {
            eventBuilder.withEnvironment(config.environment)
        } else {
            eventBuilder.withEnvironment(Environment.current.name)
        }

        // Custom
        if (config.serverName) {
            eventBuilder.withServerName(config.serverName)
        }

        raven.runBuilderHelpers(eventBuilder)
        return eventBuilder.build()
    }

}