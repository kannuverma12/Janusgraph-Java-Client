package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.DownloadOrder;
import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.ResponseData;

import java.util.Date;
import java.util.List;

public interface SellerPanelService {

    List<FormData> getInfoOnOrderIds(String merchantId, List<Long> orderIds, Date startDate, Date endDate);

    ResponseData<FormData> getInfoOnDate(String merchantId, Date startDate, Date endDate, int offset, int limit);

    void submitDownloadOrderRequest(DownloadOrder downloadOrder);

    ResponseData<FormData> getBulkOrders(String merchantId, List<Long> orderIds, Date startDate, Date endDate,
                                         int offset, int limit);
}
