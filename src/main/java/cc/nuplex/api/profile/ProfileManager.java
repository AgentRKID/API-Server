package cc.nuplex.api.profile;

import cc.nuplex.api.Application;
import cc.nuplex.api.util.MongoUtils;
import cc.nuplex.api.util.map.ExpireableMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProfileManager {

    private final Map<String, Profile> uuidToProfile = new ExpireableMap<>(5L, TimeUnit.SECONDS);
    private final Map<String, Profile> usernameToProfile = new ExpireableMap<>(5L, TimeUnit.SECONDS);

    private final MongoCollection<Document> collection = Application.getInstance().getDatabase().getCollection("profiles");

    public void stop() {
        for (Profile profile : this.uuidToProfile.values()) {
            this.save(profile, false);
        }
        this.uuidToProfile.clear();
        this.usernameToProfile.clear();
    }

    public Profile getProfile(UUID uuid) {
        Profile profile = this.uuidToProfile.get(uuid.toString());

        // Found profile, recache, and return.
        if (profile != null) {
            this.uuidToProfile.put(uuid.toString(), profile);
            this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);

            return profile;
        }

        Document document = this.find(uuid);

        if (document != null && document.containsKey("uuid") && document.containsKey("username")) {
            profile = Application.GSON.fromJson(document.toJson(), Profile.class);

            this.uuidToProfile.put(uuid.toString(), profile);
            this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);
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

    public void removeProfile(UUID uuid) {
        Profile profile = this.uuidToProfile.remove(uuid.toString());

        if (profile != null) {
            this.usernameToProfile.remove(profile.getUsername());
        }
    }

}
