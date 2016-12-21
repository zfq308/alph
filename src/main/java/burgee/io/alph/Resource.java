package burgee.io.alph;

import com.amazonaws.services.lambda.runtime.Context;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Resource {

    private final String pathRegex;
    private final Map<Method, MethodHandler> methods;
    private final boolean enableCors;

    private Resource(final String pathRegex, final Map<Method, MethodHandler> supportedMethods,
                    final boolean enableCors) {
        this.pathRegex = pathRegex;
        this.methods = supportedMethods;
        this.enableCors = enableCors;
    }

    public static ResourceBuilder builder() {
        return new ResourceBuilder();
    }

    public boolean matches(final String requestPath) {
        return requestPath.matches(pathRegex);
    }

    public Response handle(final Input input, final Context context) {
        if (methods.containsKey(input.getHttpMethod())) {
            return methods.get(input.getHttpMethod()).apply(input, context);
        } else {
            throw new ResponseException(405);
        }
    }

    public interface MethodHandler extends BiFunction<Input, Context, Response> {
    }

    public static class ResourceBuilder {
        private String pathRegex = "";
        private Map<Method, MethodHandler> methods = new HashMap<>();

        private boolean enableCors = true;
        private String allowMethods = "DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT";
        private String allowHeaders = "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token";
        private String allowOrigin = "*";

        public ResourceBuilder match(String pathRegex) {
            this.pathRegex = pathRegex;
            return this;
        }

        public ResourceBuilder handle(Method method, MethodHandler handler) {
            methods.put(method, handler);
            return this;
        }

        public ResourceBuilder disableCors() {
            enableCors = false;
            return this;
        }

        public ResourceBuilder allowMethods(Collection<Method> methods) {
            allowMethods = methods.stream().map(Method::toString).collect(Collectors.joining(","));
            return this;
        }

        public ResourceBuilder allowHeaders(List<String> headers) {
            allowHeaders = String.join(",", headers);
            return this;
        }

        public ResourceBuilder allowOrigin(String origin) {
            allowOrigin = origin;
            return this;
        }

        public Resource build() {
            if (enableCors) {
                methods.put(Method.OPTIONS, new CorsOptionsHandler(allowMethods,allowHeaders,allowOrigin));
            }
            return new Resource(pathRegex, methods, enableCors);
        }
    }
}
