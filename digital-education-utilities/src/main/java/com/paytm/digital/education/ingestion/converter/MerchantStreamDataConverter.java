package com.paytm.digital.education.ingestion.converter;


import static com.paytm.digital.education.utility.CommonUtils.stringToBoolean;

import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import com.paytm.digital.education.ingestion.sheets.MerchantStreamForm;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MerchantStreamDataConverter {

    private static Logger log = LoggerFactory.getLogger(MerchantStreamDataConverter.class);

    public MerchantStreamEntity formRequestToEntity(MerchantStreamForm formRequest,
            MerchantStreamEntity streamEntity) {
        streamEntity.setMerchantId(formRequest.getMerchantId());
        streamEntity.setPaytmStreamId(formRequest.getPaytmStreamId());
        streamEntity.setStream(formRequest.getMerchantStream());
        streamEntity.setActive(stringToBoolean(formRequest.getActive()));
        streamEntity.setUpdatedAt(new Date());
        return streamEntity;
    }


}
