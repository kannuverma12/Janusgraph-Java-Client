package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.request.dto.EntityData;
import com.paytm.digital.education.explore.response.dto.dataimport.CatalogDataIngestionError;

import java.util.List;

public interface ImportFromCatalogService {

    List<CatalogDataIngestionError> ingestDataEntityWise(List<EntityData> entityDataList);

}
