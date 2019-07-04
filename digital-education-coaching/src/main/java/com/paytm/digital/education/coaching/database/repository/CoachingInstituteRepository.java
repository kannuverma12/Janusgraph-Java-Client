package com.paytm.digital.education.coaching.database.repository;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ACTIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.UPDATED_AT;
import static com.paytm.digital.education.mapping.ErrorEnum.ENTITY_ID_NOT_PRESENT;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_SUCH_ENTITY_EXISTS;
import static com.paytm.digital.education.utility.DateUtil.getCurrentDate;

import com.mongodb.client.result.UpdateResult;
import com.paytm.digital.education.coaching.database.entity.CoachingCenter;
import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@AllArgsConstructor
public class CoachingInstituteRepository {

    private MongoOperations   mongoOperations;
    private SequenceGenerator sequenceGenerator;

    public CoachingInstitute createCoaching(CoachingInstitute coachingInstitute) {
        long instituteId = sequenceGenerator.getNextSequenceId(INSTITUTE);
        coachingInstitute.setInstituteId(instituteId);
        coachingInstitute.setCreatedAt(getCurrentDate());
        coachingInstitute.setUpdatedAt(coachingInstitute.getCreatedAt());
        CoachingInstitute dbInstitute = mongoOperations.save(coachingInstitute);
        /*if (!CollectionUtils.isEmpty(coachingInstitute.getCoachingCenters())) {
            List<CoachingCenter> coachingCenters =
                    createOrUpdateCoachingCenters(coachingInstitute.getCoachingCenters(),
                            coachingInstitute.getInstituteId());
            dbInstitute.setCoachingCenters(coachingCenters);
        }*/
        return dbInstitute;
    }

    public CoachingInstitute updateCoaching(CoachingInstitute coachingInstitute) {
        if (Objects.isNull(coachingInstitute.getInstituteId())) {
            throw new BadRequestException(ENTITY_ID_NOT_PRESENT,
                    ENTITY_ID_NOT_PRESENT.getExternalMessage(), new Object[] {INSTITUTE});
        }
        Query mongoQuery =
                new Query(Criteria.where(INSTITUTE_ID).is(coachingInstitute.getInstituteId()));
        CoachingInstitute dbCoachingInstitute =
                mongoOperations.findOne(mongoQuery, CoachingInstitute.class);
        if (Objects.isNull(dbCoachingInstitute)) {
            throw new BadRequestException(NO_SUCH_ENTITY_EXISTS,
                    NO_SUCH_ENTITY_EXISTS.getExternalMessage(),
                    new Object[] {INSTITUTE, INSTITUTE, coachingInstitute.getInstituteId()});
        }
        coachingInstitute.setId(dbCoachingInstitute.getId());
        coachingInstitute.setActive(true);
        coachingInstitute.setUpdatedAt(getCurrentDate());
        mongoOperations.save(coachingInstitute);
        return coachingInstitute;
    }

    public long updateCoachingStatus(long instituteId, boolean activate) {
        Query mongoQuery =
                new Query(Criteria.where(INSTITUTE_ID).is(instituteId));
        Update update = new Update();
        update.set(ACTIVE, activate);
        update.set(UPDATED_AT, getCurrentDate());

        UpdateResult coachingUpdate =
                mongoOperations.updateFirst(mongoQuery, update, CoachingInstitute.class);
        UpdateResult coachingCenterUpdate =
                mongoOperations.updateMulti(mongoQuery, update, CoachingCenter.class);
        if (coachingUpdate.isModifiedCountAvailable()) {
            return coachingUpdate.getModifiedCount();
        }
        return 0;
    }

    public CoachingInstitute findCoachingById(long instituteId, Boolean enabled) {
        Criteria criteria = Criteria.where(INSTITUTE_ID).is(instituteId);
        if (Objects.nonNull(enabled)) {
            criteria.and(ACTIVE).is(enabled);
        }
        Query mongoQuery = new Query(criteria);
        CoachingInstitute dbCoachingInstitute =
                mongoOperations.findOne(mongoQuery, CoachingInstitute.class);
        /*List<CoachingCenter> coachingCenters = findCoachingCentersByInstituteId(instituteId);
        if (!CollectionUtils.isEmpty(coachingCenters)) {
            dbCoachingInstitute.setCoachingCenters(coachingCenters);
        }*/
        return dbCoachingInstitute;
    }

    public long updateCoachingCenterStatus(long instituteId, long centerId, boolean activate) {
        Query mongoQuery = new Query(Criteria.where(INSTITUTE_ID).is(instituteId).and(COACHING_CENTER_ID).is(centerId));
        Update updateRequest = new Update();
        updateRequest.set(ACTIVE, activate);
        updateRequest.set(UPDATED_AT, getCurrentDate());
        UpdateResult updateResult = mongoOperations.updateFirst(mongoQuery, updateRequest, CoachingCenter.class);
        if (updateResult.isModifiedCountAvailable()) {
            return updateResult.getModifiedCount();
        }
        return 0;
    }

    private List<CoachingCenter> findCoachingCentersByInstituteId(long instituteId) {
        Query mongoQuery = new Query(Criteria.where(INSTITUTE_ID).is(instituteId).and(ACTIVE).is(true));
        return mongoOperations.find(mongoQuery, CoachingCenter.class);
    }

    private List<CoachingCenter> createOrUpdateCoachingCenters(
            List<CoachingCenter> coachingCenters, long instituteId) {
        List<CoachingCenter> resultList = new ArrayList<>();
        Date currentDate = getCurrentDate();
        for (CoachingCenter coachingCenter : coachingCenters) {
            if (Objects.nonNull(coachingCenter.getCenterId())) {
                Query mongoQuery = new Query(
                        Criteria.where(COACHING_CENTER_ID).is(coachingCenter.getCenterId()));
                CoachingCenter dbCoachingCenter =
                        mongoOperations.findOne(mongoQuery, CoachingCenter.class);
                coachingCenter.setId(dbCoachingCenter.getId());
                coachingCenter.setCenterId(dbCoachingCenter.getCenterId());
            } else {
                long coachingCenterId = sequenceGenerator.getNextSequenceId(COACHING_CENTER);
                coachingCenter.setCenterId(coachingCenterId);
                coachingCenter.setCreatedAt(currentDate);
            }
            coachingCenter.setUpdatedAt(currentDate);
            coachingCenter.setInstituteId(instituteId);
            mongoOperations.save(coachingCenter);
            resultList.add(coachingCenter);
        }
        return resultList;
    }

    public List<CoachingInstitute> findAllCoachingInstitutes(List<Long> instituteIds) {
        Query mongoQuery = new Query(
                Criteria.where(INSTITUTE_ID).in(instituteIds));
        return mongoOperations.find(mongoQuery, CoachingInstitute.class);
    }

    public List<CoachingCenter> findAllCoachingCenter(List<Long> centersIds) {
        Query mongoQuery = new Query(
                Criteria.where(COACHING_CENTER_ID).in(centersIds));
        return mongoOperations.find(mongoQuery, CoachingCenter.class);
    }

    public CoachingInstitute upsertCoaching(CoachingInstitute coachingInstitute) {
        if (Objects.isNull(coachingInstitute.getInstituteId())) {
            long instituteId = sequenceGenerator.getNextSequenceId(INSTITUTE);
            coachingInstitute.setInstituteId(instituteId);
            coachingInstitute.setCreatedAt(getCurrentDate());
            coachingInstitute.setUpdatedAt(coachingInstitute.getCreatedAt());
        } else {
            coachingInstitute.setUpdatedAt(getCurrentDate());
        }
        mongoOperations.save(coachingInstitute);
        return coachingInstitute;
    }
}
