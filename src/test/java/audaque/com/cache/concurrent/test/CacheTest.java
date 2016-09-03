package audaque.com.cache.concurrent.test;

import audaque.com.pbting.cache.base.info.HighCache;
import audaque.com.pbting.cache.exception.NeedsRefreshException;
import audaque.com.pbting.cache.factory.GeneralCacheFactory;

public class CacheTest {

	public static void main(String[] args) throws Exception {
		//
		System.out.println(countMoney(100, 75));
		System.out.println(Math.floor(45.676));// 向下取整函数
		System.getProperty("name");
	}

	private static int countMoney(int money, int discounts) {
		int needMoney = (int) (Math.floor(money * discounts / 100.00));// 检测用户货币
		if (money > 0 && needMoney == 0)
			needMoney = 1;
		return needMoney;
	}

	private static void testCache() throws InterruptedException, NeedsRefreshException {
		GeneralCacheFactory.initByConfigPath("META-INF/prism.properties");

		GeneralCacheFactory factory = GeneralCacheFactory.getInstance();

		HighCache highCache = factory.getCache();
		System.out.println(highCache);
		highCache.put("key-1", "val-1");
		Thread.sleep(2000);
		highCache.put("key-2", "val-2");
		Thread.sleep(2000);
		highCache.put("key-3", "val-3");
		Thread.sleep(2000);
		// 连续访问四次
		String value = (String) highCache.get("key-1");
		value = (String) highCache.get("key-1");
		value = (String) highCache.get("key-1");
		System.out.println("value is :" + value);
		highCache.put("key-4", "val-4");
		highCache.put("key-2", "new-val-2");// 这个时候应该会置换一个缓存实体，同时已经满了，需要持久化

		System.out.println("key-2 value is:" + highCache.get("key-2"));
	}
}
