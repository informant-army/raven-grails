package grails.plugins.raven.interfaces

import grails.converters.JSON
import grails.plugins.raven.interfaces.User
import grails.test.*
import org.codehaus.groovy.grails.web.json.JSONObject

class UserTests extends GroovyTestCase {

    Map userTest = [id: 123, is_authenticated: true, username: 'username', email: 'user@email.com']
    Map userTestWithAdditionalAttributes = [id: 123, is_authenticated: true, username: 'username', email: 'user@email.com', client: 'clientOne', department:"depOne"]

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

    def void testUserDataWithAdditionalAttributes(){

        User user = new User(userTestWithAdditionalAttributes.is_authenticated, userTestWithAdditionalAttributes)

        assertEquals userTestWithAdditionalAttributes.id.toString(), user.id
        assertEquals userTestWithAdditionalAttributes.username, user.username
        assertEquals userTestWithAdditionalAttributes.is_authenticated, user.is_authenticated
        assertEquals userTestWithAdditionalAttributes.email, user.email

        assertEquals userTestWithAdditionalAttributes.client, user.getData().client
        assertEquals userTestWithAdditionalAttributes.department, user.getData().department
    }

    def void testUserDataWithAdditionalAttributesToJSON(){

        User user = new User(userTestWithAdditionalAttributes.is_authenticated, userTestWithAdditionalAttributes)

        def result = user.toJSON().toString()
        def resultAsJSON = JSON.parse(result)

        assertEquals resultAsJSON.id.toString(), user.id
        assertEquals resultAsJSON.username, user.username
        assertEquals resultAsJSON.is_authenticated, user.is_authenticated
        assertEquals resultAsJSON.email, user.email

        assertEquals resultAsJSON.client, user.getData().client
        assertEquals resultAsJSON.department, user.getData().department

    }
}
