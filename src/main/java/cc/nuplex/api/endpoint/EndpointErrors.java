package cc.nuplex.api.endpoint;

import cc.nuplex.api.util.json.JsonBuilder;
import com.google.gson.JsonObject;

public class EndpointErrors {

    public static final JsonObject INVALID_UUID = new JsonBuilder().add("success", false).add("error", "INVALID_UUID").build();

}
