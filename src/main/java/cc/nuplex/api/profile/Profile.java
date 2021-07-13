package cc.nuplex.api.profile;

import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.UUID;

public class Profile {

    @Getter private final transient String username;
    @Getter private final transient UUID uniqueId;

    @ConstructorProperties({ "uniqueId ", "username" })
    public Profile(UUID uniqueId, String username) {
        this.uniqueId = uniqueId;
        this.username = username;
    }

    public void update(Profile other) { }

}
