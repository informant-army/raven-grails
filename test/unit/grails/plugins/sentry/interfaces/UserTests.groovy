package grails.plugins.sentry.interfaces

import grails.test.*
import org.codehaus.groovy.grails.web.json.JSONObject

class UserTests extends GroovyTestCase {

    Map userTest = [id: 123, is_authenticated: true, username: 'username', email: 'user@email.com']

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    def void testUserData() {
        User user = new User(userTest.is_authenticated, userTest)

        assertEquals userTest.id.toString(), user.id
        assertEquals userTest.username, user.username
        assertEquals userTest.is_authenticated, user.is_authenticated
        assertEquals userTest.email, user.email
    }

    def void testUserDataToJSON() {
        User user = new User(userTest.is_authenticated, userTest)

        def result = user.toJSON().toString()
        assertEquals '{"id":"123","username":"username","email":"user@email.com","is_authenticated":true}', result
    }
}
