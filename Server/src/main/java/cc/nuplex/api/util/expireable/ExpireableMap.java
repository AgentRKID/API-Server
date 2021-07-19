package cc.nuplex.api.util.expireable;

import cc.nuplex.api.Application;
import cc.nuplex.api.General;
import cc.nuplex.api.util.interfaces.ExpireListener;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ExpireableMap<K, V> extends ConcurrentHashMap<K, V> {

    private final Map<K, Long> times = new ConcurrentHashMap<>();
    private final Long expireTime;

    private ExpireListener<K, V> listener;

    public ExpireableMap(Long expireTime, TimeUnit unit) {
        this.expireTime = unit.toMillis(expireTime);
    }

    public ExpireableMap(Long expireTime, TimeUnit unit, ExpireListener<K, V> listener) {
        this.expireTime = unit.toMillis(expireTime);
        this.listener = listener;
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

                if (this.listener != null) {
                    this.listener.expire(entry.getKey(), entry.getValue());
                }

                if (General.isDebug()) {
                    Application.LOGGER.info("[Map] Removed entry [" + entry.getKey().toString() + ", " + entry.getValue().toString() + "]");
                }
            }
        }
    }

}
