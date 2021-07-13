package cc.nuplex.api.profile;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Profile {

    @Getter private transient final UUID uniqueId;
    @Getter @Setter private transient String username;

    @Getter @Setter private Set<String> ignored = new HashSet<>();

    @ConstructorProperties({ "uniqueId ", "username" })
    public Profile(UUID uniqueId, String username) {
        this.uniqueId = uniqueId;
        this.username = username;
    }

    public void update(Profile other) {
        this.ignored = other.ignored;
    }

}
