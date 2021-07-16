package cc.nuplex.api;

import cc.nuplex.api.config.Configuration;

public class General {

    public static boolean isDebug() {
        return Application.getInstance().getConfiguration().isDebug();
    }

    public static Configuration getConfiguration() {
        return Application.getInstance().getConfiguration();
    }

}
