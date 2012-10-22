package grails.plugins.sentry.test

class TestController {
    def sentryService

    def error = {
        def realMessage = "ErrorMessage"
        sentryService.captureException()
    }
}
