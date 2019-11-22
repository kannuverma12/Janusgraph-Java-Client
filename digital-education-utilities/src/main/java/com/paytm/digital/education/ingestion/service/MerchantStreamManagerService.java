package com.paytm.digital.education.ingestion.service;

import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_PAYTM_STREAM;
import static com.paytm.digital.education.mapping.ErrorEnum.PAYTM_STREAM_DISABLED;

import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.MerchantStreamRepository;
import com.paytm.digital.education.database.repository.StreamEntityRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.ingestion.converter.MerchantStreamDataConverter;
import com.paytm.digital.education.ingestion.sheets.MerchantStreamForm;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantStreamManagerService {

    private static Logger log = LoggerFactory.getLogger(MerchantStreamManagerService.class);

    @Autowired
    private MerchantStreamRepository merchantStreamRepository;

    @Autowired
    private MerchantStreamDataConverter streamDataConverter;

    @Autowired
    private StreamEntityRepository streamEntityRepository;

    public MerchantStreamEntity createOrUpdateMerchantStream(MerchantStreamForm request) {
        checkIfValidPaytmStream(request);
        MerchantStreamEntity merchantStreamEntity = Optional.ofNullable(merchantStreamRepository
                .findByMerchantIdAndStream(request.getMerchantId().toUpperCase(),
                        request.getMerchantStream().trim())).orElse(new MerchantStreamEntity());
        streamDataConverter.formRequestToEntity(request, merchantStreamEntity);
        try {
            return merchantStreamRepository.save(merchantStreamEntity);
        } catch (DataIntegrityViolationException ex) {
            log.error(
                    "Error in upserting data in mongo db : merchantId : {}, merchantStream : {}",
                    ex, request.getMerchantId(), request.getMerchantStream());
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    private void checkIfValidPaytmStream(MerchantStreamForm request) {
        Optional<StreamEntity> streamEntityOptional = Optional.ofNullable(
                streamEntityRepository.findByStreamId(request.getPaytmStreamId()));
        if (!streamEntityOptional.isPresent()) {
            throw new BadRequestException(INVALID_PAYTM_STREAM,
                    INVALID_PAYTM_STREAM.getExternalMessage(),
                    new Object[] {request.getPaytmStreamId()});
        } else if (!streamEntityOptional.get().getIsEnabled()) {
            throw new BadRequestException(PAYTM_STREAM_DISABLED,
                    PAYTM_STREAM_DISABLED.getExternalMessage(),
                    new Object[] {request.getPaytmStreamId()});
        }
    }
}
