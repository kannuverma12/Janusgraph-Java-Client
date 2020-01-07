package com.paytm.digital.education.application.config;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class EmbeddedServicesConfiguration {
    private static Logger logger = LoggerFactory.getLogger(EmbeddedServicesConfiguration.class);

    private final String mongoHost;
    private final int    mongoPort;
    private final int    redisPort;

    private RedisServer      redisServer;
    private MongodExecutable mongodExecutable;

    public EmbeddedServicesConfiguration(@Value("${redis.port}") String redisPort,
            @Value("${mongo.host}") String mongoHost,
            @Value("${mongo.port}") String mongoPort) {
        this.redisPort = Integer.parseInt(redisPort);
        this.mongoHost = mongoHost;
        this.mongoPort = Integer.parseInt(mongoPort);
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        addShutDownHook();
        setUpMongo();
        setUpRedis();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
        mongodExecutable.stop();
    }

    private void setUpRedis() {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    private void setUpMongo() throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(mongoHost, mongoPort, Network.localhostIsIPv6()))
                .build();

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaultsWithLogger(Command.MongoD, logger)
                .processOutput(ProcessOutput.getDefaultInstanceSilent())
                .build();

        MongodStarter mongodStarter = MongodStarter.getInstance(runtimeConfig);
        mongodExecutable = mongodStarter.prepare(mongodConfig);
        mongodExecutable.start();
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (redisServer != null) {
                redisServer.stop();
            }
            if (mongodExecutable != null) {
                mongodExecutable.stop();
            }
        }));
    }
}
