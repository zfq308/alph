package burgee.io.alph;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AbstractLambdaProxyHandler implements RequestStreamHandler {

    private ObjectMapper mapper = new ObjectMapper();

    private List<Resource> registeredResources = new ArrayList<>();

    public final void handleRequest(final InputStream inputStream,
                                    final OutputStream outputStream,
                                    final Context context)
            throws IOException {

        final Input input = mapper.readValue(inputStream, Input.class);

        Response response;

        try {

            final String requestPath = input.getPath();

            response = registeredResources.stream()
                    .filter(resource -> resource.matches(requestPath))
                    .findFirst()
                    .orElseThrow(() -> new ResponseException(404))
                    .handle(input, context);

        } catch (ResponseException e) {
            context.getLogger().log(String.format("Request [%s] to [%s] failed with message [%s]%s",
                    input.getHttpMethod(),
                    input.getPath(),
                    e.getMessage(),
                    e.getCause() != null ? String.format(". Caused by [%s]", e.getCause().toString()) : "."));
            response = e.getResponse();
        }

        mapper.writeValue(outputStream, response);
    }

    protected void register(Resource resource) {
        registeredResources.add(resource);
    }
}
