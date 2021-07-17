package cc.nuplex.api.storage;

import cc.nuplex.api.Application;
import cc.nuplex.api.General;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import lombok.Getter;

public class Mongo {

    @Getter private MongoClient client;

    public void connect(String host, int port) {
        this.client = new MongoClient(new ServerAddress(host, port));

        if (General.isDebug()) {
            Application.LOGGER.info("Connected to Mongo successfully.");
        }
    }

    public void close() {
        if (this.client != null) {
            this.client.close();
            this.client = null;

            if (General.isDebug()) {
                Application.LOGGER.info("");
            }
        }
    }

}
