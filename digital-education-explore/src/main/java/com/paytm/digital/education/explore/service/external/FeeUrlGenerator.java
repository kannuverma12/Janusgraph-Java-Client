package com.paytm.digital.education.explore.service.external;

import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.thirdparty.catalog.Attributes;
import com.paytm.digital.education.explore.thirdparty.catalog.CatalogProduct;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.PAYTM_APP_REQUEST_ID;

@Service
public class FeeUrlGenerator {

    private static final Logger log = LoggerFactory.getLogger(FeeUrlGenerator.class);

    @Autowired
    private BaseRestApiService baseRestApiService;

    @Value("${catalog.admin.uri}")
    private String catalogAdminUrl;

    @Value("${fee.web.url}")
    private String feeUrlWeb;

    @Value("${fee.app.url.prefix}")
    private String feeAppUrlPrefix;

    @Value("${fee.app.url.suffix}")
    private String feeAppUrlSuffix;

    @Cacheable(value = "fee_url_generation", keyGenerator = "customKeyGenerator")
    public String generateUrl(Long pid, Client client) {
        try {
            CatalogProduct catalogProduct = getCollegeInfo(pid);
            Attributes attributes =
                    catalogProduct.getVariants().get(0).getVariants().get(0).getProducts().get(0)
                            .getAttributes();
            String url ;
            if (Client.APP.equals(client)) {
                url = createAppUrl(attributes);
            } else {
                url = createWebUrl(attributes);
            }
            log.info("Fee URL generated for pid : {}  is {}", pid, url);
            return url;
        } catch (Exception e) {
            log.error("Received unexpected response from catalog : {}",
                    e, e.getLocalizedMessage());
            return null;
        }
    }

    private String createWebUrl(Attributes attributes) {
        String state = "/" + attributes.getState();
        String city = "/" + attributes.getCity();
        String name = "/" + attributes.getSchool();
        URI uri = baseRestApiService.getURI(feeUrlWeb, null, Arrays.asList(state, city, name));
        return uri.toString();
    }


    private String createAppUrl(Attributes attributes) {
        String dynamicUrl =
                Constants.SCHOOL + attributes.getSchool() + Constants.STATE + attributes
                        .getState() + Constants.CITY + attributes.getCity() + Constants.PAY_TYPE
                        + attributes.getPayType();
        return feeAppUrlPrefix + dynamicUrl + feeAppUrlSuffix;
    }

    private CatalogProduct getCollegeInfo(Long pid) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put(Constants.GROUP1, Constants.LOCATION);
        requestParams.put(Constants.GROUP2, Constants.COURSE);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(PAYTM_APP_REQUEST_ID, MDC.get(PAYTM_APP_REQUEST_ID));

        CatalogProduct response =
                baseRestApiService.get(catalogAdminUrl, requestParams, httpHeaders, CatalogProduct.class,
                        Arrays.asList("/" + pid.toString()));
        log.info("Catalog merchant API response : {}", response.toString());
        return response;
    }

    public static class Constants {

        public static final String SCHOOL   = "$school=";
        public static final String STATE    = "$state=";
        public static final String CITY     = "$city=";
        public static final String PAY_TYPE = "$paytype=";
        public static final String GROUP1   = "group1";
        public static final String GROUP2   = "group2";
        public static final String LOCATION = "location";
        public static final String COURSE   = "course";

    }

}
