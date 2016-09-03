package audaque.com.pbting.cache.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.event.EventListenerList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import audaque.com.pbting.cache.base.info.CacheConfig;
import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.base.persistence.CecurityDiskPersistenceListener;
import audaque.com.pbting.cache.base.persistence.FilePersistListener;
import audaque.com.pbting.cache.base.refresh.policy.EntryRefreshPolicy;
import audaque.com.pbting.cache.base.refresh.policy.ExpiresRefreshPolicy;
import audaque.com.pbting.cache.event.CacheEventListener;
import audaque.com.pbting.cache.house.DataMgr;
import audaque.com.pbting.cache.list.eventImp.CacheAccessEventListenerImpl;
import audaque.com.pbting.cache.list.eventImp.CacheEntryEventListenerImpl;
import audaque.com.pbting.cache.list.eventImp.ScopeEventListenerImpl;
import audaque.com.pbting.cache.list.eventImp.StatisticListenerImpl;
import audaque.com.pbting.cache.util.StringUtils;

/**
 */
public abstract class AbstractCacheFactory implements CacheFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static transient final Log log = LogFactory.getLog(AbstractCacheFactory.class);

	protected boolean lazyCreate = false;//

	protected CacheConfig config = null;

	public static final int ONE_SECONDS = 1000;

	public static final int ONE_MINIUTE = 60 * ONE_SECONDS;

	/**
	 * Holds a list of all the registered event listeners. Event listeners are specified using the
	 * {@link #CACHE_ENTRY_EVENT_LISTENERS_KEY} configuration key.
	 */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * The algorithm class being used, as specified by the
	 */
	protected int refreshPeriod = -1;

	// 每个缓存容器的key用逗号隔开,然后用hashmap 存储
	private Map<String, HighCache> mapCacheTopic = null;

	/**
	 * The cache capacity (number of entries), as specified by the
	 */
	protected int cacheCapacity = 1000;

	/**
	 * Whether the cache blocks waiting for content to be build, or serves stale content instead. This value can be
	 * specified using the
	 */
	private boolean blocking = false;

	/**
	 * Whether or not to store the cache entries in memory. This is configurable using the property.
	 */
	private boolean memoryCaching = true;

	/**
	 * Whether the persistent cache should be used immediately or only when the memory capacity has been reached, ie.
	 * overflow only. This can be set via
	 */
	private boolean overflowPersistence = true;

	/**
	 * Whether the disk cache should be unlimited in size, or matched 1-1 to the memory cache. This can be set via the
	 * {@link #CACHE_DISK_UNLIMITED_KEY} configuration property.
	 */
	private boolean unlimitedDiskCache = false;

	/**
	 * 
	 */
	protected String type = "cache" ;
	
	public AbstractCacheFactory(Properties proeProperties) {
		// initialize config property
		this.initConfiguration(proeProperties);

		// initialize some parameters
		this.initParameter();
	}

	public AbstractCacheFactory(String configPath) {

		initConfiguration(configPath);

		initParameter();
	}

	public boolean isBlocking() {

		return this.blocking;
	}

	public Map<String, HighCache> getMapCacheType() {
		return mapCacheTopic;
	}

	public void setMapCacheType(Map<String, HighCache> mapCacheType) {
		this.mapCacheTopic = mapCacheType;
	}

	/**
	 * @param pro
	 */
	public void setCapacity(int capacity) {

		this.cacheCapacity = capacity;
	}

	public int getCapacity() {

		return this.cacheCapacity;
	}

	public boolean isMemoryCache() {

		return this.memoryCaching;
	}

	public String getProperty(String key) {

		return this.config.getProperty(key);
	}

	public boolean isUnlimitedDiskCache() {

		return this.unlimitedDiskCache;
	}

	public boolean isOverflowPersistence() {

		return this.overflowPersistence;
	}

	public void setOverflowPersistence(boolean isoverflowPer) {

		this.overflowPersistence = isoverflowPer;
	}

	/**
	 * @return
	 */
	protected CacheEventListener[] getCacheEventListeners() {

		CacheEventListener[] listeners = new CacheEventListener[4];

		listeners[0] = new CacheAccessEventListenerImpl();
		listeners[1] = new CacheEntryEventListenerImpl();
		listeners[2] = new ScopeEventListenerImpl();
		listeners[3] = new StatisticListenerImpl();

		return listeners;

	}

	public HighCache setPersistenceListener(HighCache nchuCache, String persistListenerClassName) {

		FilePersistListener filePersistListener = null ;
		if (!org.apache.commons.lang.StringUtils.isEmpty(persistListenerClassName)) {
			try {
				Class<?> clazz = Class.forName(persistListenerClassName);
				try {
					filePersistListener = (FilePersistListener) clazz.newInstance();
				} catch (InstantiationException e) {
					log.error("nchuCache:the class of persistence listener error."+e);
				} catch (IllegalAccessException e) {
					log.error("nchuCache:the class of persistence listener error."+e);
				}
			} catch (ClassNotFoundException cf) {
				// 如果没有配置，则使用默认的
				filePersistListener = new CecurityDiskPersistenceListener();
				log.error("nchuCache:the class of persistence listener doesn't found in nchucache.properties file."+cf);
			}
		} else {// 使用默认的
			filePersistListener = new CecurityDiskPersistenceListener();
		}
		nchuCache.setPersistenceListener(filePersistListener.config(nchuCache.getTopicName(),config));
		
		return nchuCache;
	}

	public HighCache setRefreshPolicy(HighCache nchuCache, String cacheRereshPolicyClass) {
		if (!StringUtils.isEmpty(cacheRereshPolicyClass)) {//

			try {
				Class clazz = Class.class.forName(cacheRereshPolicyClass);

				try {
					if (clazz.newInstance() instanceof EntryRefreshPolicy) {
						nchuCache.setRefreshPolicy((EntryRefreshPolicy) clazz.newInstance());
					} else {
						throw new IllegalArgumentException("the value of [EntryRefreshPolicy] is invalid ,it must to extend the class of cache.refresh.policy.EntryRefreshPolicy.");
					}
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			} catch (ClassNotFoundException e) {
				nchuCache.setRefreshPolicy(new ExpiresRefreshPolicy(refreshPeriod));
				log.debug("[HighCache]the refresh policy isn't fount by the profile so use the default of that ExpireRefreshPolicy.");
			}

		} else {

			nchuCache.setRefreshPolicy(new ExpiresRefreshPolicy(refreshPeriod));
		}

		return nchuCache;
	}

	public HighCache configureStandardListeners(HighCache cache) {
		log.info("config the standard listener to the nchu cache.");
		/**
		 * 1、配置文件持久化监听器
		 */
		setPersistenceListener(cache, config.getProperty(PERSISTENCE_CLASS_KEY));

		/**
		 * 2、注册相关的事件监听器
		 */
		setEventListener(cache);

		/**
		 * 3、设置缓存刷新策略
		 */
		setRefreshPolicy(cache, config.getProperty(this.CACHE_REFRESH_POLICY));

		return cache;
	}

	private void setEventListener(HighCache cache) {
		CacheEventListener[] listeners = getCacheEventListeners();

		for (int i = 0; i < listeners.length; i++) {
			cache.addCacheEventListener(listeners[i]);
		}
	}

	protected abstract void finalizeListeners(HighCache cache);

	// 根据一个文件路径来初始化一个缓存容器
	public void initConfiguration(String configPath) {
		log.info("the method of initConfiguration in NchuCache cache factory is called.");

		this.config = new CacheConfig(configPath);
	}

	// 根据properties 来初始化一个缓存容器
	public void initConfiguration(Properties pro) {

		log.info("the method of initConfiguration in NchuCache cache factory is called.");

		this.config = new CacheConfig(pro);
	}

	/**
	 * @return the refreshPeriod
	 */
	public int getRefreshPeriod() {
		return refreshPeriod;
	}

	/**
	 * @param refreshPeriod the refreshPeriod to set
	 */
	public void setRefreshPeriod(int refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	public void initParameter() {

		log.info("the method of initParameter in NchuCache cache factory is called.");

		String lazyCreateStr = getProperty(CacheFactory.LAZY_CREATE);

		if (!StringUtils.isEmpty(lazyCreateStr))
			this.lazyCreate = Boolean.valueOf(lazyCreateStr);

		blocking = "true".equalsIgnoreCase(getProperty(CacheFactory.CACHE_BLOCKING_KEY));

		String cacheMemoryStr = getProperty(CacheFactory.CACHE_MEMORY_KEY);

		try {
			if (!StringUtils.isEmpty(cacheMemoryStr)) {
				memoryCaching = Boolean.valueOf(cacheMemoryStr);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			log.equals("the value of [cache.memory] should geivn the right value [true] or [false].");
		}

		unlimitedDiskCache = Boolean.valueOf(config.getProperty(CacheFactory.CACHE_DISK_UNLIMITED_KEY)).booleanValue();

		overflowPersistence = Boolean.valueOf(config.getProperty(CacheFactory.CACHE_PERSISTENCE_OVERFLOW_KEY)).booleanValue();

		String refreshP = config.getProperty("refresh.period");

		if (!StringUtils.isEmpty(refreshP)) {

			try {
				this.refreshPeriod = Integer.parseInt(refreshP);
				log.info("[HighCache]:the refresh period is:" + this.refreshPeriod);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		String cacheSize = config.getProperty(CACHE_CAPACITY_KEY);

		if (StringUtils.isEmpty(cacheSize)) {
			try {
				cacheCapacity = Integer.parseInt(cacheSize);
			} catch (NumberFormatException e) {
				log.error("The value supplied for the cache capacity, '" + cacheSize + "', is not a valid number. The cache capacity setting is being ignored.");
			}
		}
		
		type = config.getProperty(CacheFactory.CACHE_TYPE);
		
		// init cache numner of type
		String cacheNames = this.config.getProperty(this.CACHE_NAMES);
		if (!StringUtils.isEmpty(cacheNames)) {

			this.mapCacheTopic = new HashMap<String, HighCache>();

			StringTokenizer st = new StringTokenizer(cacheNames, ",");

			while (st.hasMoreTokens()) {
				String topic = st.nextToken();
				this.mapCacheTopic.put(topic, this.getNewCache(topic));
			}
			log.info("一共配置了" + this.mapCacheTopic.size() + " 个缓存！");
		}
		
		//register a task timer to flush the meta data index
		DataMgr.registerTask();
	}
	
	public abstract HighCache getNewCache(String topic);

	/**
	 * <pre>
	 * 	obtain a high cache named topic
	 * </pre>
	 *
	 * @param topic
	 * @return
	 */
	public HighCache getHighCache(String topic) {

		return this.mapCacheTopic.get(topic);
	}
	
	public void putHighCache(String topic,HighCache highCache){
		this.mapCacheTopic.put(topic, highCache);
	}
}
