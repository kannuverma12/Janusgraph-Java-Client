package com.paytm.digital.education.form.controller;


import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.form.config.AuthorizationService;
import com.paytm.digital.education.form.model.DownloadOrder;
import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.MerchantFormData;
import com.paytm.digital.education.form.model.ResponseData;
import com.paytm.digital.education.form.model.SellerPanelResponse;
import com.paytm.digital.education.form.service.SellerPanelService;
import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/formfbl")
public class SellerPanelController {

    private SellerPanelService sellerPanelService;
    private AuthorizationService authService;


    @GetMapping("/v1/orders")
    @CrossOrigin(origins = {"http://localhost:8080", "http://merchant-dev.paytm.com", "http://fe.paytm.com",
            "http://staging.paytm.com", "http://beta.paytm.com", "http://paytm.com", "https://seller.paytm.com",
            "https://seller-dev.paytm.com"}, allowCredentials = "true")
    public ResponseEntity<Object> getInfo(
            @RequestParam(name = "order_ids", required = false) List<Long> orderIds,

            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,

            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,

            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ) {

        if (authService.getMerchantId() == null) {
            throw new EducationException(ErrorEnum.USER_IS_NOT_MERCHANT, "Incorrect Request");
        }

        String merchantId = authService.getMerchantId().toString();

        validateRequestOrderStartDate(orderIds, startDate);

        if (endDate == null) {
            endDate = new Date();
        }

        try {

            if (orderIds != null && orderIds.size() != 0) {
                List<SellerPanelResponse> sellerPanelResponses =
                        getSellerPanelResponse(sellerPanelService.getInfoOnOrderIds(
                                merchantId, orderIds, startDate, endDate));

                return new ResponseEntity<>(new ResponseData<SellerPanelResponse>(sellerPanelResponses),
                        HttpStatus.OK);

            } else if (startDate != null) {
                ResponseData<FormData> response = sellerPanelService
                        .getInfoOnDate(merchantId, startDate, endDate, offset, limit);

                ResponseData<SellerPanelResponse> responseData = new ResponseData<SellerPanelResponse>(
                        getSellerPanelResponse(response.getData()
                                .stream().filter(formData -> {
                                    return formData != null
                                            && formData.getFormFulfilment() != null
                                            && formData.getFormFulfilment().getOrderId() != null;
                                }).collect(Collectors.toList()))
                );

                responseData.setCount(response.getCount());
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(
                        "{\"statusCode:\": 400 , \"error:\" \"Enter either orderIds or startDate.\"}",
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {

            return new ResponseEntity<>(
                    "{\"statusCode:\": 500 , \"error\" : \"Some error occurred , please try again later.\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/v1/orders/download")
    @CrossOrigin(origins = {"http://localhost:8080", "http://merchant-dev.paytm.com", "http://fe.paytm.com",
            "http://staging.paytm.com", "http://beta.paytm.com", "http://paytm.com", "https://seller.paytm.com",
            "https://seller-dev.paytm.com"}, allowCredentials = "true")
    public void downloadOrders(
            @RequestParam(name = "order_ids") List<Long> orderIds,

            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,

            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,

            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,

            HttpServletResponse response) throws Exception {

        if (authService.getMerchantId() == null) {
            throw new EducationException(ErrorEnum.USER_IS_NOT_MERCHANT, "Incorrect Request");
        }

        validateRequestOrderStartDate(orderIds, startDate);

        PrintWriter writer = response.getWriter();
        response.setHeader("Content-disposition", "attachment; filename=response.csv");
        response.setContentType("text/csv");

        StatefulBeanToCsv<MerchantFormData> sbc = new StatefulBeanToCsvBuilder<MerchantFormData>(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .build();

        List<MerchantFormData> formDataList = sellerPanelService
                .getInfoOnOrderIds(authService.getMerchantId().toString(), orderIds, startDate, endDate)
                .stream()
                .map(MerchantFormData::new)
                .collect(Collectors.toList());

        sbc.write(formDataList);
        writer.close();
    }

    @GetMapping(value = "/v1/orders/bulk-download", produces = "application/json")
    @CrossOrigin(origins = {"http://localhost:8080", "http://merchant-dev.paytm.com", "http://fe.paytm.com",
            "http://staging.paytm.com", "http://beta.paytm.com", "http://paytm.com", "https://seller.paytm.com",
            "https://seller-dev.paytm.com"}, allowCredentials = "true")
    public ResponseEntity<Object> bulkDownloadOrders(
            @RequestParam(name = "order_ids", required = false) List<Long> orderIds,

            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,

            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,

            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {

        if (authService.getMerchantId() == null) {
            throw new EducationException(ErrorEnum.USER_IS_NOT_MERCHANT, "Incorrect Request");
        }

        if (endDate == null) {
            endDate = new Date();
        }

        String merchantId = authService.getMerchantId().toString();

        ValidationResult result = validateRequestOrderStartDate(orderIds, startDate);

        List<Long> claculatedOrderIds = new ArrayList<>();
        if (result == ValidationResult.ONLY_DATE_RANGE || result == ValidationResult.ONLY_ORDER) {
            claculatedOrderIds = sellerPanelService.getBulkOrders(merchantId, orderIds, startDate, endDate,
                    offset, limit)
                    .getData()
                    .stream()
                    .map(formData -> formData.getFormFulfilment().getOrderId())
                    .collect(Collectors.toList());
        } else {
            return new ResponseEntity<>(
                    "{\"statusCode:\": 400 , \"error:\" \"Enter either orderIds or startDate.\"}",
                    HttpStatus.BAD_REQUEST);
        }

        if (orderIds != null && claculatedOrderIds.size() != orderIds.size() || claculatedOrderIds.isEmpty()) {
            return new ResponseEntity<>(
                    "{\"statusCode:\": 404, \"error\": \"Order data not exist.\"}",
                    HttpStatus.NOT_FOUND);
        }

        for (Long orderId : claculatedOrderIds) {
            sellerPanelService.submitDownloadOrderRequest(
                    new DownloadOrder(merchantId, orderId)
            );
        }

        return new ResponseEntity<>(
                "{\"status\": \"File will be available on File center to download.\"}",
                HttpStatus.OK
        );
    }

    private List<SellerPanelResponse> getSellerPanelResponse(List<FormData> formDataList) throws Exception {
        return formDataList.stream()
                .map(SellerPanelResponse::new)
                .collect(Collectors.toList());
    }

    public ValidationResult validateRequestOrderStartDate(List<Long> orderIds, Date startDate) {
        boolean isOrderPresent = orderIds != null && orderIds.stream().anyMatch(Objects::nonNull);
        boolean isDatePresent = startDate != null;

        if (!isOrderPresent && !isDatePresent) {
            throw new EducationException(ErrorEnum.ORDER_ID_OR_START_DATE, "Incorrect Request");
        }

        return isOrderPresent ? ValidationResult.ONLY_ORDER : ValidationResult.ONLY_DATE_RANGE;
    }

    enum ValidationResult {
        ONLY_ORDER, ONLY_DATE_RANGE
    }

}
