package com.paytm.digital.education.coaching.http;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class HttpRequestDetails {

    private String                    url;
    private HttpMethod                requestMethod;
    private Object                    body;
    private HttpHeaders               headers;
    private Map<String, String>       queryParams;
    private Map<String, List<String>> multiQueryParams;
    private long                      expectedLatency;
    private String requestApiName;
}
