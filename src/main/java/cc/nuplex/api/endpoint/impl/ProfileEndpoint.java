package cc.nuplex.api.endpoint.impl;

import cc.nuplex.api.Application;
import cc.nuplex.api.endpoint.Endpoint;
import cc.nuplex.api.endpoint.EndpointErrors;
import cc.nuplex.api.util.UUIDUtils;
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

            if (request.queryMap().hasKey("username")) {
                return Application.getInstance().getProfileManager().getProfile(uuid, request.queryParams("username"));
            }
            return Application.getInstance().getProfileManager().getProfile(uuid);
        });
    }

}
