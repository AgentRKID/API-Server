package cc.nuplex.api.util.expireable;

import cc.nuplex.api.Application;
import cc.nuplex.api.General;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class ExpireableList<V> extends CopyOnWriteArrayList<V> {

    private final Map<Integer, Long> times = new ConcurrentHashMap<>();
    private final Long expireTime;

    public ExpireableList(Long expireTime, TimeUnit unit) {
        this.expireTime = unit.toMillis(expireTime);
    }

    @Override
    public boolean add(V v) {
        this.check();
        this.times.put(v.hashCode(), System.currentTimeMillis());
        return super.add(v);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        this.check();
        for (V v : c) {
            this.times.put(v.hashCode(), System.currentTimeMillis());
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends V> c) {
        this.check();
        for (V v : c) {
            this.times.put(v.hashCode(), System.currentTimeMillis());
        }
        return super.addAll(index, c);
    }

    @Override
    public boolean contains(Object o) {
        this.check();
        return super.contains(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        this.check();
        return super.containsAll(c);
    }

    private void check() {
        for (V v : this) {
            // Get cache time by hash code
            Long time = this.times.get(v.hashCode());

            // If cache time is null or overdue remove
            if (time == null || System.currentTimeMillis() > time + this.expireTime) {
                this.remove(v);
                this.times.remove(v.hashCode());

                if (General.isDebug()) {
                    Application.LOGGER.info("[List] Removed entry [" + v + "]");
                }
            }
        }
    }

}
