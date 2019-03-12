package com.paytm.digital.education.explore.database.repository;

import static com.paytm.digital.education.explore.database.entity.Lead.Constants.ACTION;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.CONTACT_EMAIL;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.CONTACT_NAME;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.COURSE_ID;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.ENTITY_ID;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.ENTITY_TYPE;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.STATUS;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.USER_ID;

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
                new Query(Criteria.where(Lead.Constants.CONTACT_NUMBER).is(lead.getContactNumber())
                        .and(COURSE_ID).is(lead.getCourseId())
                        .and(CONTACT_NAME).is(lead.getContactName())
                        .and(CONTACT_EMAIL).is(lead.getContactEmail())
                        .and(ENTITY_ID).is(lead.getEntityId())
                        .and(ENTITY_TYPE).is(lead.getEntityType())
                        .and(USER_ID).is(lead.getUserId())
                        .and(ACTION).is(lead.getAction()));

        Date currentDate = new Date();

        Update update = new Update().set(Lead.Constants.STATUS, lead.isStatus())
                .setOnInsert(Lead.Constants.CREATED_AT, currentDate)
                .set(Lead.Constants.UPDATED_AT, currentDate)
                .inc(Lead.Constants.ACTION_COUNT, 1);

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
