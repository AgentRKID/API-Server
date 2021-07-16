package cc.nuplex.api.profile.grant;

import cc.nuplex.api.util.interfaces.Used;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Grant {

    private UUID granter;
    private String grantReason;

    private UUID rank;

    @Used
    public Grant() {}

    @Override
    public String toString() {
        return "Grant{" +
                "granter=" + granter +
                ", grantReason='" + grantReason + '\'' +
                ", rank=" + rank +
                '}';
    }
}
