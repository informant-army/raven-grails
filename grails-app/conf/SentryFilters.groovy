import grails.plugins.raven.RavenClient

class SentryFilters {
    def ravenClient
    // def springSecurityService

    def filters = {
        all(uri: '/**') {
            before = {
                // def user = springSecurityService.currentUser
                def user = [id: 123, is_authenticated: true, email: "user@email.com", username: "username"]
                if (user) {
                   ravenClient.setUserData(user)
                }
            }
        }
    }
}
