package grails.plugins.sentry

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.security.SignatureException
import java.net.Proxy

public class SentryConnection {

    private SentryConfiguration config

    public SentryConnection(SentryConfiguration config) {
        this.config = config
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
        String hmacSignature = getSignature("$timestamp $messageBody", config.secretKey)

        HttpURLConnection connection = getConnection()
        connection.setRequestMethod("POST")
        connection.setDoOutput(true)
        connection.setReadTimeout(10000)
        connection.setRequestProperty("X-Sentry-Auth", buildAuthHeader(hmacSignature, timestamp, config.publicKey))
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
        header += "sentry_client=${config.clientVersion}"
        return header
    }

    private HttpURLConnection getConnection() throws IOException {
        return (HttpURLConnection) config.endpoint.openConnection(Proxy.NO_PROXY)
    }

    private getSignature(String data, String key) throws SignatureException {
        String result
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1")
            Mac mac = Mac.getInstance("HmacSHA1")
            mac.init(signingKey)
            byte[] rawHmac = mac.doFinal(data.getBytes())
            result = rawHmac.encodeHex()
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC: " + e.getMessage())
        }
        return result
    }
}
