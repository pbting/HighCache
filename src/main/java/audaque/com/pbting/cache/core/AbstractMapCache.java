/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package audaque.com.pbting.cache.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import audaque.com.pbting.cache.base.persistence.FilePersistListener;
import audaque.com.pbting.cache.exception.FilePersistException;

public abstract class AbstractMapCache implements HighCacheMap{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// OpenSymphony BEGIN (pretty long!)
	protected static Log log = 
				LogFactory.getLog(AbstractMapCache.class);

	/**
	 * Persistence listener.
	 */
	protected FilePersistListener persistenceListener = null;

	/**
	 * Use memory cache or not.
	 */
	protected boolean memoryCaching = true;

	/**
	 * Use unlimited disk caching.
	 */
	protected boolean unlimitedDiskCache = false;

	/**
	 * The load factor for the hash table.
	 *
	 * @serial
	 */
	protected float loadFactor;

	/**
	 * Default cache capacity (number of entries).
	 */
	protected final int DEFAULT_MAX_ENTRIES = 100;

	/**
	 * Max number of element in cache when considered unlimited.
	 */
	protected final int UNLIMITED = 2147483646;

	/**
	 * Cache capacity (number of entries).
	 */
	protected int maxEntries = DEFAULT_MAX_ENTRIES;

	/**
	 * The table is rehashed when its size exceeds this threshold. (The value of
	 * this field is always (int)(capacity * loadFactor).)
	 *
	 * @serial
	 */
	protected int threshold;

	/**
	 * Use overflow persistence caching.
	 */
	private boolean overflowPersistence = false;

	private Map cacheMap = null;
	
	private int capacity = DEFAULT_INITIAL_CAPACITY;
	
	public AbstractMapCache(int initialCapacity, float loadFactor) {
		if (loadFactor <= 0) {
			throw new IllegalArgumentException("Illegal Load factor: "
					+ loadFactor);
		}

		this.loadFactor = loadFactor;

		maxEntries = initialCapacity;
		
		capacity = p2capacity(initialCapacity);
		
		this.cacheMap = Collections.synchronizedMap(new HashMap(initialCapacity, loadFactor));
	}

	public AbstractMapCache(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs a new, empty map with a default initial capacity and load
	 * factor.
	 */
	public AbstractMapCache() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 *
	 * @return <tt>true</tt> if this map contains no key-value mappings.
	 */
	public synchronized boolean isEmpty() {
		return cacheMap.size() == 0;
	}

	public synchronized int size(){
		
		return this.cacheMap.size();
	}
	/**
	 * Set the cache capacity
	 */
	public void setMaxEntries(String type,String topic,int newLimit) {
		if (newLimit > 0) {
			maxEntries = newLimit;

			synchronized (this) { // because remove() isn't synchronized

				while (cacheMap.size() > maxEntries) {
					remove(type,topic,removeItem(), false);
				}
			}
		} else {
			// Capacity must be at least 1
			throw new IllegalArgumentException(
					"Cache maximum number of entries must be at least 1");
		}
	}

	/**
	 * Retrieve the cache capacity (number of entries).
	 */
	public int getMaxEntries() {
		return maxEntries;
	}

	/**
	 * Sets the memory caching flag.
	 */
	public void setMemoryCaching(boolean memoryCaching) {
		this.memoryCaching = memoryCaching;
	}

	/**
	 * Check if memory caching is used.
	 */
	public boolean isMemoryCaching() {
		return memoryCaching;
	}

	/**
	 * Set the persistence listener to use.
	 */
	public void setPersistenceListener(FilePersistListener listener) {
		this.persistenceListener = listener;
	}

	/**
	 * Get the persistence listener.
	 */
	public FilePersistListener getPersistenceListener() {
		return persistenceListener;
	}

	/**
	 * Sets the unlimited disk caching flag.
	 */
	public void setUnlimitedDiskCache(boolean unlimitedDiskCache) {
		this.unlimitedDiskCache = unlimitedDiskCache;
	}

	/**
	 * Check if we use unlimited disk cache.
	 */
	public boolean isUnlimitedDiskCache() {
		return unlimitedDiskCache;
	}

	/**
	 * Check if we use overflowPersistence
	 *
	 * @return Returns the overflowPersistence.
	 */
	public boolean isOverflowPersistence() {
		return this.overflowPersistence;
	}

	/**
	 * Sets the overflowPersistence flag
	 *
	 * @param overflowPersistence
	 *            The overflowPersistence to set.
	 */
	public void setOverflowPersistence(boolean overflowPersistence) {
		this.overflowPersistence = overflowPersistence;
	}

	/**
	 * Removes all mappings from this map.
	 */
	public synchronized void clear() {

		for (Iterator iter = cacheMap.keySet().iterator(); iter.hasNext();) {
			itemRemoved(iter.next());
			iter.remove();// 清除一级缓存
		}
		// Clean out the entire disk cache
		persistClear();// 将二级缓存清空
	}

	public boolean contains(Object value) {
		return containsValue(value);
	}

	/**
	 * Tests if the specified object is a key in this table.
	 *
	 * @param key
	 *            possible key.
	 * @return <code>true</code> if and only if the specified object is a key in
	 *         this table, as determined by the <tt>equals</tt> method;
	 *         <code>false</code> otherwise.
	 * @exception NullPointerException
	 *                if the key is <code>null</code>.
	 * @see #contains(Object)
	 */
	public boolean containsKey(String type,String topic,Object key) {
		return get(type,topic,key) != null;
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the specified
	 * value. Note: This method requires a full internal traversal of the hash
	 * table, and so is much slower than method <tt>containsKey</tt>.
	 *
	 * @param value
	 *            value whose presence in this map is to be tested.
	 * @return <tt>true</tt> if this map maps one or more keys to the specified
	 *         value.
	 * @exception NullPointerException
	 *                if the value is <code>null</code>.
	 */
	public boolean containsValue(Object value) {
		return cacheMap.containsValue(value);
	}

	public Object get(String type,String topic,Object key) {
		if (log.isDebugEnabled()) {
			log.debug("get called (key=" + key + ")");
		}
		// 一级缓存取
		Object e = cacheMap.get(key);

		if (e == null) {// 一级缓存取，如果一缓存没有，则到二级缓存取

			Object value = persistRetrieve(type,topic,key);

			if (value != null) {
				// Update the map, but don't persist the data
				cacheMap.put(key, value);
			}

			return value;
		}
		// checking for pointer equality first wins in most applications
		else {
			itemRetrieved(key);// 使用缓存的缓存替换策略，改变其顺序

			return e; //
		}

	}

	public float loadFactor() {
		return loadFactor;
	}

	public Object put(String type,String topic,Object key, Object value) {
		// Call the internal put using persistance
		return put(type,topic,key, value, true);
	}

	public Object remove(String type,String topic,Object key) {
		return remove(type,topic,key, true);
	}

	/**
	 * Remove an object from the persistence.
	 * 
	 * @param key
	 *            The key of the object to remove
	 */
	protected void persistRemove(Object key) {
		if (log.isDebugEnabled()) {
			log.debug("PersistRemove called (key=" + key + ")");
		}

		if (persistenceListener != null) {
			try {
				persistenceListener.remove((String) key);
			} catch (FilePersistException e) {
				log.error("[oscache] Exception removing cache entry with key '"
						+ key + "' from persistence", e);
			}
		}
	}

	/**
	 * Removes a cache group using the persistence listener.
	 * 
	 * @param groupName
	 *            The name of the group to remove
	 */
	protected void persistRemoveGroup(String groupName) {
		if (log.isDebugEnabled()) {
			log.debug("persistRemoveGroup called (groupName=" + groupName + ")");
		}

		if (persistenceListener != null) {
			try {
				persistenceListener.removeGroup(groupName);
			} catch (FilePersistException e) {
				log.error("[oscache] Exception removing group " + groupName, e);
			}
		}
	}

	/**
	 * Retrieve an object from the persistence listener.
	 * 
	 * @param key
	 *            The key of the object to retrieve
	 */
	protected Object persistRetrieve(String type,String topic,Object key) {
		if (log.isDebugEnabled()) {
			log.debug("persistRetrieve called (key=" + key + ")");
		}

		Object entry = null;

		if (persistenceListener != null) {
			try {
				entry = persistenceListener.retrieveCache(type,topic,(String) key);
			} catch (FilePersistException e) {
			}
		}

		return entry;
	}

	/**
	 * Retrieves a cache group using the persistence listener.
	 * 
	 * @param groupName
	 *            The name of the group to retrieve
	 */
	protected Set persistRetrieveGroup(String groupName) {
		if (log.isDebugEnabled()) {
			log.debug("persistRetrieveGroup called (groupName=" + groupName
					+ ")");
		}

		if (persistenceListener != null) {
			try {
				return persistenceListener.retrieveGroupCache(groupName);
			} catch (FilePersistException e) {
				log.error("[oscache] Exception retrieving group " + groupName,
						e);
			}
		}

		return null;
	}

	/**
	 * Store an object in the cache using the persistence listener.
	 * 
	 * @param key
	 *            The object key
	 * @param obj
	 *            The object to store
	 */
	protected void persistStore(String type,String topic,Object key, Object obj) {
		if (log.isDebugEnabled()) {
			log.debug("persistStore called (key=" + key + ")");
		}

		if (persistenceListener != null) {
			try {
				persistenceListener.store(type,topic,(String) key, obj);
			} catch (FilePersistException e) {
				log.error("[oscache] Exception persisting " + key, e);
			}
		}
	}

	/**
	 * Creates or Updates a cache group using the persistence listener.
	 * 
	 * @param groupName
	 *            The name of the group to update
	 * @param group
	 *            The entries for the group
	 */
	protected void persistStoreGroup(String groupName, Set group) {
		if (log.isDebugEnabled()) {
			log.debug("persistStoreGroup called (groupName=" + groupName + ")");
		}

		if (persistenceListener != null) {
			try {
				if ((group == null) || group.isEmpty()) {
					persistenceListener.removeGroup(groupName);
				} else {
					persistenceListener.storeGroup(groupName, group);
				}
			} catch (FilePersistException e) {
				log.error("[oscache] Exception persisting group " + groupName,
						e);
			}
		}
	}

	/**
	 * Removes the entire cache from persistent storage.
	 */
	protected void persistClear() {
		if (log.isDebugEnabled()) {
			log.debug("persistClear called");
			;
		}

		if (persistenceListener != null) {
			try {
				persistenceListener.clear();
			} catch (FilePersistException e) {
				log.error("[oscache] Exception clearing persistent cache", e);
			}
		}
	}

	/**
	 * Notify the underlying implementation that an item was put in the cache.
	 *
	 * @param key
	 *            The cache key of the item that was put.
	 */
	public abstract void itemPut(Object key);

	/**
	 * Notify any underlying algorithm that an item has been retrieved from the
	 * cache.
	 *
	 * @param key
	 *            The cache key of the item that was retrieved.
	 */
	public abstract void itemRetrieved(Object key);

	/**
	 * Notify the underlying implementation that an item was removed from the
	 * cache.
	 *
	 * @param key
	 *            The cache key of the item that was removed.
	 */
	public abstract void itemRemoved(Object key);

	/**
	 * The cache has reached its cacpacity and an item needs to be removed.
	 * (typically according to an algorithm such as LRU or FIFO).
	 *
	 * @return The key of whichever item was removed.
	 */
	public abstract Object removeItem();

	private static int hash(Object x) {
		int h = x.hashCode();

		// Multiply by 127 (quickly, via shifts), and mix in some high
		// bits to help guard against bunching of codes that are
		// consecutive or equally spaced.
		return ((h << 7) - h + (h >>> 9) + (h >>> 17));
	}

	private int p2capacity(int initialCapacity) {
		int cap = initialCapacity;

		// Compute the appropriate capacity
		int result;

		if ((cap > MAXIMUM_CAPACITY) || (cap < 0)) {
			result = MAXIMUM_CAPACITY;
		} else {
			result = MINIMUM_CAPACITY;

			while (result < cap) {
				result <<= 1;
			}
		}

		return result;
	}

	/*
	 * Previous code public Object put(Object key, Object value)
	 */
	private Object put(String type,String topic,Object key, Object value, boolean persist) {
		/** OpenSymphony END */
		if (value == null) {
			throw new NullPointerException();
		}

		Object oldValue = null;
		if (cacheMap.size() >= maxEntries) {
			oldValue = remove(type,topic,removeItem(), false);
		}

		if (memoryCaching) {
			cacheMap.put(key, value);
		}

		itemPut(key);

		// Persist if required and update the secondary cache directly
		if (persist && !overflowPersistence) {
			persistStore(type,topic,key, value);
		}

		return oldValue;
	}

	private synchronized Object remove(String type,String topic,Object key, boolean invokeAlgorithm) {
		if (key == null) {
			return null;
		}

		Object e = cacheMap.remove(key);
		
		/**
		 * 如果不支持磁盘无限大持久化缓存 并且 不需要 等到缓存容器就需要持久化的情况下.那每次缓存 容量满的时候,都需要.
		 * 这个时候:如果从内存中移除一个过期的缓存实体,与此同时,也会从磁盘给移除.
		 */
		if (!unlimitedDiskCache && !overflowPersistence) {
			persistRemove(key);
		}
		
		//z这里因为上面已经 remove 一个了，所以要加载才知道 这个key 是否需要 持久化
		if (overflowPersistence && ((cacheMap.size() + 1) >= maxEntries)) {
			persistStore(type,topic,key, e);
		}

		itemRemoved(key);
		return e;
	}
	
	public Set<java.util.Map.Entry<Object, Object>> getAllEntrySet() {
		return this.cacheMap.entrySet();
	};
	
	public Set<Object> getAllKeySet() {
		return this.cacheMap.keySet();
	}
}
