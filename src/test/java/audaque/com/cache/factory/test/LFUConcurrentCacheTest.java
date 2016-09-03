package audaque.com.cache.factory.test;

import audaque.com.pbting.concurrent.cache.core.LFUConcurrentCache;

public class LFUConcurrentCacheTest {

	public static void main(String[] args) {
		LFUConcurrentCache lfuCache = new LFUConcurrentCache(3);
		
		System.out.println(lfuCache);
	}
}
