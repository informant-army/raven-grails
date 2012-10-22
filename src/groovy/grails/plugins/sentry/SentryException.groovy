package grails.plugins.sentry

class SentryException extends Exception {

    SentryException() {
        super()
    }
    
    SentryException(String message) {
        super(message)
    }
    
    SentryException(Exception ex) {
        super(ex)
    }
    
    SentryException(String message, Exception ex) {
        super(message, ex)
    }
}
