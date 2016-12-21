package burgee.io.alph;

import com.amazonaws.services.lambda.runtime.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ExampleHandler extends AbstractLambdaProxyHandler {

    private static String getOneRegex = "/(?<id>[0-9]+)";

    public Map<Integer, Example> examples;
    //Lazy initialized pattern
    private Pattern getOnePattern = null;

    public ExampleHandler() {
        examples = new HashMap<>();
        examples.put(1, new Example("Example1"));
        examples.put(2, new Example("Example2"));

        register(Resource.builder().match("/").handle(Method.GET, this::getAll).build());
        register(Resource.builder().match(getOneRegex).handle(Method.GET, this::getOne).build());
    }

    private Response getAll(Input input, Context context) {
        return Response.builder().status(200).jsonBody(examples).build();
    }

    private Response getOne(Input input, Context context) {
        if (getOnePattern == null) getOnePattern = Pattern.compile(getOneRegex);

        final String id = getOnePattern.matcher(input.getPath()).group("id");
        final Example example = examples.get(id);

        if (example != null) {
            return Response.builder().status(200).jsonBody(examples.get(id)).build();
        } else {
            return Response.notFound();
        }
    }

    public static class Example {
        private String text;

        public Example(final String text) {
            this.text = text;
        }
    }
}
