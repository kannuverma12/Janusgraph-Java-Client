package com.paytm.digital.education.ingestion.service.exportdata;

import static com.paytm.digital.education.ingestion.constant.IngestionConstants.MERCHANT_STREAM_SHEET_ID;
import static com.paytm.digital.education.utility.CommonUtils.booleanToString;

import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import com.paytm.digital.education.database.repository.MerchantStreamRepository;
import com.paytm.digital.education.ingestion.request.DataExportPropertiesRequest;
import com.paytm.digital.education.ingestion.response.DataExportPropertiesResponse;
import com.paytm.digital.education.ingestion.response.ExportResponse;
import com.paytm.digital.education.ingestion.sheets.MerchantStreamForm;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerchantStreamExportService extends AbstractExportService implements ExportService {

    private static Logger log = LoggerFactory.getLogger(MerchantStreamExportService.class);

    @Autowired
    private MerchantStreamRepository merchantStreamRepository;

    @Override
    public ExportResponse export() {
        final DataExportPropertiesResponse properties = super.getProperties(
                DataExportPropertiesRequest.builder()
                        .sheetIdKey(MERCHANT_STREAM_SHEET_ID)
                        .build(), MerchantStreamForm.class);

        final List<MerchantStreamEntity> entityList = merchantStreamRepository.findAll();
        final List<MerchantStreamForm> formList = convertToFormData(entityList);

        final int recordsWritten = super.processRecords(formList, MerchantStreamForm.class,
                properties.getSheetId());

        return ExportResponse.builder()
                .countOfRecordsPresentInDb(entityList.size())
                .countOfRecordsWritten(recordsWritten)
                .build();
    }

    private List<MerchantStreamForm> convertToFormData(
            final List<MerchantStreamEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(entity -> MerchantStreamForm.builder()
                        .merchantId(entity.getMerchantId())
                        .paytmStreamId(entity.getPaytmStreamId())
                        .merchantStream(entity.getStream())
                        .active(booleanToString(entity.getActive()))
                        .build())
                .collect(Collectors.toList());
    }
}
