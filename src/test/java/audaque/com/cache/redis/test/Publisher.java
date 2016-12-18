package audaque.com.cache.redis.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

public class Publisher {

	private static final Logger logger = Logger.getLogger(Publisher.class);

    private final Jedis publisherJedis;

    private final String channel;

    public Publisher(Jedis publisherJedis, String channel) {
        this.publisherJedis = publisherJedis;
        this.channel = channel;
    }

    public void start() {
        logger.info("Type your message (quit for terminate)");
		int i = 0 ;
		String message = "java topic_1 "+(i++);
		while (true) {
			publisherJedis.publish(channel, message);
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			message = "java topic_1 "+(i++);
		}
    }
}
