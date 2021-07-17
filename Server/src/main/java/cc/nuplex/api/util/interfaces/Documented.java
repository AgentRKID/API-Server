package cc.nuplex.api.util.interfaces;

import cc.nuplex.api.Application;
import org.bson.Document;

public interface Documented {

    /**
     * Turns a JSON String into a Document
     */
    default Document toDocument() {
        return Document.parse(Application.GSON.toJson(this));
    }

}
