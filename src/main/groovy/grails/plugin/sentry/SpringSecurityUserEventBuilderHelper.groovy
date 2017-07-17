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

import grails.util.Holders
import io.sentry.event.EventBuilder
import io.sentry.event.helper.EventBuilderHelper
import io.sentry.event.interfaces.UserInterface
import io.sentry.servlet.SentryServletRequestListener

import javax.servlet.http.HttpServletRequest

/**
 * @author <a href='mailto:alexey@zhokhov.com'>Alexey Zhokhov</a>
 */
class SpringSecurityUserEventBuilderHelper implements EventBuilderHelper {

    static List<String> ipHeaders = ['X-Real-IP',
                                     'Client-IP',
                                     'X-Forwarded-For',
                                     'Proxy-Client-IP',
                                     'WL-Proxy-Client-IP',
                                     'rlnclientipaddr']

    def springSecurityService
    SentryServletRequestListener sentryServletRequestListener

    @Override
    void helpBuildingEvent(EventBuilder eventBuilder) {
        def isLoggedIn = springSecurityService?.isLoggedIn()

        if (isLoggedIn) {
            def principal = springSecurityService.getPrincipal()

            if (principal != null && principal != 'anonymousUser') {
                def sentryConfig = Holders.config.grails?.plugin?.sentry

                String idPropertyName = 'id'
                String emailPropertyName = null
                String usernamePropertyName = 'username'

                if (sentryConfig?.springSecurityUserProperties &&
                        sentryConfig?.springSecurityUserProperties instanceof Map) {
                    if (sentryConfig.springSecurityUserProperties.id &&
                            sentryConfig.springSecurityUserProperties.id instanceof String) {
                        idPropertyName = sentryConfig.springSecurityUserProperties.id
                    }
                    if (sentryConfig.springSecurityUserProperties.email &&
                            sentryConfig.springSecurityUserProperties.email instanceof String) {
                        emailPropertyName = sentryConfig.springSecurityUserProperties.email
                    }
                    if (sentryConfig.springSecurityUserProperties.username &&
                            sentryConfig.springSecurityUserProperties.username instanceof String) {
                        usernamePropertyName = sentryConfig.springSecurityUserProperties.username
                    }
                }

                def id = principal[idPropertyName].toString()
                String username = principal[usernamePropertyName].toString()
                String ipAddress = getIpAddress(sentryServletRequestListener?.getServletRequest())
                String email = emailPropertyName ? principal[emailPropertyName].toString() : null
                UserInterface userInterface = new UserInterface(id, username, ipAddress, email)
                eventBuilder.withSentryInterface(userInterface, true)
            }
        }
    }

    String getIpAddress(HttpServletRequest request) {
        String unknown = 'unknown'
        String ipAddress = unknown

        if (request) {
            ipHeaders.each { header ->
                if (!ipAddress || unknown.equalsIgnoreCase(ipAddress))
                    ipAddress = request.getHeader(header)
            }

            if (!ipAddress)
                ipAddress = request.remoteAddr
        }

        return ipAddress
    }

}
