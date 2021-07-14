package cc.nuplex.api.rank;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Rank {

    @Getter private final UUID rankId;
    @Getter private final String name;

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

    public void update(Rank other) {
        this.weight = other.weight;
        this.prefix = other.prefix;
        this.displayColor = other.displayColor;
        this.permissions = other.permissions;
        this.hidden = other.hidden;
    }

}
