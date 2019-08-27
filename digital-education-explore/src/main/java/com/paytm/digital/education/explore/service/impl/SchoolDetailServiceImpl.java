package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.School;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;

@Slf4j
@Service
@AllArgsConstructor
public class SchoolDetailServiceImpl {

    private CommonMongoRepository commonMongoRepository;

    public List<School> getSchools(List<Long> entityIds, List<String> groupFields) {
        if (CollectionUtils.isEmpty(groupFields)) {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }

        Set<Long> searchIds = new HashSet<>(entityIds);
        List<School> schools =
                commonMongoRepository
                        .getEntityFieldsByValuesIn(SCHOOL_ID, new ArrayList<>(searchIds),
                                School.class,
                                groupFields);

        return schools;
    }
}
