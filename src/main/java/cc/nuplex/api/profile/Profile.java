package cc.nuplex.api.profile;

import cc.nuplex.api.profile.grant.Grant;
import cc.nuplex.api.util.interfaces.Documented;
import cc.nuplex.api.util.interfaces.Used;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.*;

public class Profile implements Documented {

    @Getter private String uuid;
    @Getter @Setter private String username;

    // We only have singular perm grants.
    @Getter @Setter private Grant grant;

    @Getter @Setter private Map<String, Boolean> settings = new HashMap<>();
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
        this.settings = other.settings;
    }

    public void saveSetting(Enum<?> setting, boolean value) {
        this.settings.put(setting.name(), value);
    }

    public void removeSetting(Enum<?> setting) {
        this.settings.remove(setting.name());
    }

    public boolean isSettingEnabled(Enum<?> setting) {
        return this.settings.getOrDefault(setting.name(), false);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", grant=" + grant +
                ", ignored=" + ignored +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, username, ignored);
    }

}
