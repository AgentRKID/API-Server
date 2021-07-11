package cc.nuplex.api.endpoint.route.transformer;

import cc.nuplex.api.Application;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    @Override
    public String render(Object object) throws Exception {
        return Application.GSON.toJson(object);
    }

}
