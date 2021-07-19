package cc.nuplex.api.profile;

import cc.nuplex.api.Application;
import cc.nuplex.api.General;
import cc.nuplex.api.util.MongoUtils;
import cc.nuplex.api.util.expireable.ExpireableMap;
import com.google.gson.JsonElement;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProfileManager {

    private final Map<String, Profile> uuidToProfile = new ExpireableMap<>(5L, TimeUnit.MINUTES,
            (uuid, profile) -> this.removeProfile(profile, true, false));
    private final Map<String, Profile> usernameToProfile = new ExpireableMap<>(5L, TimeUnit.MINUTES);

    private final MongoCollection<Document> collection = Application.getInstance().getDatabase().getCollection("profiles");

    public ProfileManager() {
        if (General.getConfiguration().isRunRemoveSettings()) {
            long start = System.currentTimeMillis();

            Set<String> toRemove = new HashSet<>();

            for (JsonElement element : General.getConfiguration().getSettingsToRemove()) {
                toRemove.add(element.getAsString());
            }

            for (Document document : collection.find()) {
                Profile profile = Application.GSON.fromJson(document.toJson(), Profile.class);

                if (profile != null) {
                    for (String setting : toRemove) {
                        profile.getSettings().remove(setting);
                    }
                }
            }
            Application.LOGGER.info("Removed all settings that needed to be removed from all profiles in " + (System.currentTimeMillis() - start) + "ms!");
            Application.getInstance().stop();
        }
    }

    public void stop() {
        for (Profile profile : this.uuidToProfile.values()) {
            this.save(profile, false);
        }
        this.uuidToProfile.clear();
        this.usernameToProfile.clear();
    }

    public Profile getProfile(UUID uuid, boolean load) {
        Profile profile = this.uuidToProfile.get(uuid.toString());

        // Found profile, recache, and return.
        if (profile != null) {
            this.uuidToProfile.put(uuid.toString(), profile);
            this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);

            return profile;
        }

        if (load) {
            Document document = this.find(uuid);

            if (document != null && document.containsKey("uuid") && document.containsKey("username")) {
                profile = Application.GSON.fromJson(document.toJson(), Profile.class);

                this.uuidToProfile.put(uuid.toString(), profile);
                this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);
            }
        }
        return profile;
    }

    public Profile getProfile(String username) {
        Profile profile = this.usernameToProfile.get(username.toLowerCase());

        // Found profile, recache, and return.
        if (profile != null) {
            this.uuidToProfile.put(profile.getUuid(), profile);
            this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);

            return profile;
        }
        return null;
    }

    public Profile getProfile(UUID uuid, String username) {
        Profile profile = this.uuidToProfile.get(uuid.toString());

        // Found profile by uuid
        if (profile != null) {
            this.uuidToProfile.put(profile.getUuid(), profile);
            this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);

            return profile;
        }

        // Couldn't find by UUID lets try username.
        profile = this.usernameToProfile.get(username.toLowerCase());

        // Couldn't find profile, lets try mongo.
        if (profile == null) {
            Document document = this.find(uuid);

            if (document != null && document.containsKey("uuid") && document.containsKey("username")) {
                profile = Application.GSON.fromJson(document.toJson(), Profile.class);
            } else { // Couldn't find document, lets just create one and save it.
                profile = new Profile(uuid.toString(), username);
                this.save(profile, true);
            }
        }

        // Profile wasn't null lets cache them.
        this.uuidToProfile.put(profile.getUuid(), profile);
        this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);

        return profile;
    }

    public Document find(UUID uuid) {
        return this.collection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    public void save(Profile profile, boolean async) {
        Runnable runnable = () -> {
            long start = System.currentTimeMillis();

            this.collection.replaceOne(Filters.eq("uuid", profile.getUuid()),
                    profile.toDocument(), MongoUtils.UPSERT_OPTIONS);

            Application.LOGGER.info("Saved " + profile.getUsername() + " in " + (System.currentTimeMillis() - start) + "ms!");
        };

        if (async) {
            Application.EXECUTOR.execute(runnable);
        } else {
            runnable.run();
        }
    }

    public void removeProfile(UUID uuid, boolean save, boolean async) {
        Profile profile = this.uuidToProfile.remove(uuid.toString());

        if (profile != null) {
            this.usernameToProfile.remove(profile.getUsername());

            if (save) {
                this.save(profile, async);
            }
        }
    }

    public void removeProfile(Profile profile, boolean save, boolean async) {
        this.uuidToProfile.remove(profile.getUuid());
        this.usernameToProfile.remove(profile.getUsername().toLowerCase());

        if (save) {
            this.save(profile, async);
        }
    }

}
