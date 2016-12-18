package audaque.com.cache.redis.test;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ProgramPubSub {

	public static final String CHANNEL_NAME = "topic_1";

    private static Logger logger = Logger.getLogger(ProgramPubSub.class);

    public static void main(String[] args) throws Exception {

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        JedisPool jedisPool = new JedisPool(poolConfig, "192.168.32.129", 6379, 4);

        final Jedis subscriberJedis = jedisPool.getResource();
        subscriberJedis.auth("pbting");
        final Subscriber subscriber = new Subscriber();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("Subscribing to \"commonChannel\". This thread will be blocked.");
                    subscriberJedis.subscribe(subscriber, CHANNEL_NAME);
                    logger.info("Subscription ended.");
                } catch (Exception e) {
                    logger.error("Subscribing failed.", e);
                }
            }
        }).start();

        Jedis publisherJedis = jedisPool.getResource();
        publisherJedis.auth("pbting");
        new Publisher(publisherJedis, CHANNEL_NAME).start();
    }
}
