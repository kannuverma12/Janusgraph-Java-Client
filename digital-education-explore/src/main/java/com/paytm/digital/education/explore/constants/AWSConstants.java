package com.paytm.digital.education.explore.constants;

import com.paytm.digital.education.config.AwsConfig;

public interface AWSConstants {
    String S3_RELATIVE_PATH_PREFIX         = AwsConfig.getRelativePathPrefix();
    String S3_RELATIVE_PATH_FOR_AMBASSADOR = S3_RELATIVE_PATH_PREFIX + "/{0}/ambassadors";
    String S3_RELATIVE_PATH_FOR_ARTICLE    = S3_RELATIVE_PATH_PREFIX + "/{0}/articles";
    String S3_RELATIVE_PATH_FOR_EVENT      = S3_RELATIVE_PATH_PREFIX + "/{0}/events";
}
