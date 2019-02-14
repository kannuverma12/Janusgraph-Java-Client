package com.paytm.digital.education.elasticsearch.client;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchClient {

    @Value("${spring.search.education.hostname}")
    private String esSearchClusterHostName;

    @Value("${spring.search.education.hostname}")
    private String esSearchClusterScheme;

    @Value("${spring.search.education.hostname}")
    private int    esSearchClusterPort;

    @Bean(ESConstants.EDUCATION_ES_CLIENT)
    public RestHighLevelClient esClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(esSearchClusterHostName, esSearchClusterPort,
                                esSearchClusterScheme)));
    }

}
