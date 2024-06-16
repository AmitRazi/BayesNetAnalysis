package utils;

/**
 * This class represents a generic pair of key and value.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public class Pair<K, V> {
    private K key; // The key of the pair
    private V value; // The value of the pair

    /**
     * Constructor for the utils.Pair class.
     *
     * @param key the key of the pair
     * @param value the value of the pair
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key of the pair.
     *
     * @return the key of the pair
     */
    public K getKey() {
        return this.key;
    }

    /**
     * Sets the key of the pair.
     *
     * @param key the new key of the pair
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Gets the value of the pair.
     *
     * @return the value of the pair
     */
    public V getValue() {
        return this.value;
    }

    /**
     * Sets the value of the pair.
     *
     * @param value the new value of the pair
     */
    public void setValue(V value) {
        this.value = value;
    }
}
