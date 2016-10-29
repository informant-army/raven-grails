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

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.helpers.MDCInsertingServletFilter
import com.getsentry.raven.DefaultRavenFactory
import com.getsentry.raven.dsn.Dsn
import com.getsentry.raven.servlet.RavenServletRequestListener
import grails.plugins.Plugin
import groovy.util.logging.Commons
import org.slf4j.LoggerFactory
import org.springframework.boot.context.embedded.FilterRegistrationBean

@Commons
class SentryGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = '3.0.0 > *'

    def title = 'Sentry Plugin'
    def author = 'Benoit Hediard'
    def authorEmail = 'ben@benorama.com'
    def description = 'Sentry Client for Grails'
    def profiles = ['web']
    def documentation = 'http://github.com/agorapulse/grails-raven/blob/master/README.md'

    def license = 'APACHE'
    def developers = [[name: 'Benoit Hediard', email: 'ben@benorama.com']]
    def issueManagement = [system: 'GitHub', url: 'http://github.com/agorapulse/grails-raven/issues']
    def scm = [url: 'http://github.com/agorapulse/grails-raven']

    Closure doWithSpring() {
        { ->
            def pluginConfig = grailsApplication.config.grails?.plugin?.sentry

            if (pluginConfig.containsKey('active') && !pluginConfig.active) {
                log.warn "Raven disabled"
                return
            }

            if (pluginConfig?.dsn) {
                log.info 'Sentry config found, creating Sentry client and corresponding Logback appender'
                ravenFactory(DefaultRavenFactory)
                raven(ravenFactory: 'createRavenInstance', new Dsn(pluginConfig.dsn.toString())) { bean ->
                    bean.autowire = 'byName'
                }
                sentryAppender(GrailsLogbackSentryAppender, ref('raven'), pluginConfig, grailsApplication.metadata['info.app.version'])

                if (pluginConfig.logHttpRequest) {
                    ravenServletRequestListener(RavenServletRequestListener)
                }

                if (pluginConfig.springSecurityUser) {
                    springSecurityUserEventBuilderHelper(SpringSecurityUserEventBuilderHelper) {
                        springSecurityService = ref('springSecurityService')
                        if (pluginConfig.logHttpRequest)
                            ravenServletRequestListener = ref('ravenServletRequestListener')
                    }
                }

                if (pluginConfig?.disableMDCInsertingServletFilter != true) {
                    log.info 'Activating MDCInsertingServletFilter'
                    mdcInsertingServletFilter(FilterRegistrationBean) {
                        filter = bean(MDCInsertingServletFilter)
                        urlPatterns = ['/*']
                    }
                }
            } else {
                log.warn "Raven config not found, add 'grails.plugin.sentry.dsn' to your config to enable Sentry client"
            }
        }
    }

    void doWithApplicationContext() {
        def pluginConfig = grailsApplication.config.grails?.plugin?.sentry

        if (pluginConfig.containsKey('active') && !pluginConfig.active) {
            return
        }

        def configLoggers = pluginConfig?.loggers

        if (pluginConfig?.springSecurityUser) {
            def springSecurityUserEventBuilderHelper = applicationContext.springSecurityUserEventBuilderHelper
            applicationContext.raven.addBuilderHelper(springSecurityUserEventBuilderHelper)
        }

        GrailsLogbackSentryAppender appender = applicationContext.sentryAppender
        if (appender) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory()
            if (configLoggers) {
                def loggers = configLoggers.tokenize(',')
                loggers.each { String logger ->
                    loggerContext.getLogger(logger).addAppender(appender)
                }
            } else {
                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender)
            }
            appender.start()
        }
    }

}
