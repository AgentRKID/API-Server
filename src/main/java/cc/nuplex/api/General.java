package cc.nuplex.api;

public class General {

    public static boolean isDebug() {
        return Application.getInstance().getConfiguration().isDebug();
    }

}
