package com.paytm.digital.education.coaching.database.repository;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ACTIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.UPDATED_AT;
import static com.paytm.digital.education.mapping.ErrorEnum.ENTITY_ID_NOT_PRESENT;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_SUCH_ENTITY_EXISTS;
import static com.paytm.digital.education.utility.DateUtil.getCurrentDate;

import com.mongodb.client.result.UpdateResult;
import com.paytm.digital.education.coaching.database.entity.CoachingExam;
import com.paytm.digital.education.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@AllArgsConstructor
@Deprecated
// TODO : Need to remove this class
public class CoachingExamRepository {

    private MongoOperations   mongoOperations;
    private SequenceGenerator sequenceGenerator;

    public CoachingExam createCoachingExam(CoachingExam coachingExam) {
        long examId = sequenceGenerator.getNextSequenceId(EXAM);
        coachingExam.setExamId(examId);
        coachingExam.setActive(true);
        coachingExam.setCreatedAt(getCurrentDate());
        coachingExam.setUpdatedAt(coachingExam.getCreatedAt());
        CoachingExam dbCoaching = mongoOperations.save(coachingExam);
        return dbCoaching;
    }

    public CoachingExam updateCoachingExam(CoachingExam updateCoachingExam) {
        if (Objects.isNull(updateCoachingExam) || Objects.isNull(updateCoachingExam.getExamId())) {
            throw new BadRequestException(ENTITY_ID_NOT_PRESENT,
                    ENTITY_ID_NOT_PRESENT.getExternalMessage(), new Object[] {EXAM});
        }
        Query mongoQuery = new Query(
                Criteria.where(EXAM_ID).is(updateCoachingExam.getExamId()).and(ACTIVE).is(true));
        CoachingExam dbCoachingExam = mongoOperations.findOne(mongoQuery, CoachingExam.class);
        if (Objects.isNull(dbCoachingExam)) {
            throw new BadRequestException(NO_SUCH_ENTITY_EXISTS,
                    NO_SUCH_ENTITY_EXISTS.getExternalMessage(),
                    new Object[] {EXAM, EXAM, updateCoachingExam.getExamId()});
        }
        updateCoachingExam.setId(dbCoachingExam.getId());
        updateCoachingExam.setActive(true);
        updateCoachingExam.setUpdatedAt(getCurrentDate());
        mongoOperations.save(updateCoachingExam);
        return updateCoachingExam;
    }

    public long updateCoachingExamStatus(long examId, boolean active) {
        Query mongoQuery =
                new Query(Criteria.where(EXAM_ID).is(examId));
        Update update = new Update();
        update.set(ACTIVE, active);
        update.set(UPDATED_AT, getCurrentDate());
        UpdateResult examUpdate =
                mongoOperations.updateFirst(mongoQuery, update, CoachingExam.class);
        if (examUpdate.isModifiedCountAvailable()) {
            return examUpdate.getModifiedCount();
        }
        return 0;
    }

    public CoachingExam findCoachingExamById(long examId, Boolean active) {
        Criteria criteria = Criteria.where(EXAM_ID).is(examId);
        if (Objects.nonNull(active)) {
            criteria.and(ACTIVE).is(active);
        }
        Query mongoQuery = new Query(criteria);
        return  mongoOperations.findOne(mongoQuery, CoachingExam.class);
    }

    public List<CoachingExam> findAllCoachingExam(List<Long> examIds) {
        Query mongoQuery = new Query(
                Criteria.where(EXAM_ID).in(examIds));
        return mongoOperations.find(mongoQuery, CoachingExam.class);
    }

    public CoachingExam upsertCoachingExam(CoachingExam coachingExam) {
        if (Objects.isNull(coachingExam.getExamId())) {
            long examId = sequenceGenerator.getNextSequenceId(EXAM);
            coachingExam.setExamId(examId);
            coachingExam.setCreatedAt(getCurrentDate());
            coachingExam.setUpdatedAt(coachingExam.getCreatedAt());
        } else {
            coachingExam.setUpdatedAt(getCurrentDate());
        }
        mongoOperations.save(coachingExam);
        return coachingExam;
    }

}
