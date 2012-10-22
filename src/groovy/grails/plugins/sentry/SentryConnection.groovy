package grails.plugins.sentry

import net.kencochrane.sentry.RavenConfig
import net.kencochrane.sentry.RavenUtils

public class SentryConnection {

    private static final CLIENT_VERSION = 'Raven-grails 0.1'

    private RavenConfig config
    private URL endpoint

    public SentryConnection(RavenConfig config) {
        this.config = config
        this.endpoint = new URL(config.getSentryURL())
    }

    /*
     * POST /api/store/
     * User-Agent: raven-python/1.0
     * X-Sentry-Auth: Sentry sentry_version=2.0, sentry_timestamp=1329096377,
     * sentry_key=b70a31b3510c4cf793964a185cfe1fd0, sentry_client=raven-python/1.0
     *
     * {
     *   "project": "default",
     *    "event_id": "fc6d8c0c43fc4630ad850ee518f1b9d0",
     *    "culprit": "my.module.function_name",
     *    "timestamp": "2011-05-02T17:41:36",
     *    "message": "SyntaxError: Wattttt!",
     *    "sentry.interfaces.Exception": {
     *        "type": "SyntaxError",
     *        "value": "Wattttt!",
     *        "module": "__builtins__"
     *    }
     * }
     */
    public void send(String messageBody, long timestamp) throws IOException {
        String hmacSignature = RavenUtils.getSignature(messageBody, timestamp, config.getSecretKey())

        HttpURLConnection connection = getConnection()
        connection.setRequestMethod("POST")
        connection.setDoOutput(true)
        connection.setReadTimeout(10000)
        connection.setRequestProperty("X-Sentry-Auth", buildAuthHeader(hmacSignature, timestamp, config.getPublicKey()))
        OutputStream output = connection.getOutputStream()
        output.write(messageBody.getBytes())
        output.close()
        connection.connect()
        InputStream input = connection.getInputStream()
        input.close()
    }

    private String buildAuthHeader(String hmacSignature, long timestamp, String publicKey) {
        String header = "Sentry sentry_version=2.0,sentry_signature=${hmacSignature},"
        header += "sentry_timestamp=${timestamp},"
        header += "sentry_key=${publicKey},"
        header += "sentry_client=${CLIENT_VERSION}"
        return header
    }

    private HttpURLConnection getConnection() throws IOException {
        return (HttpURLConnection) endpoint.openConnection(config.getProxy())
    }
}
