package com.paytm.digital.education.cache.redis;

import com.paytm.digital.education.config.RedisConfiguration;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

@Service()
public class RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);

    public RedisCacheService() {
    }

    public void addKeyToCache(String pKey, String pValue, Integer lRecordTtl) {
        Jedis jedis = null;
        log.debug("Add key to cache key : {}, value: {}, ttl : {}",
                pKey, pValue, lRecordTtl);
        try {
            JedisPool jedisPool = RedisConfiguration.getJedisPool();
            jedis = jedisPool.getResource();
            if (lRecordTtl != -1) {
                jedis.setex(pKey, lRecordTtl, pValue);
            } else {
                jedis.set(pKey, pValue);
            }
        } catch (Exception ex) {
            log.error("Exception while adding key : {} , value : {}, ttl : {}",
                    ex, pKey, pValue, lRecordTtl);
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
            log.error("Exception while getting value from redis, key : {}", ex, pKey);
        } finally {
            if (Objects.nonNull(jedis)) {
                jedis.close();
            }

        }
        return value;
    }

    public void delKeyFromCache(String key) {
        log.debug("Delete Value From Cache, key : {} ", key);
        Jedis jedis = null;
        try {
            JedisPool jedisPool = RedisConfiguration.getJedisPool();
            jedis = jedisPool.getResource();
            jedis.del(key);
        } catch (Exception ex) {
            log.error("Error while deleting value from redis, key : {}", ex, key);
        } finally {
            if (Objects.nonNull(jedis)) {
                jedis.close();
            }

        }
    }
}
