package com.paytm.digital.education.explore.utility;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.paytm.digital.education.database.entity.ShiftDetails;
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
import lombok.Data;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.ArrayList;

import static com.paytm.digital.education.enums.ClassType.LKG;
import static com.paytm.digital.education.enums.ClassType.UKG;
import static com.paytm.digital.education.enums.ShiftType.Morning;

public class ClassTypeEnumTest {

    private static final String MONGO_HOST = "127.0.0.1";

    private static final int MONGO_PORT = 27018;

    private static MongodExecutable MongoExe;

    private static MongoTemplate MongoTemplate;

    @BeforeClass
    public static void setUp() throws IOException {
        MainMapLookup.setMainArguments("digital-education-service");
        setUpMongo(MONGO_HOST, MONGO_PORT);

        ShiftDetailsContainer shiftDetailsContainer = new ShiftDetailsContainer(
                new ShiftDetails(LKG, UKG, Morning));

        MongoTemplate.save(shiftDetailsContainer);
    }

    @AfterClass
    public static void tearDown() {
        MongoTemplate.dropCollection(ShiftDetailsContainer.class);
        MongoExe.stop();
    }

    @Test
    public void testThatClassTypeEnumIsStoredAsStringInDB() {
        Document topDocument = MongoTemplate.executeCommand(shiftDetailsQueryCommand());
        Document document = (Document) topDocument.get("cursor", Document.class)
                .get("firstBatch", ArrayList.class).get(0);
        Document shiftDetails = document.get("shiftDetails", Document.class);
        Object classFrom = shiftDetails.get("class_from");
        Object classTo = shiftDetails.get("class_to");
        Assert.assertTrue(classFrom instanceof String);
        Assert.assertEquals(classFrom, "LKG");
        Assert.assertTrue(classTo instanceof String);
        Assert.assertEquals(classTo, "UKG");
    }

    private static String shiftDetailsQueryCommand() {
        BasicDBList andList = new BasicDBList();
        andList.add(new BasicDBObject("shiftDetails.class_from", "LKG"));
        andList.add(new BasicDBObject("shiftDetails.class_to", "UKG"));
        andList.add(new BasicDBObject("shiftDetails.shift_type", "Morning"));
        BasicDBObject and = new BasicDBObject("$and", andList);
        BasicDBObject command = new BasicDBObject("find", "shiftDetailsContainer");
        command.append("filter", and);
        return command.toString();
    }

    private static void setUpMongo(String mongoHost, int mongoPort) throws IOException {
        Logger logger = LoggerFactory.getLogger(ClassTypeEnumTest.class);

        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(mongoHost, mongoPort, Network.localhostIsIPv6()))
                .build();

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaultsWithLogger(Command.MongoD, logger)
                .processOutput(ProcessOutput.getDefaultInstanceSilent())
                .build();

        MongodStarter mongodStarter = MongodStarter.getInstance(runtimeConfig);
        MongoExe = mongodStarter.prepare(mongodConfig);
        MongoExe.start();

        MongoTemplate = new MongoTemplate(new MongoClient(mongoHost, mongoPort), "test");
    }

    @Data
    @org.springframework.data.mongodb.core.mapping.Document
    private static class ShiftDetailsContainer {
        @Id
        private String id;

        private final ShiftDetails shiftDetails;
    }
}
