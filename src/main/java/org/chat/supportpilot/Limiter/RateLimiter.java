package org.chat.supportpilot.Limiter;

import org.springframework.stereotype.Component;
import redis.clients.jedis.RedisClient;

import java.time.Duration;

@Component
public class RateLimiter {
    private static final int max_request = 10;
    private static final int time_limit = 60;

    private final RedisClient redisClient;

    public RateLimiter(RedisClient client){
        this.redisClient = client;
    }

    public boolean allow(String userId){
        String key = "rate:"+userId;
        long count = redisClient.incr(key);
        if(redisClient.ttl(key) == -1){
            redisClient.expire(key, time_limit);
        }
        return count<=max_request;
    }
}
