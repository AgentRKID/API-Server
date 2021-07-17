package cc.nuplex.api.util.json;

import com.google.gson.JsonElement;

public interface JsonHandler<T extends JsonElement> {

    void load(T t);

    T save();

}
