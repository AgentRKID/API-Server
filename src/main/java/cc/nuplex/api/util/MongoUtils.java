package cc.nuplex.api.util;

import com.mongodb.client.model.ReplaceOptions;

public class MongoUtils {

    public static ReplaceOptions UPSERT_OPTIONS = new ReplaceOptions().upsert(true);

}
