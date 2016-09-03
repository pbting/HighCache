package audaque.com.cache.factory.test;

import audaque.com.pbting.concurrent.cache.core.LFUConcurrentCache;

public class LRUCacheTest {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		// LRUCache lruCache = new LRUCache(2);

		// LRFUConcurrentCache lruCache = new LRFUConcurrentCache(2);

		// LRFUByDXConcurrentCache lruCache = new LRFUByDXConcurrentCache(2);

		// LRFUByExConcurrentCache lruCache = new LRFUByExConcurrentCache(2);

		LFUConcurrentCache lruCache = new LFUConcurrentCache(2);

		System.out.println("1------>" + lruCache);
		lruCache.get("key-2");
		lruCache.get("key-2");
		lruCache.get("key-1");
		Thread.sleep(1000);
		System.out.println("2------>" + lruCache);
		System.out.println("3------>" + lruCache);
	}

}
