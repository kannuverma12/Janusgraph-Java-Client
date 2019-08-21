package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.collegepredictor.CollegePredictor;
import com.paytm.digital.education.explore.database.repository.collegepredictor.CollegePredictorRepository;
import com.paytm.digital.education.explore.dto.CollegePredictorDetailsDto;
import com.paytm.digital.education.explore.service.CollegePredictorService;
import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CollegePredictorsServiceImpl implements CollegePredictorService {

    @Autowired
    private CollegePredictorRepository predictorListRepository;

    @Override
    public CollegePredictorDetailsDto getPredictor(Long productId) {
        Optional<CollegePredictor> predictor = predictorListRepository.findCollegePredictorByMerchantSku(productId);

        if (! predictor.isPresent()) {
            throw new BadRequestException(ErrorEnum.NO_ENTITY_FOUND, new String[]{"College Predictor", "product id", productId.toString()});
        }

        return createCollegePredictorDto(predictor.get());
    }

    private CollegePredictorDetailsDto createCollegePredictorDto(CollegePredictor predictor) {

        CollegePredictorDetailsDto predictorDetailsDto =
                CollegePredictorDetailsDto.builder()
                .id(predictor.getMerchantSku())
                .currency(predictor.getCurrency())
                .description(predictor.getDescription())
                .longDescription(predictor.getLongDescription())
                .image(predictor.getImage())
                .offeredPrice(predictor.getOfferedPrice())
                .paytmPrice(predictor.getPaytmPrice())
                .price(predictor.getPrice())
                .status(predictor.getStatus())
                .title(predictor.getTitle())
                .type(predictor.getType())
                .build();

        return predictorDetailsDto;
    }
}
