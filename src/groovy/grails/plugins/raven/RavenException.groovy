package grails.plugins.raven

class RavenException extends Exception {

    RavenException() {
        super()
    }
    
    RavenException(String message) {
        super(message)
    }
    
    RavenException(Exception ex) {
        super(ex)
    }
    
    RavenException(String message, Exception ex) {
        super(message, ex)
    }
}
