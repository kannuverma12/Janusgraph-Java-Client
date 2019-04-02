package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.BANNER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DATA_STRING;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;

import com.paytm.digital.education.explore.response.dto.common.BannerData;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class BannerDataHelper {

    private PropertyReader propertyReader;

    public List<BannerData> getBannerData(String entity) {
        Map<String, Object> bannerDataMap = propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, entity, BANNER);
        if (!CollectionUtils.isEmpty(bannerDataMap)) {
            return (List<BannerData>) bannerDataMap.get(DATA_STRING);
        }
        return null;
    }
}
