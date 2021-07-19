package cc.nuplex.api.endpoint.impl;

import cc.nuplex.api.Application;
import cc.nuplex.api.endpoint.Endpoint;
import cc.nuplex.api.endpoint.EndpointErrors;
import cc.nuplex.api.profile.Profile;
import cc.nuplex.api.profile.ProfileManager;
import cc.nuplex.api.util.UUIDUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.UUID;

public class ProfileEndpoint extends Endpoint {

    public ProfileEndpoint() {
        super("profile");

        ProfileManager profileManager = Application.getInstance().getProfileManager();

        addRoute(":uuid", Type.GET, (request, response) -> {
            UUID uuid = UUIDUtils.fromStringOrNull(request.params("uuid"));

            if (uuid == UUIDUtils.NULL_UUID) {
                return EndpointErrors.INVALID_UUID;
            }

            Profile profile = null;

            if (request.queryMap().hasKey("username")) {
                String username = request.queryParams("username");

                profile = profileManager.getProfile(uuid, username);

                // We need to update the username
                // whenever ours doesn't match their current
                if (!profile.getUsername().equals(username)) {
                    profile.setUsername(username);
                }
            }

            // We either didn't get a request with a username or
            // somehow didn't create one/find one, so lets try to load one from UUID.
            if (profile == null) {
                profile = profileManager.getProfile(uuid, true);
            }

            return profile;
        });

        addRoute(":uuid/save", Type.POST, (request, response) -> {
            UUID uuid = UUIDUtils.fromStringOrNull(request.params("uuid"));

            if (uuid == UUIDUtils.NULL_UUID) {
                return EndpointErrors.INVALID_UUID;
            }

            String body = request.body();

            try {
                Profile otherProfile = Application.GSON.fromJson(body, Profile.class);
                Profile profile = profileManager.getProfile(uuid, false);

                // Check if we need to save the other
                // profile cause we don't have the current cached.
                if (profile == null) {
                    profileManager.save(otherProfile, true);
                    return SUCCESS;
                }

                profile.update(otherProfile);

                return SUCCESS;
            } catch (JsonParseException ex) {
                return EndpointErrors.INVALID_JSON;
            }
        });

        addRoute(":uuid/status", Type.POST, (request, response) -> {
            UUID uuid = UUIDUtils.fromStringOrNull(request.params("uuid"));

            if (uuid == UUIDUtils.NULL_UUID) {
                return EndpointErrors.INVALID_UUID;
            }

            String body = request.body();

            try {
                JsonElement element = Application.JSON_PARSER.parse(body);

                if (element.isJsonNull()) {
                    return EndpointErrors.INVALID_JSON;
                }

                JsonObject object = element.getAsJsonObject();
                Profile profile = profileManager.getProfile(uuid, true);

                if (profile != null) {
                    if (object.has("online")) profile.setOnline(object.get("online").getAsBoolean());
                    if (object.has("lastOnline")) profile.setLastOnline(object.get("lastOnline").getAsLong());
                }
            } catch (JsonParseException ex) {
                return EndpointErrors.INVALID_JSON;
            }
            return SUCCESS;
        });
    }



}
