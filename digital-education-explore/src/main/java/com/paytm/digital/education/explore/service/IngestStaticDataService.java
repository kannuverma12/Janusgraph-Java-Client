package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.request.dto.EntityData;
import com.paytm.digital.education.explore.response.dto.dataimport.StaticDataIngestionResponse;

import java.util.List;

public interface IngestStaticDataService {

    List<StaticDataIngestionResponse> ingestDataEntityWise(List<EntityData> entityDataList);

}
