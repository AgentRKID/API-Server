package cc.nuplex.api.profile;

import cc.nuplex.api.util.interfaces.Documented;
import cc.nuplex.api.util.interfaces.Used;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Profile implements Documented {
    @Getter private String uuid;
    @Getter @Setter private String username;

    @Getter @Setter private Set<String> ignored = new HashSet<>();

    @ConstructorProperties({ "uniqueId ", "username" })
    public Profile(String uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Used
    public Profile() {}

    public void update(Profile other) {
        this.ignored = other.ignored;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", ignored=" + ignored +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, username, ignored);
    }

}
