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
