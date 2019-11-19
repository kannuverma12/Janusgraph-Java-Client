package com.paytm.digital.education.application.datastore.mongo;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import java.io.IOException;
import java.util.Objects;

@TestConfiguration
public class EmbeddedMongoDb {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedMongoDb.class);

    private static String           host;
    private static Integer          port;
    private static MongodExecutable executable;

    EmbeddedMongoDb(@Value("${spring.data.mongodb.host}") String host,
            @Value("${spring.data.mongodb.port}") String port) throws IOException {
        if (!Objects.isNull(executable)) {
            return;
        }
        EmbeddedMongoDb.host = host;
        EmbeddedMongoDb.port = Integer.parseInt(port);
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.V4_0)
                .net(new Net(host, Integer.parseInt(port), Network.localhostIsIPv6())).build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        executable = starter.prepare(mongodConfig);
        log.info("EmbeddedMongoDb | Setting up embedded mongo instance");
        executable.start();
        log.info("EmbeddedMongoDb | Started successfully");
    }
}
