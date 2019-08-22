package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.dto.CollegePredictorDetailsDto;
import com.paytm.digital.education.explore.service.CollegePredictorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping(EDUCATION_BASE_URL)
public class CollegePredictorsController {

    private CollegePredictorService collegePredictorService;

    @RequestMapping(method = RequestMethod.GET, path = "/v1/college-predictor")
    public @ResponseBody CollegePredictorDetailsDto getCollegePredictorDetails(
            @RequestParam(value = "productId") Long productId) {

        log.info("received a request to fetch college predictor details for product id {}",
                productId);

        return collegePredictorService.getPredictor(productId);
    }
}
