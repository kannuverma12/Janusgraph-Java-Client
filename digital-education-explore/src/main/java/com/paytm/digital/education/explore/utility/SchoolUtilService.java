package com.paytm.digital.education.explore.utility;

import com.paytm.digital.education.config.SchoolConfig;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Data
public class SchoolUtilService {

    private final SchoolConfig schoolConfig;

    public String buildLogoFullPathFromRelativePath(String relativeLogoPath) {
        return StringUtils.isBlank(relativeLogoPath)
                ? schoolConfig.getSchoolPlaceholderLogoURL()
                : CommonUtils.addCDNPrefixAndEncode(relativeLogoPath);
    }
}
