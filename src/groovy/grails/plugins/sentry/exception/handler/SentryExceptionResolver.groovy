package grails.plugins.sentry.exception.handler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.web.servlet.ModelAndView
import org.codehaus.groovy.runtime.InvokerInvocationException
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.servlet.mvc.exceptions.GrailsMVCException
import org.codehaus.groovy.grails.web.errors.GrailsExceptionResolver

import grails.util.Environment
import grails.plugins.sentry.SentryClient
import grails.plugins.sentry.exception.filter.SentryStackTraceFilterer

/*
 * TODO: FIXME: We are using resolveException because Grails 1.3.9 don't implements logStackTrace. When upgrading to Grails 2.x, remove resolveException
 * and move code to logStackTrace.
 */
class SentryExceptionResolver extends GrailsExceptionResolver {

    SentryStackTraceFilterer stackFilterer

    @Override
    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, java.lang.Object handler, java.lang.Exception exception) {
        ModelAndView mv = super.resolveException(request, response, handler, exception)

        if (ConfigurationHolder.config.grails.plugins.sentry.active) {
            exception = findWrappedException(exception)
            stackFilterer = new SentryStackTraceFilterer(true)
            exception = stackFilterer.filter(exception, true)

            SentryClient client = new SentryClient(getDSN())
            client.logException(exception, "root", "error", request)
        }

        return mv
    }

    /*
     * FIXME: Copied from grails-core 2.x. Remove it when upgrading to 2.x.
     */
    private Exception findWrappedException(Exception e) {
        if ((e instanceof InvokerInvocationException) || (e instanceof GrailsMVCException)) {
            Throwable t = getRootCause(e)
            if (t instanceof Exception) {
                e = (Exception) t
            }
        }
        return e
    }

    /*
     * FIXME: Copied from grails-core 2.x. Remove it when upgrading to 2.x.
     */
    public static Throwable getRootCause(Throwable ex) {
        while (ex.getCause() != null && !ex.equals(ex.getCause())) {
            ex = ex.getCause()
        }
        return ex
    }

    private String getDSN() {
        return ConfigurationHolder.config.grails.plugins.sentry.dsn
    }
}
