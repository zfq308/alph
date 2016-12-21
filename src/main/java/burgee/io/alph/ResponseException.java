package burgee.io.alph;

public class ResponseException extends RuntimeException {
    private Response response;

    public ResponseException() {
        response = Response.error();
    }

    public ResponseException(final int statusCode) {
        response = Response.builder().status(statusCode).build();
    }

    public ResponseException(final int statusCode, final String message) {
        super(message);
        response = Response.builder().status(statusCode).stringBody(message).build();
    }

    public ResponseException(final int statusCode, final String message, final Throwable cause) {
        super(message, cause);
        response = Response.builder().status(statusCode).stringBody(message).build();
    }

    public Response getResponse() {
        return this.response;
    }
}
