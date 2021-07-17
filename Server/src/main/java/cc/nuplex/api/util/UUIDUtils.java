package cc.nuplex.api.util;

import java.util.UUID;

public class UUIDUtils {

    public static final UUID NULL_UUID = UUID.randomUUID();

    public static UUID fromString(String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    public static UUID fromStringOrNull(String input) {
        try {
            return fromString(input);
        } catch (Exception ex) {
            return NULL_UUID;
        }
    }

}
