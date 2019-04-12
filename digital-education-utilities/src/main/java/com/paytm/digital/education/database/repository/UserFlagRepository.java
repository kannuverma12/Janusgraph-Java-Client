package com.paytm.digital.education.database.repository;

import static com.paytm.digital.education.constant.DBConstants.USER_ID;

import com.paytm.digital.education.database.entity.UserFlags;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

@AllArgsConstructor
@Repository
public class UserFlagRepository {

    private MongoOperations mongoOperation;

    public UserFlags incrementCounter(long userId, String counterName, int counterValue) {
        Query query = new Query(Criteria.where(USER_ID).is(userId));
        Update update = new Update().inc(counterName, counterValue);
        return mongoOperation.findAndModify(query, update, UserFlags.class);
    }

    public UserFlags decrementCounterIfPositive(long userId, String counterName, int counterValue) {
        Query query = new Query(Criteria.where(USER_ID).is(userId).and(counterName).gt(0));
        Update update = new Update().inc(counterName, -counterValue);
        return mongoOperation.findAndModify(query, update, UserFlags.class);
    }

    public UserFlags updateCounter(long userId, String counterName, int counterValue) {
        Query query = new Query(Criteria.where(USER_ID).is(userId));
        Update update = new Update().set(counterName, counterValue);
        return mongoOperation.findAndModify(query, update, UserFlags.class);
    }

    public UserFlags getUserFlag(long userId) {
        Query mongoQuery = new Query(Criteria.where(USER_ID).is(userId));
        return mongoOperation.findOne(mongoQuery, UserFlags.class);
    }

    public void saveOrUpdate(UserFlags userFlags) {
        userFlags.setUpdatedAt(new Date());
        mongoOperation.save(userFlags);
    }
}
