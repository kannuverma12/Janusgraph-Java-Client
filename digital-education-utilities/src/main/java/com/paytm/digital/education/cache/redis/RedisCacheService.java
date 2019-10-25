package com.paytm.digital.education.cache.redis;

import com.paytm.digital.education.config.RedisConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

@Slf4j
@Service()
public class RedisCacheService {

    public RedisCacheService() {
    }

    public void addKeyToCache(String pKey, String pValue, Integer lRecordTtl) {
        Jedis jedis = null;
        log.debug("Add key to cache key : {}, value: {}, ttl : {}",
                new Object[] {pKey, pValue, lRecordTtl});
        try {
            JedisPool jedisPool = RedisConfiguration.getJedisPool();
            jedis = jedisPool.getResource();
            if (lRecordTtl != -1) {
                jedis.setex(pKey, lRecordTtl, pValue);
            } else {
                jedis.set(pKey, pValue);
            }
        } catch (Exception ex) {
            log.error("Error while adding key : {} , value : {}, ttl : {}",
                    new Object[] {pKey, pValue, lRecordTtl});
            log.error("Error while adding key", ex);
        } finally {
            if (Objects.nonNull(jedis)) {
                jedis.close();
            }

        }
    }

    public String getValueFromCache(String pKey) {
        log.debug("Get Value From Cache, key : {} ", pKey);
        Jedis jedis = null;
        String value = null;

        try {
            JedisPool jedisPool = RedisConfiguration.getJedisPool();
            jedis = jedisPool.getResource();
            value = jedis.get(pKey);
        } catch (Exception ex) {
            log.error("Error while getting value from redis, key : {}", pKey);
            log.error("Error while getting value from redis,", ex);
        } finally {
            if (Objects.nonNull(jedis)) {
                jedis.close();
            }

        }
        return value;
    }

    public void clearCache(String cacheName) {
    }
}
