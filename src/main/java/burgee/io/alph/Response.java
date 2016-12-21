package burgee.io.alph;

// Example JSON from: https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-set-up-simple-proxy
// .html#api-gateway-simple-proxy-for-lambda-input-format
//{
//    "statusCode": httpStatusCode,
//    "headers": { "headerName": "headerValue", ... },
//    "body": "..."
//}

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private int statusCode;
    private Map<String, String> headers;
    private String body;
    private Response(final int statusCode, final Map<String, String> headers, final String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static Response error() {
        return new Response(500, Collections.emptyMap(), "");
    }

    public static Response notFound() {
        return new Response(404, Collections.emptyMap(), "");
    }

    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }

    public static class ResponseBuilder {
        private int statusCode = 200;
        private Map<String, String> headers = Collections.emptyMap();
        private String body = "";

        public ResponseBuilder status(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public ResponseBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public ResponseBuilder header(String key, String value) {
            if (this.headers.isEmpty()) headers = new HashMap<>();
            headers.put(key, value);
            return this;
        }

        public ResponseBuilder stringBody(String body) {
            this.body = body;
            this.headers.put("Content-Type", "text/plain");
            return this;
        }

        public <T> ResponseBuilder jsonBody(T body) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                this.body = mapper.writeValueAsString(body);
            } catch (JsonProcessingException e) {
                throw new ResponseException(500, "Failed to serialise response body", e);
            }
            this.headers.put("Content-Type", "application/json");
            return this;
        }

        public Response build() {
            return new Response(this.statusCode, this.headers, this.body);
        }
    }
}
