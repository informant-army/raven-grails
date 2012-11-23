import grails.plugins.sentry.SentryService

class SentryFilters {
    def sentryService
    // def springSecurityService

    def filters = {
        all(uri: '/**') {
            before = {
                // def user = springSecurityService.currentUser
                def user = [id: 123, is_authenticated: true, email: "user@email.com", username: "username"]
                if (user) {
                   sentryService.setUserData(user)
                }
            }
        }
    }
}
