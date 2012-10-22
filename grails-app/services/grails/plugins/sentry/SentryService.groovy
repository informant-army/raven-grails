package grails.plugins.sentry

import net.kencochrane.sentry.RavenClient
import net.kencochrane.sentry.RavenUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SentryService {
    static transactional = false

    SentryClient client

    void captureMessage(String errorMessage, Throwable throwable = null) {
        client = new SentryClient(getDSN())
        client.captureMessage(errorMessage, RavenUtils.getTimestampLong(), "root", 50, null)
    }

    void captureException() {

        client = new SentryClient(getDSN())
        client.captureException(new SentryException('Not implemented'))
    }

    private String getDSN() {
        return ConfigurationHolder.config.grails.plugins.sentry.dsn
    }
}
