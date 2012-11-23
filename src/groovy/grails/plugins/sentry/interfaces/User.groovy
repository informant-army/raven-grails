package grails.plugins.sentry.interfaces

import org.codehaus.groovy.grails.web.json.JSONObject

class User {

    boolean is_authenticated
    String id
    String username
    String email

    public User(boolean is_authenticated, Map<String, String> data) {
        this.is_authenticated = is_authenticated
        setData(data)
    }

    public void setData(Map<String, String> data) {
        this.id = data.id?: ''
        this.username = data.username?: ''
        this.email = data.email?: ''
    }

    public Map<String, String> getData() {
        return [
            is_authenticated: this.is_authenticated,
            id: this.id?: '',
            username: this.username?: '',
            email: this.email?: ''
        ]
    }

    public toJSON() {
        return new JSONObject(getData())
    }
}
