package com.paytm.digital.education.coaching.database.repository;

import com.mongodb.client.result.UpdateResult;
import com.paytm.digital.education.coaching.database.entity.CoachingCourse;
import com.paytm.digital.education.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ACTIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.UPDATED_AT;
import static com.paytm.digital.education.mapping.ErrorEnum.ENTITY_ID_NOT_PRESENT;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_SUCH_ENTITY_EXISTS;
import static com.paytm.digital.education.utility.DateUtil.getCurrentDate;

@Repository
@AllArgsConstructor
public class CoachingCourseRepositoryOld {

    private MongoOperations   mongoOperations;
    private SequenceGenerator sequenceGenerator;

    public CoachingCourse createCourse(CoachingCourse course) {
        long courseId = sequenceGenerator.getNextSequenceId(COURSE);
        course.setCourseId(courseId);
        course.setActive(true);
        course.setCreatedAt(getCurrentDate());
        course.setUpdatedAt(course.getCreatedAt());
        CoachingCourse dbCourse = mongoOperations.save(course);
        return dbCourse;
    }

    public CoachingCourse updateCourse(CoachingCourse updateCourse) {
        if (Objects.isNull(updateCourse) || Objects.isNull(updateCourse.getCourseId())) {
            throw new BadRequestException(ENTITY_ID_NOT_PRESENT,
                    ENTITY_ID_NOT_PRESENT.getExternalMessage(), new Object[] {COURSE});
        }
        Query mongoQuery = new Query(
                Criteria.where(COURSE_ID).is(updateCourse.getCourseId()).and(ACTIVE).is(true));
        CoachingCourse dbCourse = mongoOperations.findOne(mongoQuery, CoachingCourse.class);
        if (Objects.isNull(dbCourse)) {
            throw new BadRequestException(NO_SUCH_ENTITY_EXISTS,
                    NO_SUCH_ENTITY_EXISTS.getExternalMessage(),
                    new Object[] {COURSE, COURSE, updateCourse.getCourseId()});
        }
        updateCourse.setActive(true);
        updateCourse.setId(dbCourse.getId());
        updateCourse.setUpdatedAt(getCurrentDate());
        updateCourse = mongoOperations.save(updateCourse);
        return updateCourse;
    }

    public long updateCourseStatus(long courseId, boolean activate) {
        Query mongoQuery = new Query(Criteria.where(COURSE_ID).is(courseId));
        Update courseUpdate = new Update();
        courseUpdate.set(ACTIVE, activate);
        courseUpdate.set(UPDATED_AT, getCurrentDate());
        UpdateResult updateResult =
                mongoOperations.updateFirst(mongoQuery, courseUpdate, CoachingCourse.class);
        if (updateResult.isModifiedCountAvailable()) {
            return updateResult.getModifiedCount();
        }
        return 0;
    }

    public CoachingCourse getCoachingCourseById(long courseId, Boolean active) {
        Criteria criteria = Criteria.where(COURSE_ID).is(courseId);
        if (Objects.nonNull(active)) {
            criteria.and(ACTIVE).is(active);
        }
        Query mongoQuery = new Query(criteria);
        return mongoOperations.findOne(mongoQuery, CoachingCourse.class);
    }

    public CoachingCourse upsertCoaching(CoachingCourse coachingCourse) {
        if (Objects.isNull(coachingCourse.getCourseId())) {
            long courseId = sequenceGenerator.getNextSequenceId(COURSE);
            coachingCourse.setCourseId(courseId);
            coachingCourse.setCreatedAt(getCurrentDate());
            coachingCourse.setUpdatedAt(coachingCourse.getCreatedAt());
        } else {
            coachingCourse.setUpdatedAt(getCurrentDate());
        }
        mongoOperations.save(coachingCourse);
        return coachingCourse;
    }

    public List<CoachingCourse> findAllCoachingCourses(List<Long> courseIds) {
        Query mongoQuery = new Query(
                Criteria.where(COURSE_ID).in(courseIds));
        return mongoOperations.find(mongoQuery, CoachingCourse.class);
    }
}
