package cc.nuplex.api.rank;

import cc.nuplex.api.Application;
import cc.nuplex.api.util.MongoUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RankManager {

    private final Map<UUID, Rank> uuidToRank = new ConcurrentHashMap<>();
    private final Map<String, Rank> nameToRank = new ConcurrentHashMap<>();

    private final MongoCollection<Document> collection = Application.getInstance().getDatabase().getCollection("ranks");

    @Getter private final Set<Rank> ranks = new HashSet<>();

    @Getter private Rank defaultRank;

    public RankManager() {
        for (Document document : collection.find()) {
            Rank rank = this.load(document);

            if (rank != null) {
                if (rank.getName().contains("Default")) {
                    this.defaultRank = rank;
                }

                this.uuidToRank.put(rank.getRankId(), rank);
                this.nameToRank.put(rank.getName().toLowerCase(), rank);
                this.ranks.add(rank);
            }
        }

        if (ranks.isEmpty()) {
            this.defaultRank = new Rank(UUID.randomUUID(), "Default");

            uuidToRank.put(this.defaultRank.getRankId(), this.defaultRank);
            nameToRank.put(this.defaultRank.getName().toLowerCase(), this.defaultRank);
            this.ranks.add(this.defaultRank);

            this.save(this.defaultRank, true);
        }
        Application.LOGGER.info("Loaded " + this.ranks.size() + " Ranks!");
    }

    public void stop() {
        for (Rank rank : this.ranks) {
            this.save(rank, false);
        }
        this.ranks.clear();
        this.uuidToRank.clear();
        this.nameToRank.clear();
    }

    public Rank load(Document document) {
        try {
            return Application.GSON.fromJson(document.toJson(), Rank.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    public Document find(String string) {
        Document document = collection.find(Filters.eq("uuid", string)).first();

        if (document != null) {
            return document;
        }
        return collection.find(Filters.eq("name", string)).first();
    }

    public Rank getRank(UUID uuid) {
        Rank rank = uuidToRank.get(uuid);

        if (rank != null) {
            return rank;
        }

        Document document = this.find(uuid.toString());

        if (document != null) {
            return this.load(document);
        }
        return null;
    }

    public Rank getRank(String name) {
        Rank rank = nameToRank.get(name.toLowerCase());

        if (rank != null) {
            return rank;
        }

        Document document = this.find(name);

        if (document != null) {
            return this.load(document);
        }
        return null;
    }

    public void save(Rank rank, boolean async) {
        Runnable runnable = () -> this.collection.replaceOne(Filters.eq("uuid", rank.getRankId().toString()), rank.toDocument(), MongoUtils.UPSERT_OPTIONS);

        if (async) {
            Application.EXECUTOR.execute(runnable);
        } else {
            runnable.run();
        }
    }

}
