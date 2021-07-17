package cc.nuplex.api.endpoint.route;

import spark.Request;
import spark.Response;

@FunctionalInterface
public interface RouteFunction {

    Object handle(Request request, Response response);

}
