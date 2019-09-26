package com.paytm.digital.education.coaching.database.repository;

import com.paytm.digital.education.coaching.database.entity.CoachingCenter;
import lombok.AllArgsConstructor;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ACTIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_ID;
import static com.paytm.digital.education.utility.DateUtil.getCurrentDate;


@Repository
@AllArgsConstructor
public class CoachingCenterRespository {
    private MongoOperations   mongoOperations;
    private SequenceGenerator sequenceGenerator;

    public CoachingCenter upsertCoachingCenter(CoachingCenter coachingCenter) {
        if (Objects.isNull(coachingCenter.getCenterId())) {
            long centerId = sequenceGenerator.getNextSequenceId(COACHING_CENTER);
            coachingCenter.setCenterId(centerId);
            coachingCenter.setCreatedAt(getCurrentDate());
            coachingCenter.setUpdatedAt(coachingCenter.getCreatedAt());
        } else {
            coachingCenter.setUpdatedAt(getCurrentDate());
        }
        mongoOperations.save(coachingCenter);
        return coachingCenter;
    }

    public Long getNextSequenceId(String type) {
        return sequenceGenerator.getNextSequenceId(type);
    }

    public CoachingCenter findCoachingCenterById(long centerId, Boolean enabled) {
        Criteria criteria = Criteria.where(COACHING_CENTER_ID).is(centerId);
        if (Objects.nonNull(enabled)) {
            criteria.and(ACTIVE).is(enabled);
        }
        Query mongoQuery = new Query(criteria);
        CoachingCenter coachingCenter =
                mongoOperations.findOne(mongoQuery, CoachingCenter.class);
        return coachingCenter;
    }
}
