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

import com.getsentry.raven.event.EventBuilder
import com.getsentry.raven.event.helper.EventBuilderHelper
import com.getsentry.raven.event.interfaces.UserInterface

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
    def ravenServletRequestListener

    @Override
    void helpBuildingEvent(EventBuilder eventBuilder) {
        def currentUser = springSecurityService?.getCurrentUser()

        if (currentUser) {
            def SpringSecurityUtils = Class.forName('grails.plugin.springsecurity.SpringSecurityUtils')

            def securityConfig = SpringSecurityUtils.securityConfig

            String usernamePropertyName = securityConfig.userLookup.usernamePropertyName
            String emailPropertyName = securityConfig.userLookup.emailPropertyName

            def id = currentUser.id?.toString()
            String username = currentUser[usernamePropertyName]
            String ipAddress = getIpAddress(ravenServletRequestListener?.getServletRequest())
            String email = emailPropertyName ? currentUser[emailPropertyName] : null
            UserInterface userInterface = new UserInterface(id, username, ipAddress, email)
            eventBuilder.withSentryInterface(userInterface, true)
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
