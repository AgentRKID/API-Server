package cc.nuplex.api.endpoint.filter;

import spark.Request;
import spark.Response;

public interface Filter {

    void handle(Request request, Response response);

    default boolean isBefore() {
        return true;
    }

}
