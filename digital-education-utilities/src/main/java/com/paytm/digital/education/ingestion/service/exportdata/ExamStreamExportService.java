package com.paytm.digital.education.ingestion.service.exportdata;

import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EXAM_STREAM_SHEET_ID;
import static com.paytm.digital.education.utility.CommonUtils.booleanToString;

import com.paytm.digital.education.database.entity.ExamStreamEntity;
import com.paytm.digital.education.database.repository.ExamStreamMappingRepository;
import com.paytm.digital.education.ingestion.request.DataExportPropertiesRequest;
import com.paytm.digital.education.ingestion.response.DataExportPropertiesResponse;
import com.paytm.digital.education.ingestion.response.ExportResponse;
import com.paytm.digital.education.ingestion.sheets.ExamStreamForm;
import com.paytm.digital.education.ingestion.sheets.MerchantStreamForm;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExamStreamExportService extends AbstractExportService implements ExportService {

    private static Logger log = LoggerFactory.getLogger(ExamStreamExportService.class);

    @Autowired
    private ExamStreamMappingRepository examStreamMappingRepository;

    @Override public ExportResponse export() {
        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(EXAM_STREAM_SHEET_ID)
                        .build(), MerchantStreamForm.class);

        final List<ExamStreamEntity> entityList = examStreamMappingRepository.findAll();
        final List<ExamStreamForm> formList = convertToFormData(entityList);

        final int recordsWritten = super.processRecords(formList, ExamStreamForm.class,
                properties.getSheetId());

        return ExportResponse.builder()
                .countOfRecordsPresentInDb(entityList.size())
                .countOfRecordsWritten(recordsWritten)
                .build();
    }

    private List<ExamStreamForm> convertToFormData(List<ExamStreamEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(entity -> ExamStreamForm.builder().examFullName(entity.getExamFullName())
                        .examShortName(entity.getExamShortName()).examId(entity.getExamId())
                        .globalPriority(entity.getPriority())
                        .domains(getPaytmDomains(entity.getPaytmStreamIds()))
                        .statusActive(booleanToString(entity.getIsEnabled()))
                        .build()).collect(
                        Collectors.toList());
    }

    private String getPaytmDomains(List<Long> paytmStreamIds) {
        return Optional.ofNullable(paytmStreamIds).orElse(Collections.emptyList()).stream()
                .map(streamId -> streamId.toString()).collect(Collectors.joining(","));
    }
}
