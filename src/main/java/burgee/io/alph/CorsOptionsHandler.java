package burgee.io.alph;

import burgee.io.alph.Resource.MethodHandler;
import com.amazonaws.services.lambda.runtime.Context;

public class CorsOptionsHandler implements MethodHandler {
    private String allowMethods;
    private String allowHeaders;
    private String allowOrigin;

    public CorsOptionsHandler(final String allowMethods, final String allowHeaders, final String allowOrigin) {
        this.allowMethods = allowMethods;
        this.allowHeaders = allowHeaders;
        this.allowOrigin = allowOrigin;
    }

    @Override
    public Response apply(final Input input, final Context context) {
        return Response.builder()
                .status(200)
                .header("Access-Control-Allow-Methods", allowMethods)
                .header("Access-Control-Allow-Headers", allowHeaders)
                .header("Access-Control-Allow-Origin", allowOrigin)
                .build();
    }
}
