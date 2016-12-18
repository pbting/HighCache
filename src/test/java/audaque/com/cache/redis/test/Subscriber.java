package audaque.com.cache.redis.test;

import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {

    private static Logger logger = Logger.getLogger(Subscriber.class);

    @Override
    public void onMessage(String channel, String message) {
    	System.out.println("Message received. Channel: "+channel+", Msg: "+message);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {

    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
    	System.out.println("onSubscribe");
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {

    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {

    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {

    }
}