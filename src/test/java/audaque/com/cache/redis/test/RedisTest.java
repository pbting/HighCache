package audaque.com.cache.redis.test;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisTest {

	private Jedis jedis = null ;
	
	@Before
	public void redisCon(){
		
		jedis = new Jedis("192.168.60.134");
		
		System.out.println(jedis);
	}
	
	@Test
	public void redisString(){
		
		System.out.println("name:"+jedis.get("name"));
	}
	
	@Test
	public void redisHash(){
		Set<String> keys =  jedis.hkeys("car-2");
		
		for(String key : keys)
			System.out.println("key:"+key);
	}
	
	@Test
	public void redisList(){
		
		List<String> list = jedis.lrange("list-1", 0, -1);
		
		for(String vals : list)
			System.out.println("value :"+vals);
	}
}
