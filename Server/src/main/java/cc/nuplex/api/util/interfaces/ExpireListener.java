package cc.nuplex.api.util.interfaces;

@FunctionalInterface
public interface ExpireListener<K, V> {

    /**
     * @param k the key value
     * @param v the value
     */
    void expire(K k, V v);

}
