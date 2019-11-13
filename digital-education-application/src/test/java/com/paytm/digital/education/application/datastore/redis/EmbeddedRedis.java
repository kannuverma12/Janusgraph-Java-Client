package com.paytm.digital.education.application.datastore.redis;

import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import java.util.Objects;

@TestConfiguration
public class EmbeddedRedis {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedRedis.class);

    private static RedisServer redisServer;

    public EmbeddedRedis(@Value("${redis.port}") String port) {
        if (!Objects.isNull(redisServer)) {
            return;
        }
        redisServer = new RedisServer(Integer.parseInt(port));
        log.info("EmbeddedRedis | Starting redis server");
        try {
            redisServer.start();
        } catch (Exception ex) {
            log.error("EmbeddedRedis | Error: {} | Redis Server: {}", CommonUtils.toString(ex),
                    JsonUtils.toJson(redisServer));
            return;
        }
        log.info("EmbeddedRedis | Created successfully");
    }
}
