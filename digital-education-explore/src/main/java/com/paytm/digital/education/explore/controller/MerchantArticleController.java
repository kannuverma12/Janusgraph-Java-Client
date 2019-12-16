package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.request.dto.articles.MerchantArticleRequest;
import com.paytm.digital.education.explore.response.dto.articles.MerchantArticleResponse;
import com.paytm.digital.education.explore.service.impl.MerchantArticleServiceImpl;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@AllArgsConstructor
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@Validated
@Api(value = "Merchant Articles API", description = "Merchant Articles API")
public class MerchantArticleController {

    private static Logger log = LoggerFactory.getLogger(MerchantArticleController.class);

    private MerchantArticleServiceImpl merchantArticleServiceImpl;

    @RequestMapping(method = RequestMethod.POST, path = "/auth/v1/merchant/article")
    @ApiOperation(value = "Save Merchant Articles")
    public ResponseEntity<MerchantArticleResponse> saveArticle(
            @Valid @RequestBody @NotNull MerchantArticleRequest merchantArticleRequest) {
        log.info("Merchant Article Request : {}", JsonUtils.toJson(merchantArticleRequest));
        MerchantArticleResponse merchantArticleResponse =
                merchantArticleServiceImpl.saveArticle(merchantArticleRequest);
        return new ResponseEntity<>(merchantArticleResponse, merchantArticleResponse.getStatus());
    }

}
