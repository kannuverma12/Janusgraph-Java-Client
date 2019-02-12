package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.explore.database.entity.Lead;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import javax.validation.constraints.NotNull;

@Repository
@AllArgsConstructor
public class LeadRepository {

    private MongoTemplate mongoTemplate;

    /**
     * Update/Insert record in db based on contactName, contactEmail, contactNumber,
     * entityId, entityType, userId and action
     * @param lead contact to capture
     */
    public void upsertLead(@NotNull Lead lead) {
        Query query = new Query(Criteria.where(Lead.Constants.CONTACT_NUMBER).is(lead.getContactNumber())
            .and(Lead.Constants.CONTACT_NAME).is(lead.getContactName())
            .and(Lead.Constants.CONTACT_EMAIL).is(lead.getContactEmail())
            .and(Lead.Constants.ENTITY_ID).is(lead.getEntityId())
            .and(Lead.Constants.ENTITY_TYPE).is(lead.getEntityType())
            .and(Lead.Constants.USER_ID).is(lead.getUserId())
            .and(Lead.Constants.ACTION).is(lead.getAction()));

        Date currentDate = new Date();

        Update update = new Update().set(Lead.Constants.STATUS, lead.isStatus())
            .setOnInsert(Lead.Constants.CREATED_AT, currentDate)
            .set(Lead.Constants.UPDATED_AT, currentDate)
            .inc(Lead.Constants.ACTION_COUNT, 1);

        mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().upsert(true), Lead.class);
    }
}
