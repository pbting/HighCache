package audaque.com.cache.redis.test;

import java.util.Set;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

public class RedisStringTest {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("192.168.32.129");
		jedis.auth("pbting");
		jedis.publish("topic_1", "this is a java client send a message");
		jedis.close();
	}

	private static void pingTest(Jedis jedis) {
		String ping = jedis.ping();
		System.out.println(ping);
	}

	private static void keysTest(Jedis jedis) {
		Set<String> keys = jedis.keys("*");
		for(String key : keys){
			System.out.println(key);
		}
	}

	private static void stringSetTest(Jedis jedis) {
		String name = jedis.get("name");
		Person person = new Person();
		person.setName("pbting");
		person.setAddress("江西省吉安县油田镇");
		person.setTel("+8615279132865");
		person.setAge(24);
		jedis.set(person.genertorKey(), new Gson().toJson(person));
		System.out.println(jedis.get(person.genertorKey()));
	}
}
