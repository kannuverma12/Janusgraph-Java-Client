package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.constant.ExploreConstants.BANNER;
import static com.paytm.digital.education.constant.ExploreConstants.BANNER_APP;
import static com.paytm.digital.education.constant.ExploreConstants.DATA_STRING;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;

import com.paytm.digital.education.enums.Client;
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

    public List<BannerData> getBannerData(String entity, Client client) {
        String banner = BANNER;
        if (Client.APP.equals(client)) {
            banner = BANNER_APP;
        }
        Map<String, Object> bannerDataMap =
                propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, entity, banner);
        if (!CollectionUtils.isEmpty(bannerDataMap)) {
            return (List<BannerData>) bannerDataMap.get(DATA_STRING);
        }
        return null;
    }
}
