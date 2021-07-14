package cc.nuplex.api.profile;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Profile {
    @Getter private String uuid;
    @Getter @Setter private String username;

    @Getter @Setter private Set<String> ignored = new HashSet<>();

    @ConstructorProperties({ "uniqueId ", "username" })
    public Profile(String uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public Profile() {}

    public void update(Profile other) {
        this.ignored = other.ignored;
    }

}
