package grails.plugins.sentry.test

import grails.plugins.sentry.SentryClient

class SentryFilters {
    def sentryClient
    // def springSecurityService

    def filters = {
        all(uri: '/**') {
            before = {
                // def user = springSecurityService.currentUser
                def user = [id: 123, is_authenticated: true, email: "user@email.com", username: "username"]
                if (user) {
                   sentryClient.setUserData(user)
                }
            }
        }
    }
}
