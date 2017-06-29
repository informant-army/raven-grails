package grails.plugin.sentry

import grails.test.mixin.integration.Integration
import spock.lang.Specification

@Integration
class SanityIntegrationSpec extends Specification {

    def sentryAppender
    def sentryClient
    def sentryFactory
    def sentryServletRequestListener

    def "everything works"() {
        expect: "if everything is ok sentry then beans are injected"
            sentryAppender
            sentryClient
            sentryFactory
            sentryServletRequestListener
    }

}
