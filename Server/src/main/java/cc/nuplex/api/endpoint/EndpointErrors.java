package cc.nuplex.api.endpoint;

import cc.nuplex.api.util.json.JsonBuilder;
import com.google.gson.JsonObject;

public class EndpointErrors {

    public static final JsonObject INVALID_UUID = new JsonBuilder().add("success", false).add("error", "INVALID_UUID").build();
    public static final JsonObject INVALID_JSON = new JsonBuilder().add("success", false).add("error", "INVALID_JSON").build();
    public static final JsonObject INVALID_PAGE = new JsonBuilder().add("success", false).add("error", "INVALID_PAGE").build();

}
