package com.paytm.digital.education.coaching.utils;

import com.paytm.digital.education.utility.CommonUtil;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class ImageUtils {

    public String getImageWithAbsolutePath(String image, String placeholder, String imagePrefix) {
        if (StringUtils.isEmpty(image)) {
            return CommonUtil.getAbsoluteUrl(placeholder, imagePrefix);
        } else {
            return CommonUtil.getAbsoluteUrl(image, imagePrefix);
        }
    }
}
