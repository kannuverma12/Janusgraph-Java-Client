package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.database.entity.CustomerAction;
import com.paytm.digital.education.database.entity.InstituteByProduct;
import com.paytm.digital.education.enums.Product;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.dto.InstituteListResponseDto;
import com.paytm.digital.education.explore.request.dto.institute.by.product.ActionRequest;
import com.paytm.digital.education.explore.service.InstituteByProductService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;
import static com.paytm.digital.education.mapping.ErrorEnum.EITHER_USER_PROFILE_OR_REQUEST_SHOULD_HAVE_EMAIL;

@RequestMapping(EDUCATION_BASE_URL)
@RequiredArgsConstructor
@RestController
@Validated
public class InstituteByProductController {

    private final InstituteByProductService instituteByProductService;

    @GetMapping("/auth/v1/institutes-by-product")
    public InstituteListResponseDto getInstitutesByProduct(
            @RequestHeader("x-user-id") @NotNull @Min(1) Long userId,
            @RequestHeader(value = "x-user-email", required = false) String email,
            @RequestParam("product") Product product) {
        return instituteByProductService.getInstitutesByProduct(userId, product, email);
    }

    @PostMapping("/auth/admin/v1/institutes-by-product")
    public InstituteByProduct postInstitutesByProduct(
            @RequestBody @Valid InstituteByProduct instituteByProduct) {
        return instituteByProductService.saveInstituteByProduct(instituteByProduct);
    }

    @PostMapping("/auth/v1/institutes-by-product")
    public CustomerAction postAction(
            @RequestHeader("x-user-id") @NotNull @Min(1) Long userId,
            @RequestHeader(value = "x-user-email", required = false) String email,
            @Valid @RequestBody ActionRequest actionRequest) {
        if (StringUtils.isBlank(email) && StringUtils.isBlank(actionRequest.getEmail())) {
            throw new BadRequestException(EITHER_USER_PROFILE_OR_REQUEST_SHOULD_HAVE_EMAIL);
        }
        return instituteByProductService.postCustomerActionForProduct(
                userId,
                actionRequest.getProduct(),
                StringUtils.isBlank(email) ? actionRequest.getEmail() : email,
                actionRequest.getExploreInstituteId(),
                actionRequest.getAction()
        );
    }


    @DeleteMapping("/auth/v1/institutes-by-product")
    public CustomerAction deleteAction(
            @RequestHeader("x-user-id") @NotNull @Min(1) Long userId,
            @RequestHeader(value = "x-user-email", required = false) String email,
            @Valid @RequestBody ActionRequest actionRequest) {
        if (StringUtils.isBlank(email) && StringUtils.isBlank(actionRequest.getEmail())) {
            throw new BadRequestException(EITHER_USER_PROFILE_OR_REQUEST_SHOULD_HAVE_EMAIL);
        }
        return instituteByProductService.deleteCustomerActionForProduct(
                userId,
                actionRequest.getProduct(),
                StringUtils.isBlank(email) ? actionRequest.getEmail() : email,
                actionRequest.getExploreInstituteId(),
                actionRequest.getAction()
        );
    }

    @GetMapping("/auth/v1/institutes-by-product/report")
    public void getReport(HttpServletResponse response) {
        instituteByProductService.getReport(response);
    }
}
