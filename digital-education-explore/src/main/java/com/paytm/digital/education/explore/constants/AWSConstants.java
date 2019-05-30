package com.paytm.digital.education.explore.constants;

import com.paytm.digital.education.explore.config.AwsConfig;

public interface AWSConstants {
    String S3_BUCKET_PATH         = AwsConfig.getS3path();
    String S3_PATH                = "/{0}/{1}";
    String S3_PATH_FOR_AMBASSADOR = "/{0}/ambassador/{1}";
    String S3_PATH_FOR_ARTICLE    = "/{0}/article/{1}";
    String S3_PATH_FOR_EVENT      = "/{0}/article/{1}";
    String HTTP_AGENT             = "http.agent";
    String CHROME_HTTP_AGENT      = "Chrome";
}
