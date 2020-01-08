package com.paytm.digital.education.database.indexes.partial;

import com.paytm.digital.education.database.entity.CustomerAction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.DefaultIndexOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.paytm.digital.education.constant.InstituteByProductConstants.ACTION;
import static com.paytm.digital.education.constant.InstituteByProductConstants.CUSTOMER_ACTION;
import static com.paytm.digital.education.constant.InstituteByProductConstants.EMAIL;
import static com.paytm.digital.education.constant.InstituteByProductConstants.INSTITUTE_BY_PRODUCT_ENTRY_ID;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Component
@RequiredArgsConstructor
public class CustomerActionIndex {

    private final MongoOperations mongoOperations;

    private static final String UNIQUE_CUSTOMER_ACTION = "unique_customer_action";

    @PostConstruct
    public void createIndex() {
        Index uniqueCustomerActionForInstituteIndex = new Index()
                .background()
                .unique()
                .named(UNIQUE_CUSTOMER_ACTION)
                .on(INSTITUTE_BY_PRODUCT_ENTRY_ID, ASC)
                .on(EMAIL, ASC)
                .on(ACTION, ASC);

        DefaultIndexOperations indexOperations = new DefaultIndexOperations(
                mongoOperations, CUSTOMER_ACTION, CustomerAction.class);

        indexOperations.ensureIndex(uniqueCustomerActionForInstituteIndex);
    }
}
