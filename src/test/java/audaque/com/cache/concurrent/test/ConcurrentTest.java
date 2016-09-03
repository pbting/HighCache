package audaque.com.cache.concurrent.test;

import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import audaque.com.cache.TestBean;
import audaque.com.pbting.cache.base.info.CacheEntry;
import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.exception.NeedsRefreshException;
import audaque.com.pbting.cache.factory.GeneralCacheFactory;
import audaque.com.pbting.concurrent.cache.core.LRFUByDXConcurrentCache;

public class ConcurrentTest {

	@Test
	public void factory() {
		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();

		System.out.println(factory);
	}

	@Test
	public void highCache() {
		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();

		HighCache highCacheOne = factory.getHighCache("topicOne");
		HighCache highCacheTwo = factory.getHighCache("topicTwo");
		HighCache highCacheThree = factory.getHighCache("topicThree");

		System.out.println(highCacheOne);
		System.out.println(highCacheTwo);
		System.out.println(highCacheThree);
	}

	@Test
	public void concurrentPutByDx() throws InterruptedException {
		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();

		HighCache highCache = factory.getHighCache("topicOne");
		int i = 1;
		long start = System.currentTimeMillis();
		while (i-- >= 0) {
			highCache.put("topicOne-key-1", "topicOne-val-1");
			highCache.put("topicOne-key-2", "topicOne-val-2");
			highCache.put("topicOne-key-3", "topicOne-val-3");
			try {
				highCache.get("key-1");
				highCache.put("key-4", "val-4");
				highCache.put("key-5", "val-4");
				highCache.put("key-6", "val-4");
				highCache.put("key-7", "val-4");

			} catch (NeedsRefreshException e) {
				e.printStackTrace();
				// highCache.put("key-1", "key-1-new");
			}
		}
		System.out.println(System.currentTimeMillis() - start);
	}

	@Test
	public void putByEx() throws InterruptedException, NeedsRefreshException {
		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();
		HighCache highCache = factory.getHighCache("topicOne");
		TestBean tb1 = new TestBean(21,"name-1","广东省深圳市","+86110");
		highCache.put("key-1-new",new StringBuffer(new Gson().toJson(tb1)));
	}

	@Test
	public void getMsg() throws InterruptedException, NeedsRefreshException {
		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();
		HighCache highCache = factory.getHighCache("topicOne");
		Object value = highCache.get("key-1-new");
		System.out.println(value);
		TestBean tbs = new TestBean(20, "王辉-1-new", "广东省深圳市", "+86110119120");
		highCache.put("wanghui-1", new StringBuffer(new Gson().toJson(tbs)));
		System.out.println("wanghui-1"+highCache.get("wanghui-1"));
		TestBean tbs_1 = new TestBean(20, "王辉-new", "广东省深圳市", "+86110119120");
		highCache.put("wanghui-2", new StringBuffer(new Gson().toJson(tbs_1)));
		System.out.println("wanghui-2"+highCache.get("wanghui-2"));
		TestBean wyl = new TestBean(24,"吴颖丽-1","江西省吉安市","+8618026993099");
		highCache.put("wyl-1", new Gson().toJson(wyl));
		TestBean get = new Gson().fromJson(highCache.get("wyl-1").toString(), new TypeToken<TestBean>(){}.getType());
		System.out.println("吴颖丽"+get.getAddress());
	}
	
	@Test
	public void putByDX() throws InterruptedException, NeedsRefreshException {
		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();
		HighCache highCache = factory.getCache();
		highCache.put("key-1", "val-1");
		Thread.sleep(2000);
		highCache.put("key-2", "val-2");
		Thread.sleep(1000);
		highCache.put("key-3", "val-3");
		Thread.sleep(2000);
		// 连续访问四次
		String value = (String) highCache.get("key-1");
		value = (String) highCache.get("key-1");
		value = (String) highCache.get("key-1");
		value = (String) highCache.get("key-1");
		value = (String) highCache.get("key-1");
		System.out.println("value is :" + value);
		highCache.put("key-4", "val-4");
	}

	@Test
	public void putBylfruDefault() throws InterruptedException, NeedsRefreshException {
		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();

		HighCache highCache = factory.getCache();
		highCache.put("key-new-1", "val-1");
		Thread.sleep(2000);
		highCache.put("key-new-2", "val-2");
		Thread.sleep(1000);
		highCache.put("key-new-3", "val-3");
		Thread.sleep(2000);
		// 连续访问四次
		highCache.get("key-new-1");
		highCache.get("key-new-1");
		highCache.get("key-new-1");
		highCache.get("key-new-1");
		highCache.get("key-new-1");
		highCache.put("key-new-4", "val-4");
	}

	@Test
	public void lfruCache() throws InterruptedException {

		LRFUByDXConcurrentCache highCache = new LRFUByDXConcurrentCache(3);

		CacheEntry cacheEntry_1 = new CacheEntry("key-1","val-1");
		CacheEntry cacheEntry_2 = new CacheEntry("key-2","val-2");
		CacheEntry cacheEntry_3 = new CacheEntry("key-3","val-3");
		CacheEntry cacheEntry_4 = new CacheEntry("key-4","val-4");

		highCache.put("key-1", cacheEntry_1);
		Thread.sleep(2000);
		highCache.put("key-2", cacheEntry_2);
		Thread.sleep(2000);
		highCache.put("key-3", cacheEntry_3);
		Thread.sleep(2000);
		highCache.put("key-4", cacheEntry_4);
		Thread.sleep(2000);
		highCache.put("key-5", new CacheEntry("key-5","val-5"));

		System.out.println(highCache);
	}

	@Test
	public void lfuCache() throws InterruptedException, NeedsRefreshException {
		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();
		HighCache highCache = factory.getCache();
		highCache.put("key-1", "val-1");
		Thread.sleep(2000);
		highCache.put("key-2", "val-2");
		Thread.sleep(1000);
		highCache.put("key-3", "val-3");
		Thread.sleep(2000);
		// 连续访问四次
		highCache.get("key-1");
		highCache.put("key-4", "val-4");
	}
}
