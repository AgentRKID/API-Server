package cc.nuplex.api.storage;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import lombok.Getter;

public class Mongo {

    @Getter private MongoClient client;

    public void connect(String uri) {
        client = new MongoClient(new MongoClientURI(uri));
    }

    public boolean close() {
        if (client != null) {
            client.close();
            client = null;
            return true;
        }
        return false;
    }

}
