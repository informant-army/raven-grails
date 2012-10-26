package grails.plugins.sentry.exception.filter

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * FIXME: Copied from grails-core 2.x. Remove it when upgrading to 2.x.
 */
public class SentryStackTraceFilterer {
    public static final String FULL_STACK_TRACE_MESSAGE = "Full Stack Trace:";
    public static final String SYS_PROP_DISPLAY_FULL_STACKTRACE = "grails.full.stacktrace";
    public static final String STACK_LOG_NAME = "StackTrace";
    public static final Log STACK_LOG = LogFactory.getLog(STACK_LOG_NAME);

    private static final def DEFAULT_INTERNAL_PACKAGES = [
        "org.grails.plugin.resource.DevMode",
        "org.codehaus.groovy.grails.",
        "gant.",
        "org.codehaus.groovy.runtime.",
        "org.codehaus.groovy.reflection.",
        "org.codehaus.groovy.ast.",
        "org.codehaus.gant.",
        "groovy.",
        "org.mortbay.",
        "org.apache.catalina.",
        "org.apache.coyote.",
        "org.apache.tomcat.",
        "net.sf.cglib.proxy.",
        "sun.",
        "java.lang.reflect.",
        "org.springframework.",
        "com.springsource.loaded.",
        "com.opensymphony.",
        "org.hibernate.",
        "javax.servlet."
    ]

    private List<String> packagesToFilter = new ArrayList<String>();
    private boolean shouldFilter;
    private String cutOffPackage = null;

    public SentryStackTraceFilterer() {
        this(!Boolean.getBoolean(SYS_PROP_DISPLAY_FULL_STACKTRACE));
    }

    public SentryStackTraceFilterer(boolean shouldFilter) {
        this.shouldFilter = shouldFilter;
        packagesToFilter.addAll(Arrays.asList(DEFAULT_INTERNAL_PACKAGES));
    }

    public void addInternalPackage(String name) {
        Assert.notNull(name, "Package name cannot be null");
        packagesToFilter.add(name);
    }

    public void setCutOffPackage(String cutOffPackage) {
        this.cutOffPackage = cutOffPackage;
    }

    public Throwable filter(Throwable source, boolean recursive) {
        if (recursive) {
            Throwable current = source;
            while (current != null) {
                current = filter(current);
                current = current.getCause();
            }
        }
        return filter(source);
    }

    public Throwable filter(Throwable source) {
        if (shouldFilter) {
            StackTraceElement[] trace = source.getStackTrace();
            List<StackTraceElement> newTrace = filterTraceWithCutOff(trace, cutOffPackage);

            if (newTrace.isEmpty()) {
                // filter with no cut-off so at least there is some trace
                newTrace = filterTraceWithCutOff(trace, null);
            }

            // Only trim the trace if there was some application trace on the stack
            // if not we will just skip sanitizing and leave it as is
            if (!newTrace.isEmpty()) {
                // We don't want to lose anything, so log it
                STACK_LOG.error(FULL_STACK_TRACE_MESSAGE, source);
                StackTraceElement[] clean = new StackTraceElement[newTrace.size()];
                newTrace.toArray(clean);
                source.setStackTrace(clean);
            }
        }
        return source;
    }

    private List<StackTraceElement> filterTraceWithCutOff(StackTraceElement[] trace, String endPackage) {
        List<StackTraceElement> newTrace = new ArrayList<StackTraceElement>();
        boolean foundGroovy = false;
        for (StackTraceElement stackTraceElement : trace) {
            String className = stackTraceElement.getClassName();
            String fileName = stackTraceElement.getFileName();
            if (!foundGroovy && fileName != null && fileName.endsWith(".groovy")) {
                foundGroovy = true;
            }
            if (endPackage != null && className.startsWith(endPackage) && foundGroovy) break;
            if (isApplicationClass(className)) {
                if (stackTraceElement.getLineNumber() > -1) {
                    newTrace.add(stackTraceElement);
                }
            }
        }
        return newTrace;
    }

    /**
     * Whether the given class name is an internal class and should be filtered
     * @param className The class name
     * @return True if is internal
     */
    protected boolean isApplicationClass(String className) {
        for (String packageName : packagesToFilter[0]) {
            if (className.startsWith(packageName)) return false;
        }
        return true;
    }

    public void setShouldFilter(boolean shouldFilter) {
        this.shouldFilter = shouldFilter;
    }
}
