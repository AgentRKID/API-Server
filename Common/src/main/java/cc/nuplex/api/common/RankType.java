package cc.nuplex.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RankType {
    OWNER("&7[&cOwner&7] &c"),
    DEV("&7[&bDev&7] &b"),
    DEFAULT("&7");

    private final String prefix;
}
