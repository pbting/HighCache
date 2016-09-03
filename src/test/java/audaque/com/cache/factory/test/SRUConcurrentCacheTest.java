package audaque.com.cache.factory.test;

import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.exception.NeedsRefreshException;
import audaque.com.pbting.cache.factory.GeneralCacheFactory;
import audaque.com.pbting.concurrent.cache.core.LRFUByExConcurrentCache;

public class SRUConcurrentCacheTest {

	public static void main(String[] args) throws InterruptedException {
		根据期望值测();
	}

	private static void 根据均方差() throws InterruptedException {

		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();
		HighCache lfuCache = factory.getCache();

		lfuCache.put("key-1", "val-1");
		lfuCache.put("key-2", "val-2");
		lfuCache.put("key-3", "val-3");
		// 对key-3多访问一次

		try {
			lfuCache.get("key-1");
			lfuCache.get("key-3");
			lfuCache.get("key-1");
			Thread.sleep(1000);
			lfuCache.get("key-3");
			lfuCache.get("key-2");
			lfuCache.get("key-2");
			lfuCache.get("key-3");
			lfuCache.get("key-1");
			lfuCache.get("key-3");
			Thread.sleep(1000);
			lfuCache.get("key-2");
			lfuCache.get("key-2");
			lfuCache.get("key-1");
			Thread.sleep(1000);

			lfuCache.put("key-4", "val-4");
			lfuCache.put("key-4", "val-4");

			System.out.println(lfuCache);
		} catch (NeedsRefreshException e) {
			e.printStackTrace();
		}
	}

	private static void 根据期望值测() throws InterruptedException {

		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();
		HighCache lfuCache = factory.getCache();

		lfuCache.put("key-1", "val-1");
		lfuCache.put("key-2", "val-2");
		lfuCache.put("key-3", "val-3");
		// 对key-3多访问一次

		try {
			lfuCache.get("key-1");
			lfuCache.get("key-3");
			lfuCache.get("key-1");
			Thread.sleep(1000);
			lfuCache.get("key-3");
			lfuCache.get("key-2");
			lfuCache.get("key-2");
			lfuCache.get("key-3");
			lfuCache.get("key-1");
			lfuCache.get("key-3");
			Thread.sleep(1000);
			lfuCache.get("key-2");
			lfuCache.get("key-2");
			lfuCache.get("key-1");
			Thread.sleep(1000);

			lfuCache.put("key-4", "val-4");
			lfuCache.put("key-4", "val-4");
			Thread.sleep(1000);
			lfuCache.put("key-4", "val-4");

			lfuCache.put("key-5", "val-5");
			System.out.println(lfuCache);
		} catch (NeedsRefreshException e) {
			e.printStackTrace();
		}
	}

	private static void test_1(LRFUByExConcurrentCache lfuCache) throws InterruptedException {
		lfuCache.get("key-3");

		Thread.sleep(2000);

		lfuCache.put("key-3", "val-3-new");
		lfuCache.put("key-3", "val-3-new");
		lfuCache.put("key-3", "val-3-new");
		lfuCache.put("key-3", "val-3-new");
		lfuCache.put("key-3", "val-3-new");
		lfuCache.put("key-3", "val-3-new");
		lfuCache.get("key-2");

		Thread.sleep(2000);
		lfuCache.put("key-1", "val-1-new");

		Thread.sleep(1000);
		lfuCache.put("key-1", "val-1-new");
		lfuCache.get("key-2");

		lfuCache.put("key-4", "val-4");
	}
}
