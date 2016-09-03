package audaque.com.pbting.cache.base.info;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import audaque.com.pbting.cache.base.persistence.FilePersistListener;
import audaque.com.pbting.cache.base.refresh.policy.EntryRefreshPolicy;
import audaque.com.pbting.cache.core.HighCacheMap;
import audaque.com.pbting.cache.event.CacheAccessEvent;
import audaque.com.pbting.cache.event.CacheAccessEventListener;
import audaque.com.pbting.cache.event.CacheAccessEventType;
import audaque.com.pbting.cache.event.CacheEntryEvent;
import audaque.com.pbting.cache.event.CacheEntryEventListener;
import audaque.com.pbting.cache.event.CacheEntryEventType;
import audaque.com.pbting.cache.event.CacheEventListener;
import audaque.com.pbting.cache.event.CacheGroupEvent;
import audaque.com.pbting.cache.event.CachewideEvent;
import audaque.com.pbting.cache.event.CachewideEventType;
import audaque.com.pbting.cache.exception.NeedsRefreshException;
import audaque.com.pbting.cache.factory.AbstractCacheFactory;
import audaque.com.pbting.cache.util.FastCronParser;
import audaque.com.pbting.cache.util.TimeUtils;
import audaque.com.pbting.concurrent.cache.core.LRFUByDXConcurrentCache;
import audaque.com.pbting.concurrent.cache.core.UnlimitedConcurrentCache;

/**
 * @author pbting
 */
public class HighCache implements Serializable {

	public static final String NESTED_EVENT = "NESTED";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static transient final Log log = LogFactory.getLog(HighCache.class);

	/**
	 * A list of all registered event listeners for this cache.
	 */
	protected EventListenerList listenerList = new EventListenerList();

	protected EntryRefreshPolicy refreshPolicy = null;
	
	protected int refreshPeriod = 2 * AbstractCacheFactory.ONE_MINIUTE;//默认两分钟的刷新
	/**
	 * The actual cache map. This is where the cached objects are held.
	 */
	private HighCacheMap cacheMap = null;
	// private AbstractConcurrentReadCache cacheMap = null;

	/**
	 * Date of last complete cache flush.
	 */
	private Date flushDateTime = null;

	private Map<Object,EntryUpdateState> updateStates = new HashMap<Object,EntryUpdateState>();

	private boolean blocking = false;

	/**
	 * each high cache have a topic name
	 */
	private String topicName ;
	
	private String type = "cache" ;
	
	public HighCache(String type,String topicName,boolean useMemoryCaching, boolean unlimitedDiskCache, boolean overflowPersistence) {
		this(type,topicName,useMemoryCaching, unlimitedDiskCache, overflowPersistence,  null, HighCacheMap.DEFAULT_INITIAL_CAPACITY, 2 * AbstractCacheFactory.ONE_MINIUTE);
	}

	public HighCache(String type,String topicName,boolean useMemoryCaching, boolean unlimitedDiskCache, boolean overflowPersistence,int refreshPeriod) {
		this(type,topicName,useMemoryCaching, unlimitedDiskCache, overflowPersistence,  null, HighCacheMap.DEFAULT_INITIAL_CAPACITY, refreshPeriod);
	}
	
	public HighCache(String type,String topicName,boolean useMemoryCaching, boolean unlimitedDiskCache, boolean overflowPersistence, String algorithmClass, int capacity, int refreshPeriod) {
		if(StringUtils.isNotBlank(topicName)){
			this.topicName = topicName;
		}else{
			this.topicName = "HighCache-"+System.currentTimeMillis();
		}
		
		if(!StringUtils.isEmpty(type)){
			this.type = type;
		}
		
			
		// Instantiate the algo class if valid
		if (((algorithmClass != null) && (algorithmClass.length() > 0)) && (capacity > 0)) {
			try {
				log.debug("[HighCache]:the algorithm class is：" + algorithmClass);
				cacheMap = (HighCacheMap) Class.forName(algorithmClass).newInstance();
				cacheMap.setMaxEntries(type,topicName,capacity);
			} catch (Exception e) {
				log.error("Invalid class name for cache algorithm class. " + e.toString());
			}
		}
		
		if (cacheMap == null) {
			// If we have a capacity, use LRU cache otherwise use unlimited Cache
			if (capacity > 0) {
				cacheMap = new LRFUByDXConcurrentCache(capacity);
			} else {
				cacheMap = new UnlimitedConcurrentCache();
			}
		}

		cacheMap.setUnlimitedDiskCache(unlimitedDiskCache);
		cacheMap.setOverflowPersistence(overflowPersistence);
		cacheMap.setMemoryCaching(useMemoryCaching);

		this.refreshPeriod = refreshPeriod * AbstractCacheFactory.ONE_MINIUTE;
		
		if(this.refreshPeriod < 0)//如果是一个非负数，则默认过期时间为一天
			this.flushDateTime = TimeUtils.getNextDate();
	}

	public int getRefreshPeriod() {
		return refreshPeriod;
	}

	public void setRefreshPeriod(int refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	/**
	 * @return the maximum number of items to cache can hold.
	 */
	public int getCapacity() {
		return cacheMap.getMaxEntries();
	}

	public void setCapacity(int capacity) {
		cacheMap.setMaxEntries(type,topicName,capacity);
	}

	// to judge whether a cache entry have refreshed before
	private boolean isFlushed(CacheEntry cacheEntry) {
		if (flushDateTime != null) {
			final long lastUpdate = cacheEntry.getLastUpdate();
			final long flushTime = flushDateTime.getTime();

			// CACHE-241: check flushDateTime with current time also
			return (flushTime <= System.currentTimeMillis()) && (flushTime >= lastUpdate);
		} else {
			return false;
		}
	}

	/**
	 * @param key 在缓存容器中根据key获取一个缓存实体，默认使用的刷新策略是系统配置的refresh period 策略
	 * @return
	 * @throws NeedsRefreshException
	 */
	public Object get(Object key) throws NeedsRefreshException {
		// in here the refresh period value will geven in properties file
		return get(key, refreshPeriod, null);
	}

	public Object get(Object key, int refreshPeriod) throws NeedsRefreshException {
		return get(key, refreshPeriod, null);
	}

	/**
	 * 这里获取一个缓存实体时，需要判断该缓存实体是否已经过期,提供这个方法给程序员调用，是否显得更加的灵活
	 * 
	 * @param key
	 * @param refreshPeriod
	 * @param cronExpiry
	 * @return
	 * @throws NeedsRefreshException
	 */
	public Object get(Object key, int refreshPeriod, String cronExpiry) throws NeedsRefreshException {

		CacheEntry cacheEntry = this.getCacheEntry(key);

		// 如果该缓存实体为null,就说名这个缓存实体收到了意外，就是在缓存容器中过期或者丢失，这将导致外部需要异常处理
		if (cacheEntry == null)
			throw new NeedsRefreshException("the cache entry has expire,please refresh it.");
		
		if(cacheEntry.getPolicy()==null){
			cacheEntry.setPolicy(this.refreshPolicy);
		}
		
		Object content = cacheEntry.getContent();

		// 从缓存中获取一个缓存实体，表明缓存访问事件触发，
		CacheAccessEventType accessEventType = CacheAccessEventType.HIT;

		boolean reload = false;

		// 这里判断是否已经过期,这个时候应该还未过期 false
		if (this.isStale(cacheEntry, refreshPeriod, cronExpiry)) {// 需要判断这个缓存实体是否已经过期

			// Get access to the EntryUpdateState instance and increment the usage count during the potential sleep
			EntryUpdateState updateState = getUpdateState(key);
			try {
				synchronized (updateState) {// 同步每一个缓存实体的更新状态

					// 如果当前缓存实体的状态是等待去更新或者已经取消了更新，则开=开始更新
					if (updateState.isAwaitingUpdate() || updateState.isCancelled()) {
						// No one else is currently updating this entry - grab ownership
						updateState.startUpdate();

						// 判断该缓存实体是命中的状态还是过期命中状态
						if (cacheEntry.isNew()) {
							accessEventType = CacheAccessEventType.STALE_HIT;
						} else {
							accessEventType = CacheAccessEventType.MISS;
						}
					} else if (updateState.isUpdating()) {
						// Another thread is already updating the cache. We block if this
						// is a new entry, or blocking mode is enabled. Either putInCache()
						// or cancelUpdate() can cause this thread to resume.
						if (cacheEntry.isNew() || blocking) {
							do {
								try {
									updateState.wait();
								} catch (InterruptedException e) {
								}
							} while (updateState.isUpdating());

							if (updateState.isCancelled()) {
								// The updating thread cancelled the update, let this one have a go.
								// This increments the usage count for this EntryUpdateState instance
								updateState.startUpdate();

								if (cacheEntry.isNew()) {
									accessEventType = CacheAccessEventType.STALE_HIT;
								} else {
									accessEventType = CacheAccessEventType.MISS;
								}
							} else if (updateState.isComplete()) {
								reload = true;
							} else {
								log.error("Invalid update state for cache entry " + key);
							}
						}
					} else {
						reload = true;
					}
				}
			} finally {
				// Make sure we release the usage count for this EntryUpdateState since we don't use it anymore. If the
				// current thread started the update, then the counter was
				// increased by one in startUpdate()
				releaseUpdateState(updateState, key);
			}
		}

		dispatchCacheMapAccessEvent(accessEventType, cacheEntry, cacheEntry);

		// If we didn't end up getting a hit then we need to throw a NRE
		if (accessEventType != CacheAccessEventType.HIT) {
			throw new NeedsRefreshException("it doesn't hit so that you must to refresh the cache entry!");
		}

		// If reload is true then another thread must have successfully rebuilt the cache entry
		try {
			if (reload) {
				cacheEntry = (CacheEntry) cacheMap.get(type,topicName,key);

				if (cacheEntry != null) {
					content = cacheEntry.getContent();
				} else {
					log.error("Could not reload cache entry after waiting for it to be rebuilt");
					throw new NeedsRefreshException("you must refresh the content of cache.");
				}
			}
		} finally {
			// 刷次年缓存实体
			this.flush(cacheEntry, cacheEntry);
		}

		return content;
	}

	/**
	 * Set the listener to use for data persistence. Only one <code>PersistenceListener</code> can be configured per
	 * cache.
	 *
	 * @param listener The implementation of a persistance listener
	 */
	public void setPersistenceListener(FilePersistListener listener) {
		cacheMap.setPersistenceListener(listener);
	}

	/**
	 * @return the refreshPolicy
	 */
	public EntryRefreshPolicy getRefreshPolicy() {
		return refreshPolicy;
	}

	/**
	 * @param refreshPolicy the refreshPolicy to set
	 */
	public void setRefreshPolicy(EntryRefreshPolicy refreshPolicy) {
		this.refreshPolicy = refreshPolicy;
	}

	/**
	 * Retrieves the currently configured <code>PersistenceListener</code>.
	 *
	 * @return the cache's <code>PersistenceListener</code>, or <code>null</code> if no listener is configured.
	 */
	public FilePersistListener getPersistenceListener() {
		return cacheMap.getPersistenceListener();
	}

	/**
	 * Register a listener for Cache events. The listener must implement one of the child interfaces of the
	 * {@link CacheEventListener} interface.
	 *
	 * @param listener The object that listens to events.
	 * @since 2.4
	 */
	public void addCacheEventListener(CacheEventListener listener) {
		this.listenerList.add(CacheEventListener.class, listener);
	}

	/**
	 * Register a listener for Cache events. The listener must implement one of the child interfaces of the
	 * {@link CacheEventListener} interface.
	 *
	 * @param listener The object that listens to events.
	 * @param clazz the type of the listener to be added
	 */
	private void addCacheEventListener(CacheEventListener listener, Class clazz) {
		if (CacheEventListener.class.isAssignableFrom(clazz)) {
			listenerList.add(clazz, listener);
		} else {
			log.error("The class '" + clazz.getName() + "' is not a CacheEventListener. Ignoring this listener.");
		}
	}

	/**
	 * Returns the list of all CacheEventListeners.
	 * 
	 * @return the CacheEventListener's list of the Cache
	 */
	public EventListenerList getCacheEventListenerList() {
		return listenerList;
	}

	public void cancelUpdate(String key) {
		EntryUpdateState state;

		if (key != null) {
			synchronized (updateStates) {
				state = (EntryUpdateState) updateStates.get(key);

				if (state != null) {
					synchronized (state) {
						int usageCounter = state.cancelUpdate();
						state.notify();

						checkEntryStateUpdateUsage(key, state, usageCounter);
					}
				} else {
					if (log.isErrorEnabled()) {
						log.error("internal error: expected to get a state from key [" + key + "]");
					}
				}
			}
		}
	}

	/**
	 * Utility method to check if the specified usage count is zero, and if so remove the corresponding EntryUpdateState
	 * from the updateStates. This is designed to factor common code. Warning: This method should always be called while
	 * holding both the updateStates field and the state parameter
	 * 
	 * @throws Exception
	 */
	private void checkEntryStateUpdateUsage(Object key, EntryUpdateState state, int usageCounter) {
		// Clean up the updateStates map to avoid a memory leak once no thread is using this EntryUpdateState instance
		// anymore.
		if (usageCounter == 0) {
			EntryUpdateState removedState = (EntryUpdateState) updateStates.remove(key);
			if (state != removedState) {
				if (log.isErrorEnabled()) {
					try {
						throw new Exception("OSCache: internal error: removed state [" + removedState + "] from key [" + key + "] whereas we expected [" + state + "]");
					} catch (Exception e) {
						log.error(e);
					}
				}
			}
		}
	}

	/**
	 * Flush all entries in the cache on the given date/time.
	 *
	 * @param date The date at which all cache entries will be flushed.
	 */
	public void pointToFlushDate(Date date) {
		pointToFlushDate(date, null);
	}

	/**
	 * Flush all entries in the cache on the given date/time.
	 *
	 * @param date The date at which all cache entries will be flushed.
	 * @param origin The origin of this flush request (optional)
	 */
	private void pointToFlushDate(Date date, Object origin) {
		flushDateTime = date;

		if (listenerList.getListenerCount() > 0) {
			dispatchCachewideEvent(CachewideEventType.CACHE_FLUSHED, date, origin);
		}
	}

	/**
	 * Flush the cache entry (if any) that corresponds to the cache key supplied. This call will flush the entry from
	 * the cache and remove the references to it from any cache groups that it is a member of. On completion of the
	 * flush, a <tt>CacheEntryEventType.ENTRY_FLUSHED</tt> event is fired.
	 *
	 * @param key The key of the entry to flush
	 */
	public void flush(Object key) {
		flush(key, null);
	}

	/**
	 * Flush the cache entry (if any) that corresponds to the cache key supplied. This call will mark the cache entry as
	 * flushed so that the next access to it will cause a {@link NeedsRefreshException}. On completion of the flush, a
	 * <tt>CacheEntryEventType.ENTRY_FLUSHED</tt> event is fired.
	 *
	 * @param key The key of the entry to flush
	 * @param origin The origin of this flush request (optional)
	 */
	private void flush(Object key, Object origin) {
		flush(getCacheEntry(key), origin);
	}

	/**
	 * Flushes all objects that belong to the supplied group. On completion this method fires a
	 * <tt>CacheEntryEventType.GROUP_FLUSHED</tt> event.
	 *
	 * @param group The group to flush
	 */
	public void flushGroup(String group) {
		flushGroup(group, null);
	}

	/**
	 * Flushes all unexpired objects that belong to the supplied group. On completion this method fires a
	 * <tt>CacheEntryEventType.GROUP_FLUSHED</tt> event.
	 *
	 * @param group The group to flush
	 * @param origin The origin of this flush event (optional)
	 */
	public void flushGroup(String group, Object origin) {
		// Flush all objects in the group
		// Set groupEntries = cacheMap.getGroup(group);
		// Set groupEntries = cacheMap.getGroup(group);
		Set groupEntries = null;
		if (groupEntries != null) {
			Iterator itr = groupEntries.iterator();
			String key;
			CacheEntry entry;

			while (itr.hasNext()) {
				key = (String) itr.next();
				entry = (CacheEntry) cacheMap.get(type,topicName,key);

				if ((entry != null) && !entry.needsRefresh(CacheEntry.INDEFINITE_EXPIRY)) {
					flush(entry, NESTED_EVENT);
				}
			}
		}

		if (listenerList.getListenerCount() > 0) {
			dispatchCacheGroupEvent(CacheEntryEventType.GROUP_FLUSHED, group, origin);
		}
	}

	/**
	 * Put an object in the cache specifying the key to use.
	 *
	 * @param key Key of the object in the cache.
	 * @param content The object to cache.
	 */
	public void put(Object key, Object content) {//
		put(key, content, null, this.refreshPolicy, null);
	}

	/**
	 * Put an object in the cache specifying the key and refresh policy to use.
	 *
	 * @param key Key of the object in the cache.
	 * @param content The object to cache.
	 * @param policy Object that implements refresh policy logic
	 */
	public void put(Object key, Object content, EntryRefreshPolicy policy) {
		put(key, content, null, policy, null);
	}

	/**
	 * Put in object into the cache, specifying both the key to use and the cache groups the object belongs to.
	 *
	 * @param key Key of the object in the cache
	 * @param content The object to cache
	 * @param groups The cache groups to add the object to
	 */
	public void put(Object key, Object content, String[] groups) {
		put(key, content, groups, this.refreshPolicy, null);
	}

	/**
	 * Put an object into the cache specifying both the key to use and the cache groups the object belongs to.
	 *
	 * @param key Key of the object in the cache
	 * @param groups
	 * @param content
	 * @param policy
	 */
	public void put(Object key,Object content, String[] groups, EntryRefreshPolicy policy, Object origin) {

		CacheEntry cacheEntry = this.getCacheEntry(key);

		if (cacheEntry == null)// 表示从缓存容器中获取到的值不存在或者已经过去，则直接新建一个
			cacheEntry = new CacheEntry(key,content,policy);
		else {
			if(cacheEntry.getPolicy() == null){
				cacheEntry.setPolicy(policy);
			}
			// 刷新缓存实体的创建时间和访问的时间
			this.flush(key);
		}
		// 不管这个缓存实体是否存在，都要设置其新的内容，并更新最后一次更新的时间
		cacheEntry.setContent(content);
		// 这里需要被判断 当传进的组有效时 才处理，以及更新最后的访问时间
		if (groups != null && groups.length > 0)
			cacheEntry.setGroups(groups);

		cacheMap.put(type,topicName,key, cacheEntry);

		// Signal to any threads waiting on this update that it's now ready for them
		// in the cache!
		completeUpdate(key);

		// 对事件进行处理
		if (listenerList.getListenerCount() > 0) {

			if (cacheEntry.isNew()) {
				// 分派缓存实体事件
				dispatchCacheEntryEvent(CacheEntryEventType.ENTRY_ADDED, new CacheEntryEvent(this, cacheEntry, cacheEntry));
			} else {
				dispatchCacheEntryEvent(CacheEntryEventType.ENTRY_UPDATED, new CacheEntryEvent(this, cacheEntry, cacheEntry));
			}
		}
	}

	/**
	 * Unregister a listener for Cache events.
	 *
	 * @param listener The object that currently listens to events.
	 * @param clazz The registrated class of listening object.
	 */
	private void removeCacheEventListener(CacheEventListener listener, Class clazz) {
		listenerList.remove(clazz, listener);
	}

	/**
	 * Unregister a listener for Cache events.
	 *
	 * @param listener The object that currently listens to events.
	 * @since 2.4
	 */
	public void removeCacheEventListener(CacheEventListener listener) {
		listenerList.remove(CacheEventListener.class, listener);
	}

	/**
	 */
	private CacheEntry getCacheEntry(Object key) {
		if (key == null) {
			throw new IllegalArgumentException("getCacheEntry called with an empty or null key");
		}

		// 从缓存容器中获取
		return (CacheEntry) cacheMap.get(type,topicName,key);

	}

	/**
	 * Indicates whether or not the cache entry is stale.
	 *
	 * @param cacheEntry need to handle the cache entry
	 * @param refreshPeriod the valid time of a cache entry
	 * @param cronExpiry a time expression based on unix that the cache entry should expire at. If the cache entry was
	 *            refreshed prior to the most recent match for the cron expression, the entry will be considered stale.
	 * @return whether a cache entry expired
	 */
	protected boolean isStale(CacheEntry cacheEntry, int refreshPeriod, String cronExpiry) {
		boolean result = cacheEntry.needsRefresh(refreshPeriod) || isFlushed(cacheEntry);

		if ((!result) && (cronExpiry != null) && (cronExpiry.length() > 0)) {
			try {
				FastCronParser parser = new FastCronParser(cronExpiry);
				result = result || parser.hasMoreRecentMatch(cacheEntry.getLastUpdate());
			} catch (ParseException e) {
				log.warn(e);
			}
		}

		return result;
	}

	/**
	 * Get the updating cache entry from the update map. If one is not found, create a new one (with state
	 * {@link EntryUpdateState#NOT_YET_UPDATING}) and add it to the map.
	 *
	 * @param key The cache key for this entry
	 * @return the CacheEntry that was found (or added to) the updatingEntries map.
	 */
	protected EntryUpdateState getUpdateState(Object key) {
		EntryUpdateState updateState;

		synchronized (updateStates) {
			// Try to find the matching state object in the updating entry map.
			updateState = (EntryUpdateState) updateStates.get(key);

			if (updateState == null) {
				// It's not there so add it.
				updateState = new EntryUpdateState();
				updateStates.put(key, updateState);
			} else {
				// Otherwise indicate that we start using it to prevent its removal until all threads are done with it.
				updateState.incrementUsageCounter();
			}
		}

		return updateState;
	}

	/**
	 * releases the usage that was made of the specified EntryUpdateState. When this reaches zero, the entry is removed
	 * from the map.
	 * 
	 * @param state the state to release the usage of
	 * @param key the associated key.
	 */
	protected void releaseUpdateState(EntryUpdateState state, Object key) {
		synchronized (updateStates) {
			checkEntryStateUpdateUsage(key, state, state.decrementUsageCounter());
		}
	}

	/**
	 * Completely clears the cache.
	 */
	protected void clear() {
		cacheMap.clear();
	}

	/**
	 * Removes the update state for the specified key and notifies any other threads that are waiting on this object.
	 * This is called automatically by the {@link #put} method, so it is possible that no EntryUpdateState was hold when
	 * this method is called.
	 *
	 * @param key The cache key that is no longer being updated.
	 */
	protected void completeUpdate(Object key) {
		EntryUpdateState state;

		synchronized (updateStates) {
			state = (EntryUpdateState) updateStates.get(key);//

			if (state != null) {
				synchronized (state) {
					int usageCounter = state.completeUpdate();
					state.notifyAll();//

					checkEntryStateUpdateUsage(key, state, usageCounter);

				}
			} else {
				// If putInCache() was called directly (i.e. not as a result of a NeedRefreshException) then no
				// EntryUpdateState would be found.
			}
		}
	}

	/**
	 * Completely removes a cache entry from the cache and its associated cache groups.
	 *
	 * @param key The key of the entry to remove.
	 */
	public Object remove(String key) {
		return removeEntry(key, null);
	}

	/**
	 * Completely removes a cache entry from the cache and its associated cache groups.
	 *
	 * @param key The key of the entry to remove.
	 * @param origin The origin of this remove request.
	 */
	private Object removeEntry(String key, Object origin) {
		CacheEntry cacheEntry = (CacheEntry) cacheMap.get(type,topicName,key);
		cacheMap.remove(type,topicName,key);

		if (listenerList.getListenerCount() > 0) {
			dispatchCacheEntryEvent(CacheEntryEventType.ENTRY_REMOVED, new CacheEntryEvent(this, cacheEntry, cacheEntry));
		}

		return cacheEntry.getContent();
	}

	/**
	 * Dispatch a cache entry event to all registered listeners.
	 *
	 * @param eventType The type of event (used to branch on the proper method)
	 * @param event The event that was fired
	 */
	private void dispatchCacheEntryEvent(CacheEntryEventType eventType, CacheEntryEvent event) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		/**
		 * Process the listeners last to first, notifying， those that are interested in this event。这里想要知道为什么是这样子的遍历，就要知道
		 * Listener 的底层是怎么样的实现。他的实现是event-source对一对一对线性存储
		 */
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i + 1] instanceof CacheEntryEventListener) {
				CacheEntryEventListener listener = (CacheEntryEventListener) listeners[i + 1];
				if (eventType.equals(CacheEntryEventType.ENTRY_ADDED)) {// add a cache entry
					listener.cacheEntryAdded(event);
				} else if (eventType.equals(CacheEntryEventType.ENTRY_UPDATED)) {// the cache entry has in cache so
																					// update it
					listener.cacheEntryUpdated(event);
				} else if (eventType.equals(CacheEntryEventType.ENTRY_FLUSHED)) {//
					listener.cacheEntryFlushed(event);
				} else if (eventType.equals(CacheEntryEventType.ENTRY_REMOVED)) {// the event that remove a cache from
																					// the cache container
					listener.cacheEntryRemoved(event);
				}
			}
		}
	}

	/**
	 * Dispatch a cache group event to all registered listeners.
	 *
	 * @param eventType The type of event (this is used to branch to the correct method handler)
	 * @param group The cache group that the event applies to
	 * @param origin The origin of this event (optional)
	 */
	private void dispatchCacheGroupEvent(CacheEntryEventType eventType, String group, Object origin) {
		CacheGroupEvent event = new CacheGroupEvent(this, origin, group);

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i + 1] instanceof CacheEntryEventListener) {
				CacheEntryEventListener listener = (CacheEntryEventListener) listeners[i + 1];
				if (eventType.equals(CacheEntryEventType.GROUP_FLUSHED)) {// ��ˢ���¼� ����
					listener.cacheGroupFlushed(event);
				}
			}
		}
	}

	/**
	 * Dispatch a cache map access event to all registered listeners.
	 *
	 * @param eventType The type of event
	 * @param entry The entry that was affected.
	 * @param origin The origin of this event (optional)
	 */
	private void dispatchCacheMapAccessEvent(CacheAccessEventType eventType, CacheEntry entry, Object origin) {
		CacheAccessEvent event = new CacheAccessEvent(eventType, entry, origin);

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i + 1] instanceof CacheAccessEventListener) {
				CacheAccessEventListener listener = (CacheAccessEventListener) listeners[i + 1];
				listener.accessed(event);// 向缓存中访问一个缓存实体，这个访问的过程有三种状态，
			}
		}
	}

	/**
	 * Dispatches a cache-wide event to all registered listeners.
	 *
	 * @param eventType The type of event (this is used to branch to the correct method handler)
	 * @param origin The origin of this event (optional)
	 */
	private void dispatchCachewideEvent(CachewideEventType eventType, Date date, Object origin) {
		CachewideEvent event = new CachewideEvent(this, date, origin);

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] instanceof CacheEntryEventListener) {
				if (eventType.equals(CachewideEventType.CACHE_FLUSHED)) {
					CacheEntryEventListener listener = (CacheEntryEventListener) listeners[i + 1];
					listener.cacheFlushed(event);
				}
			}
		}
	}

	/**
	 * Flush a cache entry. On completion of the flush, a <tt>CacheEntryEventType.ENTRY_FLUSHED</tt> event is fired.
	 *
	 * @param entry The entry to flush
	 * @param origin The origin of this flush event (optional)
	 */
	private void flush(CacheEntry entry, Object origin) {
		// 缓存实体刷新，值得值刷新他的几个状态量 刷新他的创建时间和最后一次访问的时间
		entry.flush();// just to change the wasFlushed status

		// Trigger an ENTRY_FLUSHED event. [CACHE-107] Do this for all flushes.
		if (listenerList.getListenerCount() > 0) {
			CacheEntryEvent event = new CacheEntryEvent(this, entry, entry);
			dispatchCacheEntryEvent(CacheEntryEventType.ENTRY_FLUSHED, event);
		}
	}

	/**
	 * @return the total number of cache entries held in this cache.
	 */
	public int getSize() {
		synchronized (cacheMap) {
			return cacheMap.size();
		}
	}

	/**
	 * Test support only: return the number of EntryUpdateState instances within the updateStates map.
	 */
	protected int getNbUpdateState() {
		synchronized (updateStates) {
			return updateStates.size();
		}
	}

	/**
	 * Test support only: return the number of entries currently in the cache map
	 */
	public int getNbEntries() {
		synchronized (cacheMap) {
			return cacheMap.size();
		}
	}
	
	public Set<java.util.Map.Entry<Object, Object>> getEntrySet(){
		return this.cacheMap.getAllEntrySet();
	}
	
	public Set<Object> getAllKetSet(){
		return this.cacheMap.getAllKeySet();
	}

	public String getTopicName() {
		return topicName;
	}
}
