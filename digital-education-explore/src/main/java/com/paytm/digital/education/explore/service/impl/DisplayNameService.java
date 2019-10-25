package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_COLLECTION_ID_KEY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_IDS;
import static com.sun.org.apache.xml.internal.utils.LocaleUtility.EMPTY_STRING;
import static java.util.Collections.emptyList;

@RequiredArgsConstructor
@Service
public class DisplayNameService {
    private final CommonMongoRepository commonMongoRepository;

    public String getDisplayName(Map<String, Map<String, Object>> propertyMap, String docKey,
            String bucketKey) {
        if (STREAM_IDS.equals(docKey) && !STREAM_IDS.equals(bucketKey)) {
            return lookupStreamNameFromId(bucketKey);
        }
        return CommonUtil.getDisplayName(propertyMap,
                docKey,
                bucketKey);
    }

    private String lookupStreamNameFromId(String id) {
        try {
            long streamId = Long.valueOf(id);
            StreamEntity streamEntity =
                    commonMongoRepository.getEntityByFields(
                            STREAM_COLLECTION_ID_KEY, streamId, StreamEntity.class, emptyList());
            return streamEntity.getName();
        } catch (Exception e) {
            return EMPTY_STRING;
        }
    }
}
