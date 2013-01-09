// configuration for plugin testing - will not be included in the plugin zip
grails.plugins.raven.dsn = "https://123:123@app.getsentry.com/123"
grails.plugins.raven.dsn = "https://1f195b8762874849b7b48034a9f5c2cf:c075ecead5d748d2ae81f8402206ff96@app.getsentry.com/3615"
grails.exceptionresolver.params.exclude = ['password', 'creditCard']
grails.exceptionresolver.logRequestParameters = true

environments {
    development {
        grails.plugins.raven.active = true
    }
    production {
        grails.plugins.raven.active = false
    }
}

log4j = {
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    }

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
    debug  'grails.plugins.raven'

    root {
        warn 'stdout'
    }
}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
