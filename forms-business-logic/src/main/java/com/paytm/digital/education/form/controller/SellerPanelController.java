package com.paytm.digital.education.form.controller;


import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.ResponseData;
import com.paytm.digital.education.form.model.SellerPanelResponse;
import com.paytm.digital.education.form.service.SellerPanelService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/formfbl")
public class SellerPanelController {

    private SellerPanelService sellerPanelService;

    @GetMapping("/v1/orders")
    public ResponseEntity<Object> getInfo(
            @RequestParam("merchant_id")String merchantId,
            @RequestParam(name = "order_ids", required = false) List<String> orderIds,

            @RequestParam(name = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,

            @RequestParam(name = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,

            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ) {
        if (endDate == null) {
            endDate = new Date();
        }

        if (orderIds != null && orderIds.size() != 0) {
            List<SellerPanelResponse> sellerPanelResponses =
                    getSellerPanelResponse(sellerPanelService.getInfoOnOrderIds(merchantId, orderIds));

            return new ResponseEntity<>(new ResponseData<SellerPanelResponse>(sellerPanelResponses), HttpStatus.OK);

        } else if (startDate != null) {
            ResponseData<FormData> response = sellerPanelService
                    .getInfoOnDate(merchantId, startDate, endDate, offset, limit);

            ResponseData<SellerPanelResponse> responseData = new ResponseData<SellerPanelResponse>(
                    response.getCount(),
                    getSellerPanelResponse(response.getData())
            );

            return new ResponseEntity<>(responseData, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(
                    "{\"statusCode:\": 400 , \"error:\" \"Enter either orderIds or startDate.\"}",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private List<SellerPanelResponse> getSellerPanelResponse(List<FormData> formDataList) {
        return formDataList.stream()
                .map(SellerPanelResponse::new)
                .collect(Collectors.toList());
    }

}
