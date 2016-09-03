package audaque.com.pbting.concurrent.cache.core;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 最少使用缓存置换算法的实现latest-seldom use cache
 * 
 * @author pbting
 */
public abstract class LRFUAbstractConcurrentCache extends AbstractConcurrentCache {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 定义用来存放访问次数Set集合
	private Set<Float> Rc_S = new ConcurrentSkipListSet<Float>(new Comparator<Float>() {
		public int compare(Float o1, Float o2) {
			// 从小到大排序
			return o1.compareTo(o2);
		};
	});

	/**
	 * key和访问次数据映射的hashmap
	 */
	private ConcurrentHashMap<Object, Float> K_Rc = null;

	/**
	 * 访问映射字段：次数和这一类key的映射排序分类处理
	 */
	private ConcurrentHashMap<Float, Map<Object, SRUKey>> Rc_ES = null;

	public LRFUAbstractConcurrentCache() {
		super();
		initParameter(DEFAULT_INITIAL_CAPACITY);
		// 开启任务
	}

	private void initParameter(int capacity) {
		K_Rc = new ConcurrentHashMap<Object, Float>(capacity);
		Rc_ES = new ConcurrentHashMap<Float, Map<Object, SRUKey>>(capacity);
	}

	public LRFUAbstractConcurrentCache(int capacity) {
		super(capacity);
		this.maxEntries = capacity;
		initParameter(capacity);
	}

	private final static Log log = LogFactory.getLog(LFUConcurrentCache.class);

	// 给一个增强因子，默认为10
	protected final static int EFACTOR = 2 << 10;

	@Override
	public void itemPut(Object key) {
		if (K_Rc.containsKey(key)) {// 已经包含该key，则该怎么办
			/**
			 * 先把这个key取出来，然后增加次数,放到适当的等级中 这个时候就成了一个获取时的操作了，改变其次序
			 */
			log.info("[HighCache]-LFUConcurrentCache.itemPut and containsKey:" + key);
			this.itemRetrieved(key);
		} else {// 不包含该怎么办
			SRUKey lfuKey = new SRUKey(key);
			lfuKey.incrementCount();// 对次数进行加一，同时还有一些任务需要处理，

			// 得到这个key的响应比
			float cacheRate = lfuKey.getCacheRate();

			// 得到一次级别上的所有map对象，放到第一个级别上
			Map<Object, SRUKey> ES = this.Rc_ES.get(cacheRate);

			if (ES == null) {
				// 表示系统的第一次
				ES = new Hashtable<Object, LRFUAbstractConcurrentCache.SRUKey>();
			}

			ES.put(key, lfuKey);
			// 如果为空，则表示第一次的，以后的第一次就可以不添加了
			if (!this.Rc_S.contains(cacheRate))
				this.Rc_S.add(cacheRate);

			// 就算有一次也要记录下来
			this.K_Rc.put(key, cacheRate);
			this.Rc_ES.put(cacheRate, ES);
			log.debug("[HighCache]-LFUConcurrentCache.itemPut and first:" + key);
			// 一次处理处理完成
		}
	}

	/**
	 * 当从缓存容器中get操作，促发该动作，目的就是改变key的一个排序
	 */
	@Override
	public void itemRetrieved(Object key) {

		// 如果这个key都没有响应的缓存响应因子，则直接返回
		if (!K_Rc.containsKey(key))
			return;

		// 1、获取当前key的一个响应比，
		Float cacheRate = K_Rc.get(key);
		// 获取这个访问次次数的一批key
		Map<Object, SRUKey> ES = this.Rc_ES.get(cacheRate);
		// 这个时候存在再次放入，则提高一次级别，的从原来的级别中移除
		SRUKey lFUKey = ES.remove(key);

		// 对这访问次数加一，同时得到新的一个响应比
		lFUKey.incrementCount();
		cacheRate = lFUKey.getCacheRate();

		log.debug("[HighCache]-LFUConcurrentCache.itemRetrieved and the access count is" + cacheRate);
		// 根据新的响应比，获取该响应比相同的key-sets
		Map<Object, SRUKey> ES_next = this.Rc_ES.get(cacheRate);

		if (ES_next == null) {
			ES_next = new ConcurrentHashMap<Object, LRFUAbstractConcurrentCache.SRUKey>();
		}
		// 然后在这一个等级内添加key和他访问次数的一个映射
		ES_next.put(key, lFUKey);
		this.Rc_ES.put(cacheRate, ES_next);
		this.K_Rc.put(key, cacheRate);
		// 必须的放进去
		// 对这个访问次数进行排序,重复的不会被添加,表明出现访问次数最高的
		log.debug("访问频率最高的次数为：" + lFUKey.count + ",and the key is:" + lFUKey.key);

		if (!this.Rc_S.contains(cacheRate))
			Rc_S.add(cacheRate);
		// 一次处理完成
	}

	/**
	 * 缓存容器中remove时，促发该动作，移除该key，重新保持最新的次序
	 */
	@Override
	public void itemRemoved(Object key) {

		// 如果这个key不存在响应的缓存相应因子。则直接返回
		if (!K_Rc.containsKey(key))
			return;

		Float count = K_Rc.get(key);

		// 获取这个访问次次数的一批key
		Map<Object, SRUKey> levelLFUKeys = this.Rc_ES.get(count);

		// 这个时候存在再次放入，则提高一次级别，的从原来的级别中移除
		SRUKey removeKey = levelLFUKeys.remove(key);
		this.K_Rc.remove(key);
		log.debug("the remove key is" + removeKey.key + " and the access count is:" + removeKey.currentCacheRate);
	}

	/**
	 * 一级缓存和二级缓存兑换时，自动替换一个缓存实体,这里是核心 有个一次级别全部扫描的关系在里面，对相同的次数又该如何处理 ,这里一定要确保移除掉，不要就会影响命中率
	 */
	@Override
	public Object removeItem() {

		SRUKey removeLfuKey = null;

		// 要记录哪一组哪一个key，然后才可以更好的移除
		Object removeKey = null;
		// 从低级别的开始扫描,也即排序迭代
		Float removeCacheRate = null;

		try {
			for (Float cacheRate : this.Rc_S) {
				removeCacheRate = cacheRate;
				Map<Object, SRUKey> ES = this.Rc_ES.get(cacheRate);
				if (ES.isEmpty()) {// 这个时候表明这个访问层已经清空，则跳到下一层
					continue;
					// 移除这一类级别中的任何一个数，因为这些的访问次数是相同的
				} else {
					// 得到key的集合，移除响应比最小的
					Iterator<Object> iter = ES.keySet().iterator();
					removeKey = iter.next();

					removeLfuKey = ES.remove(removeKey);

					if (this.K_Rc != null)
						this.K_Rc.remove(removeKey);
//					System.out.println(removeLfuKey.key + "--------------->remove key is:" + removeKey + "-------->,cache rate:" + removeCacheRate);

					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回真正的
		return removeKey;
	}

	protected static Long createTime = null;

	public class SRUKey {
		Object key;
		protected int count = 0;
		protected long lastAccessTime = 0;
		// ArrayList<Long> timeSequence = null ;
		long lastIntervalTime = 0;
		float lastFactor = 0.0f;
		float lastDx = 0.0f;
		Float currentCacheRate = 0F;

		// 记下这个key的创建时间
		public SRUKey(Object key) {
			this.key = key;
			// this.timeSequence = new ArrayList<Long>();
			if (LRFUAbstractConcurrentCache.this.createTime == null)
				LRFUAbstractConcurrentCache.createTime = System.currentTimeMillis();

			this.lastAccessTime = System.currentTimeMillis();
		}

		// 每次设置这个count值时表示一次访问，则这个时候更改lastAccessTime 的时间
		public void incrementCount() {
			this.count++;
			// 记录遇上一次访问时间的一个间隔
			// this.timeSequence.add(System.currentTimeMillis()-this.lastAccessTime+EFACTOR);
			lastIntervalTime = System.currentTimeMillis() - this.lastAccessTime + EFACTOR;
			this.lastAccessTime = System.currentTimeMillis();
		}

		/**
		 * 计算这个key的一个缓存响应比，如果这个缓存响应比越高，则越不应该留下，越小，则越应该留下
		 */
		public Float getCacheRate() {
			// 根据方差值来计算缓存响应比
			float factor = LRFUAbstractConcurrentCache.this.getFactor(this);
			this.currentCacheRate = Math.abs(Float.valueOf(this.count * this.count) / (float) Math.sqrt(factor));
//			System.err.println(this.key + "->factor:" + currentCacheRate);
			return this.currentCacheRate;
		}
	}

	public abstract float getFactor(LRFUAbstractConcurrentCache.SRUKey sruKey);
}
