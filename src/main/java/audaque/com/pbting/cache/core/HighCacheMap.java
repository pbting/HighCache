package audaque.com.pbting.cache.core;

import java.util.Set;

import audaque.com.pbting.cache.base.persistence.FilePersistListener;

public interface HighCacheMap {

	/**
	 * The default initial number of table slots for this table (32). Used when not otherwise specified in constructor.
	 **/
	public static int DEFAULT_INITIAL_CAPACITY = 32;

	/**
	 * The minimum capacity. Used if a lower value is implicitly specified by either of the constructors with arguments.
	 * MUST be a power of two.
	 */
	public static final int MINIMUM_CAPACITY = 4;

	/**
	 * The maximum capacity. Used if a higher value is implicitly specified by either of the constructors with
	 * arguments. MUST be a power of two <= 1<<30.
	 */
	public static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The default load factor for this table. Used when not otherwise specified in constructor, the default is 0.75f.
	 **/
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	// 设置支持二级缓存的文件存储策略
	public void setPersistenceListener(FilePersistListener listener);

	public FilePersistListener getPersistenceListener();

	// 设置最大的缓存实体
	public void setMaxEntries(String type, String topic, int newLimit);

	public int getMaxEntries();

	// 是指是否支持内存缓存
	public void setMemoryCaching(boolean memoryCaching);

	// 设置是否支持二级缓存
	public void setUnlimitedDiskCache(boolean unlimitedDiskCache);

	// 写操作策略的控制像
	public void setOverflowPersistence(boolean overflowPersistence);

	// 清除缓存
	public void clear();

	// 从缓存中得到一个缓存数据项
	public Object get(String type, String topic, Object key);

	// 像缓存容器中放入一个缓存数据项
	public Object put(String type, String topic, Object key, Object value);

	// 向缓存容器中移除一个缓存数据项
	public Object remove(String type, String topic, Object key);

	// 得到当前缓存容器的一个大小
	public int size();

	public void itemPut(Object key);

	/**
	 * Notify any underlying algorithm that an item has been retrieved from the cache.
	 *
	 * @param key The cache key of the item that was retrieved.
	 */
	public void itemRetrieved(Object key);

	/**
	 * Notify the underlying implementation that an item was removed from the cache.
	 *
	 * @param key The cache key of the item that was removed.
	 */
	public void itemRemoved(Object key);

	/**
	 * The cache has reached its cacpacity and an item needs to be removed. (typically according to an algorithm such as
	 * LRU or FIFO).
	 *
	 * @return The key of whichever item was removed.
	 */
	public Object removeItem();

	public Set<java.util.Map.Entry<Object, Object>> getAllEntrySet();

	public Set<Object> getAllKeySet();
}
