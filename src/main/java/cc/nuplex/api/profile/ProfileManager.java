package cc.nuplex.api.profile;

import cc.nuplex.api.Application;
import cc.nuplex.api.util.MongoUtils;
import cc.nuplex.api.util.UUIDUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileManager {

    // TODO: Remove After 5 Minutes if the profile hasn't been touched.
    private final Map<UUID, Profile> uuidToProfile = new ConcurrentHashMap<>();
    private final Map<String, Profile> usernameToProfile = new ConcurrentHashMap<>();

    private final MongoCollection<Document> collection = Application.getInstance().getDatabase().getCollection("profiles");

    public ProfileManager() {}

    public Profile getProfile(UUID uuid) {
        Profile profile = this.uuidToProfile.get(uuid);

        if (profile != null) {
            this.uuidToProfile.put(uuid, profile);
            this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);

            return profile;
        }
        return null;
    }

    public Profile getProfile(String username) {
        Profile profile = this.usernameToProfile.get(username.toLowerCase());

        if (profile != null) {
            this.uuidToProfile.put(profile.getUniqueId(), profile);
            this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);

            return profile;
        }
        return null;
    }

    public Profile getProfile(UUID uuid, String username) {
        Profile profile = this.uuidToProfile.get(uuid);

        if (profile != null) {
            this.uuidToProfile.put(profile.getUniqueId(), profile);
            this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);

            return profile;
        }

        profile = this.usernameToProfile.get(username.toLowerCase());

        if (profile == null) {
            Document document = this.find(uuid);

            if (document != null && document.containsKey("profile")) {
                if (document.containsKey("uuid") && document.containsKey("username")) {
                    profile = new Profile(UUIDUtils.fromString(document.getString("uuid")), document.getString("username"));
                    profile.update(Application.GSON.fromJson(document.getString("profile"), Profile.class));
                }
            } else {
                profile = new Profile(uuid, username);
                this.save(profile);
            }
        }

        if (profile != null) {
            this.uuidToProfile.put(profile.getUniqueId(), profile);
            this.usernameToProfile.put(profile.getUsername().toLowerCase(), profile);
        }
        return profile;
    }

    public Document find(UUID uuid) {
        return collection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    public void save(Profile profile) {
        Application.EXECUTOR.execute(() -> {
            long start = System.currentTimeMillis();

            Document document = new Document();

            document.put("uuid", profile.getUniqueId().toString());
            document.put("username", profile.getUsername());
            document.put("profile", Application.GSON.toJson(profile));

            collection.replaceOne(Filters.eq("uuid", profile.getUniqueId().toString()), document, MongoUtils.UPSERT_OPTIONS);

            Application.LOGGER.info("Saved " + profile.getUsername() + " in " + (System.currentTimeMillis() - start) + "ms!");
        });
    }

    public void removeProfile(UUID uuid) {
        Profile profile = this.uuidToProfile.remove(uuid);

        if (profile != null) {
            this.usernameToProfile.remove(profile.getUsername());
        }
    }

}
