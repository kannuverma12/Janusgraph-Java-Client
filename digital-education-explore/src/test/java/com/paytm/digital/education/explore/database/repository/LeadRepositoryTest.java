package com.paytm.digital.education.explore.database.repository;

import com.mongodb.MongoClient;
import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.LeadAction;
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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.util.List;

public class LeadRepositoryTest {

    private static String mongoHost = "127.0.0.1";

    private static int mongoPort = 27018;

    private static MongodExecutable mongoExe;

    private static LeadRepository leadRepository;

    private static MongoTemplate mongoTemplate;

    private static Lead leadToInsert, leadToUpdate;

    @BeforeClass
    public static void setUp() throws Exception {

        setUpMongo(mongoHost, mongoPort);

        leadRepository = new LeadRepository(mongoTemplate);

        Lead leadToAdd = new Lead();
        leadToAdd.setUserId(1234L);
        leadToAdd.setAction(LeadAction.GetUpdate);
        leadToAdd.setContactName("New Name");
        leadToAdd.setContactNumber("NewNumber");
        leadToAdd.setContactEmail("dummy-new@email.com");
        leadToAdd.setEntityId(12L);
        leadToAdd.setEntityType(EducationEntity.COURSE);
        leadToInsert = leadToAdd;

        Lead existingLead = new Lead();
        existingLead.setUserId(2345L);
        existingLead.setContactName("Existing Name");
        existingLead.setContactNumber("ExistingNumber");
        existingLead.setContactEmail("existing@email.com");
        existingLead.setEntityId(13L);
        existingLead.setEntityType(EducationEntity.COURSE);
        existingLead.setAction(LeadAction.GetUpdate);
        existingLead.setActionCount(1);

        // add existing lead to DB
        mongoTemplate.save(existingLead);
        leadToUpdate = existingLead;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        mongoTemplate.findAndRemove(new Query(Criteria.where(Lead.Constants.CONTACT_NUMBER)
                .is(leadToInsert.getContactNumber())), Lead.class);
        mongoTemplate.findAndRemove(new Query(Criteria.where(Lead.Constants.CONTACT_NUMBER)
                .is(leadToUpdate.getContactNumber())), Lead.class);

        mongoExe.stop();
    }

    @Test
    public void upsertLead_Insert() {

        leadRepository.upsertLead(leadToInsert);

        // build query to find just inserted item
        Query query = buildQueryToFindLead(leadToInsert);

        List<Lead> results = mongoTemplate.find(query, Lead.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void upsertLead_Update() {
        leadRepository.upsertLead(leadToUpdate);

        // build query to find just inserted item
        Query query = buildQueryToFindLead(leadToUpdate);

        List<Lead> results = mongoTemplate.find(query, Lead.class);

        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());

        // assert that action count is incremented by 1
        Assert.assertEquals(2, results.get(0).getActionCount());
    }

    private Query buildQueryToFindLead(Lead lead) {
        return new Query(Criteria.where(Lead.Constants.CONTACT_NUMBER).is(lead.getContactNumber())
                .and(Lead.Constants.CONTACT_EMAIL).is(lead.getContactEmail())
                .and(Lead.Constants.CONTACT_NAME).is(lead.getContactName())
                .and(Lead.Constants.ACTION).is(lead.getAction())
                .and(Lead.Constants.USER_ID).is(lead.getUserId())
                .and(Lead.Constants.ENTITY_ID).is(lead.getEntityId())
                .and(Lead.Constants.ENTITY_TYPE).is(lead.getEntityType()));
    }

    private static void setUpMongo(String mongoHost, int mongoPort) throws IOException {
        Logger logger = LoggerFactory.getLogger(LeadRepositoryTest.class);

        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(mongoHost, mongoPort, Network.localhostIsIPv6()))
                .build();

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaultsWithLogger(Command.MongoD, logger)
                .processOutput(ProcessOutput.getDefaultInstanceSilent())
                .build();

        MongodStarter mongodStarter = MongodStarter.getInstance(runtimeConfig);
        mongoExe = mongodStarter.prepare(mongodConfig);
        mongoExe.start();

        mongoTemplate = new MongoTemplate(new MongoClient(mongoHost, mongoPort), "test");
    }
}
