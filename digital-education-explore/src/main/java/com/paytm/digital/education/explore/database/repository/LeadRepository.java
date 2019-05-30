package com.paytm.digital.education.explore.database.repository;

import static com.paytm.digital.education.explore.database.entity.Lead.Constants.ACTION;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.ENTITY_ID;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.ENTITY_TYPE;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.STATUS;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.USER_ID;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.STREAM;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.CITY_ID;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.STATE_ID;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.LEAD_RESPONSES;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.INTERESTED;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.UPDATED_AT;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.CREATED_AT;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.ACTION_COUNT;

import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;

@Repository
@AllArgsConstructor
public class LeadRepository {

    private MongoTemplate mongoTemplate;

    /**
     * Update/Insert record in db based on contactName, contactEmail, contactNumber,
     * entityId, entityType, userId and action
     *
     * @param lead contact to capture
     */
    public void upsertLead(@NotNull Lead lead) {
        Query query =
                new Query(Criteria.where(USER_ID).is(lead.getContactNumber())
                        .and(CITY_ID).is(lead.getCityId())
                        .and(STATE_ID).is(lead.getStateId())
                        .and(ENTITY_ID).is(lead.getEntityId())
                        .and(ENTITY_TYPE).is(lead.getEntityType())
                        .and(USER_ID).is(lead.getUserId())
                        .and(ACTION).is(lead.getAction())
                        .and(STREAM).is(lead.getStream())
                        .and(LEAD_RESPONSES).is(lead.getBaseLeadResponse())
                        .and(INTERESTED).is(lead.isInterested()));

        Date currentDate = new Date();

        Update update = new Update().set(Lead.Constants.STATUS, lead.isStatus())
                .setOnInsert(CREATED_AT, currentDate)
                .set(UPDATED_AT, currentDate)
                .set(CITY_ID, lead.getCityId())
                .set(STATE_ID, lead.getStateId())
                .set(EN)
                .inc(ACTION_COUNT, 1);
        System.out.println(update.toString());

        mongoTemplate
                .findAndModify(query, update, new FindAndModifyOptions().upsert(true), Lead.class);
    }

    public List<Lead> fetchLeadByEntityTypeAndUserIdAndEntityIdIn(EducationEntity entityType,
            Long userId, List<Long> entityIds) {
        Query mongoQuery = new Query(
                Criteria.where(ENTITY_TYPE).is(entityType).and(ENTITY_ID).in(entityIds).and(USER_ID)
                        .is(userId).and(STATUS).is(true));
        return mongoTemplate.find(mongoQuery, Lead.class);
    }
}
