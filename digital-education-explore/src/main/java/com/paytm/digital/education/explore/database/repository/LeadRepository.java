package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.database.entity.Lead;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Objects;
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
                new Query(Criteria.where(Lead.Constants.USER_ID).is(lead.getUserId())
                        .and(Lead.Constants.ENTITY_ID).is(lead.getEntityId())
                        .and(Lead.Constants.ENTITY_TYPE).is(lead.getEntityType()));

        Date currentDate = new Date();

        Update update = new Update().set(Lead.Constants.STATUS, lead.isStatus())
                .setOnInsert(Lead.Constants.CREATED_AT, currentDate)
                .set(Lead.Constants.UPDATED_AT, currentDate)
                .set(Lead.Constants.THIRD_PARTY_RESPONSES, lead.getBaseLeadResponse())
                .set(Lead.Constants.LAST_ACTION, lead.getAction())
                .set(Lead.Constants.STREAM, lead.getStream())
                .set(Lead.Constants.ENTITY_ID, lead.getEntityId())
                .set(Lead.Constants.ENTITY_TYPE, lead.getEntityType())
                .set(Lead.Constants.INSTITUTE_ID, lead.getInstituteId())
                .set(Lead.Constants.USER_ID, lead.getUserId())
                .set(Lead.Constants.CITY_ID, lead.getCityId())
                .set(Lead.Constants.STATE_ID, lead.getStateId())
                .inc(Lead.Constants.ACTION_COUNT, 1);

        if (Objects.nonNull(lead.getInterested())) {
            update.set(Lead.Constants.INTERESTED, lead.getInterested());
        }

        mongoTemplate
                .findAndModify(query, update, new FindAndModifyOptions().upsert(true), Lead.class);
    }

    public List<Lead> fetchLeadByEntityTypeAndUserIdAndEntityIdIn(EducationEntity entityType,
            Long userId, List<Long> entityIds) {
        Query mongoQuery = new Query(
                Criteria.where(Lead.Constants.ENTITY_TYPE).is(entityType)
                        .and(Lead.Constants.ENTITY_ID).in(entityIds).and(Lead.Constants.USER_ID)
                        .is(userId).and(Lead.Constants.STATUS).is(true));
        return mongoTemplate.find(mongoQuery, Lead.class);
    }

    public List<Lead> fetchInterestedLeadByInstituteIdANdUserId(Long usedId,
            List<Long> instituteIds) {
        Query mongoQuery = new Query(
                Criteria.where(Lead.Constants.INSTITUTE_ID).in(instituteIds)
                        .and(Lead.Constants.USER_ID).is(usedId).and(Lead.Constants.INTERESTED)
                        .is(true));
        return mongoTemplate.find(mongoQuery, Lead.class);
    }

    public List<Lead> fetchInterestedLeadByEntityIdAndUserId(EducationEntity educationEntity,
            Long userId, Long entityId) {
        Query mongoQuery = new Query(
                Criteria.where(Lead.Constants.ENTITY_TYPE).is(educationEntity)
                        .and(Lead.Constants.ENTITY_ID).is(entityId)
                        .and(Lead.Constants.USER_ID).is(userId).and(Lead.Constants.INTERESTED)
                        .is(true));
        return mongoTemplate.find(mongoQuery, Lead.class);
    }
}
