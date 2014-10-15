<h1>
    Raven test
</h1>

<g:if test="${!grailsApplication.config.grails?.plugin?.raven?.dsn}">
    <h2 style="color: red">
        Please add 'grails.plugin.raven.dsn' in your config.
    </h2>
</g:if>

<ul>
    <li>
        <g:link controller="test" action="clientInfo">Send info</g:link>
    </li>
    <li>
        <g:link controller="test" action="clientException">Send exception</g:link>
    </li>
    <li>
        <g:link controller="test" action="clientEvent">Send event</g:link>
    </li>
    <li>
        <g:link controller="test" action="error">Throw exception</g:link>
    </li>
    <li>
        <g:link controller="test" action="testLog">Log error</g:link>
    </li>
</ul>
