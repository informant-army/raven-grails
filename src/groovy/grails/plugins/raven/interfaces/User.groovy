package grails.plugins.raven.interfaces

import org.codehaus.groovy.grails.web.json.JSONObject

class User {

    boolean is_authenticated
    String id
    String username
    String email

    Map<String,String> userAttributes

    public User(boolean is_authenticated, Map<String, String> data) {
        this.is_authenticated = is_authenticated
        setData(data)
    }

    public void setData(Map<String, String> data) {
        this.id = data.id?: ''
        this.username = data.username?: ''
        this.email = data.email?: ''
        this.userAttributes = data
    }

    public Map<String, String> getData() {
        def userDataMap = [
            is_authenticated: this.is_authenticated,
            id: this.id?: '',
            username: this.username?: '',
            email: this.email?: ''
        ]

        userAttributes.entrySet().each {entry ->
            if(!userDataMap.containsKey(entry.key)){
                userDataMap.put(entry.key, entry.value)
            }
        }
        return userDataMap
    }

    public toJSON() {
        return new JSONObject(getData())
    }
}
