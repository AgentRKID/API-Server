package cc.nuplex.api.storage;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import lombok.Getter;

public class Mongo {

    @Getter private MongoClient client;

    public void connect(String host, int port) {
        this.client = new MongoClient(new ServerAddress(host, port));
    }

    public void close() {
        if (this.client != null) {
            this.client.close();
            this.client = null;
        }
    }

}
