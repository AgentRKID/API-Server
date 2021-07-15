package cc.nuplex.api.rank;

import cc.nuplex.api.util.interfaces.Documented;
import cc.nuplex.api.util.interfaces.Used;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Rank implements Documented {

    @Getter private UUID rankId;
    @Getter private String name;

    @Getter @Setter private int weight = 1;
    @Getter @Setter private String prefix = "";

    @Getter @Setter private String displayColor = "§7";

    @Getter private Set<String> permissions = new HashSet<>();

    @Getter @Setter private boolean hidden = false;

    @ConstructorProperties({ "rankId", "name" })
    public Rank(UUID rankId, String name) {
        this.rankId = rankId;
        this.name = name;
    }

    @Used
    public Rank() {}

    public void update(Rank other) {
        this.weight = other.weight;
        this.prefix = other.prefix;
        this.displayColor = other.displayColor;
        this.permissions = other.permissions;
        this.hidden = other.hidden;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "rankId=" + rankId +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", prefix='" + prefix + '\'' +
                ", displayColor='" + displayColor + '\'' +
                ", permissions=" + permissions +
                ", hidden=" + hidden +
                '}';
    }


    @Override
    public int hashCode() {
        return Objects.hash(rankId, name, weight, prefix, displayColor, permissions, hidden);
    }

}
