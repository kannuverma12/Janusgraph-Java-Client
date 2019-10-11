package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.constant.DBConstants;
import com.paytm.digital.education.database.entity.Counter;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Repository
@AllArgsConstructor
public class SequenceGenerator {

    private MongoOperations mongoOperations;

    @Transactional
    public long getNextSequenceId(String sequenceName) {
        Query mongoQuery = new Query(Criteria.where(DBConstants.KEY).is(sequenceName));
        Update update = new Update();
        update.inc(DBConstants.SEQUENCE, 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);
        Counter counter = mongoOperations.findAndModify(mongoQuery, update, options, Counter.class);
        return Objects.nonNull(counter) ? counter.getSequence() : 1;
    }
}
