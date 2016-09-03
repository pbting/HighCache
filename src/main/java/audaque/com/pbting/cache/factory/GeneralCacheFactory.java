package audaque.com.pbting.cache.factory;

import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.event.CacheEventListener;
import audaque.com.pbting.cache.util.StringUtils;

/**
 * 
 */
public class GeneralCacheFactory extends AbstractCacheFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static transient final Log log = LogFactory.getLog(GeneralCacheFactory.class);

	private static class SingaltonCacheFactory {

		private volatile static GeneralCacheFactory CACHE_FACTORY = null;

		public static GeneralCacheFactory getSinglaton() {
			if (CACHE_FACTORY == null) {
				if(!properties.isEmpty()){
					CACHE_FACTORY = new GeneralCacheFactory(properties);
				}else if(!StringUtils.isEmpty(configPath)){
					CACHE_FACTORY = new GeneralCacheFactory(configPath);
				}else{
					throw new IllegalArgumentException("初始化Cache Factory 参数为空!");
				}
			}

			return CACHE_FACTORY;
		}
	}

	private static Properties properties = new Properties();

	private static String configPath = new String();
	
	public static void initByPropertoes(Properties properties) {
		if (properties != null) {
			GeneralCacheFactory.properties = properties;
		} else {
			log.debug(GeneralCacheFactory.class.getName() + ":参数设置为null！");
		}
	}

	public static void initByConfigPath(String configPath) {
		if (properties != null) {
			GeneralCacheFactory.configPath = configPath;
		} else {
			log.debug(GeneralCacheFactory.class.getName() + ":参数设置为null！");
		}
	}
	
	public static GeneralCacheFactory getInstance() {

		return SingaltonCacheFactory.getSinglaton();
	}

	/**
	 * Application cache
	 */
	private HighCache applicationCache = null;

	/**
	 * 
	 */
	private GeneralCacheFactory(Properties p) {
		/**
		 * 
		 */
		super(p);

		init();
	}
	
	private void init() {
		if (!this.lazyCreate)//
			initApplicationCache();
		else {
			log.info("delay to create the cache tontaner...");
		}
	}

	private GeneralCacheFactory(String configPath) {

		super(configPath);

		init();
	}

	/**
	 * @return The cache
	 */
	public HighCache getCache() {

		// 这里提供懒加载的支持
		if (applicationCache == null && this.lazyCreate) {
			initApplicationCache();
		}

		return applicationCache;
	}

	/**
	 * Shuts down the cache administrator.
	 */
	public void destroy(HighCache highCache) {
		finalizeListeners(highCache);
	}

	/**
	 * 
	 */
	private void initApplicationCache() {
		log.info("start to create cache ");

		/**
		 * 
		 */

		applicationCache = this.createHighCache("HighCache-Application");

		/**
		 * 主要完成3件事
		 */
		configureStandardListeners(applicationCache);
	}

	private HighCache createHighCache(String topic) {
		return new HighCache(this.type,topic,this.isMemoryCache(), this.isUnlimitedDiskCache(), this.isOverflowPersistence(), config.getProperty(CACHE_ALGORITHM_KEY), this.cacheCapacity, this.refreshPeriod);

	}

	// it can get a new cache container by user
	@Override
	public HighCache getNewCache(String topic) {

		HighCache tmpCache = this.createHighCache(topic);
		
		putHighCache(topic, tmpCache);
		/**
		 * it will configue the standard event listener for each cache container 。 就是说这些事件是对缓存容器的一些事件进行监听，然后做相关的处理
		 */
		configureStandardListeners(tmpCache);

		return tmpCache;
	}

	@Override
	protected void finalizeListeners(HighCache cache) {

		for (CacheEventListener lis : this.getCacheEventListeners()) {

			this.applicationCache.removeCacheEventListener(lis);//
		}

	}
}
