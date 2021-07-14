package cc.nuplex.api.endpoint.impl;

import cc.nuplex.api.Application;
import cc.nuplex.api.endpoint.Endpoint;
import cc.nuplex.api.endpoint.EndpointErrors;
import cc.nuplex.api.profile.Profile;
import cc.nuplex.api.util.UUIDUtils;
import org.bson.Document;
import spark.Spark;

import java.util.UUID;

public class ProfileEndpoint extends Endpoint {

    public ProfileEndpoint() {
        super("profile");

        addRoute(":uuid", Type.GET, (request, response) -> {
            String uuidString = request.params("uuid");
            UUID uuid;

            try {
                uuid = UUID.fromString(uuidString);
            } catch (Exception ex) {
                try {
                    uuid = UUIDUtils.fromString(uuidString);
                } catch (Exception ignored) {
                    return EndpointErrors.INVALID_UUID;
                }
            }

            Profile profile = null;

            if (request.queryMap().hasKey("username")) {
                String username = request.queryParams("username");

                profile = Application.getInstance().getProfileManager().getProfile(uuid, username);

                if (!profile.getUsername().equals(username)) {
                    profile.setUsername(username);
                }
            }

            if (profile != null) {
                profile = Application.getInstance().getProfileManager().getProfile(uuid);
            }

            return profile;
        });
    }

}
