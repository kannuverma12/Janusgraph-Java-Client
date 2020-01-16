package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.database.entity.CustomerAction;
import com.paytm.digital.education.database.entity.InstituteByProduct;
import com.paytm.digital.education.enums.Action;
import com.paytm.digital.education.enums.Product;
import com.paytm.digital.education.explore.dto.InstituteListResponseDto;

import javax.servlet.http.HttpServletResponse;

public interface InstituteByProductService {
    InstituteListResponseDto getInstitutesByProduct(Long userId, Product product, String email);

    CustomerAction postCustomerActionForProduct(
            Long userId, Product product, String email, Long exploreInstituteId, Action action);

    CustomerAction deleteCustomerActionForProduct(
            Long userId, Product product, String email, Long exploreInstituteId, Action action);

    void getReport(HttpServletResponse response);

    void sendReport();

    InstituteByProduct saveInstituteByProduct(InstituteByProduct instituteByProduct);
}
