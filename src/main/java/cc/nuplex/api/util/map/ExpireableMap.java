package cc.nuplex.api.util.map;

import cc.nuplex.api.Application;
import cc.nuplex.api.General;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ExpireableMap<K, V> extends ConcurrentHashMap<K, V> {

    private final Map<K, Long> times = new ConcurrentHashMap<>();
    private final Long expireTime;

    public ExpireableMap(Long expireTime, TimeUnit unit) {
        this.expireTime = unit.toMillis(expireTime);
    }

    @Override
    public V put(@NonNull K key, @NonNull V value) {
        this.check();
        this.times.put(key, System.currentTimeMillis());
        return super.put(key, value);
    }

    @Override
    public V get(Object key) {
        this.check();
        return super.get(key);
    }

    private void check() {
        for (Map.Entry<K, V> entry : this.entrySet()) {
            // Check cache time
            Long time = this.times.get(entry.getKey());

            // If cache time is null or overdue remove
            if (time == null || System.currentTimeMillis() > time + this.expireTime) {
                this.remove(entry.getKey());
                this.times.remove(entry.getKey());

                if (General.isDebug()) {
                    Application.LOGGER.info("[Map] Removed entry [" + entry.getKey().toString() + ", " + entry.getValue().toString() + "]");
                }
            }
        }
    }

}
