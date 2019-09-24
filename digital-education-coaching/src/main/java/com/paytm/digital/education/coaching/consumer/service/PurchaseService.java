package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.CartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.ConvTaxInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.MerchantNotifyCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.MerchantOrderData;
import com.paytm.digital.education.coaching.consumer.model.dto.NotifyMerchantInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.TaxInfo;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantCommitRequest;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantNotifyRequest;
import com.paytm.digital.education.coaching.consumer.model.request.VerifyRequest;
import com.paytm.digital.education.coaching.consumer.model.response.MerchantNotifyResponse;
import com.paytm.digital.education.coaching.consumer.model.response.VerifyResponse;
import com.paytm.digital.education.coaching.db.dao.CoachingCourseDAO;
import com.paytm.digital.education.coaching.enums.MerchantNotifyFailureReason;
import com.paytm.digital.education.coaching.enums.MerchantNotifyStatus;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.PurchaseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_CGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_IGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_SGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.CONVENIENCE_FEE_UTGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.ITEM_CGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.ITEM_IGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.ITEM_SGST_PERCENTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.ITEM_UTGST_PERCENTAGE;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_CART_ITEMS;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_MERCHANT_DATA;

@Slf4j
@Service
@AllArgsConstructor
public class PurchaseService {

    private CoachingCourseDAO coachingCourseDAO;
    private MerchantCall      merchantCall;

    public VerifyResponse verify(VerifyRequest request) throws PurchaseException {
        List<CartItem> cartItems = request.getCartItems();

        if (CollectionUtils.isEmpty(cartItems)) {
            throw PurchaseException
                    .builder()
                    .acknowledged(false)
                    .message("Empty cart items provided")
                    .cause(new BadRequestException(INVALID_CART_ITEMS))
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }

        for (CartItem cartItem : cartItems) {
            Long courseId = cartItem.getMetaData().getCourseId();
            CoachingCourseEntity coachingCourseEntity = coachingCourseDAO.findByProgramId(courseId);
            boolean validData = this.verifyPriceAndTaxes(cartItem, coachingCourseEntity);

            if (!validData) {
                throw PurchaseException
                        .builder()
                        .acknowledged(false)
                        .message("Oops! The prices for the product/s you are buying have updated")
                        .cause(new BadRequestException(INVALID_CART_ITEMS))
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build();
            }
        }

        return VerifyResponse
                .builder()
                .acknowledged(true)
                .build();
    }

    private boolean verifyPriceAndTaxes(CartItem cartItem,
            CoachingCourseEntity coachingCourseEntity) {
        if (Objects.isNull(coachingCourseEntity)) {
            return false;
        }

        //Verify Base Price
        float price =
                (float) (double) (coachingCourseEntity.getOriginalPrice()) * cartItem.getQty();
        if (price != cartItem.getBasePrice()) {
            return false;
        }

        //Verify Conv Fee
        float convenienceFee = (price * CONVENIENCE_FEE_PERCENTAGE) / 100;
        if (cartItem.getConvFee() != convenienceFee) {
            return false;
        }

        //Verify Selling Price
        float sellingPrice = price + convenienceFee;
        if (cartItem.getSellingPrice() != sellingPrice) {
            return false;
        }

        boolean validConvFeeTaxInfo = this.verifyConvFeeTaxInfo(cartItem, convenienceFee);
        boolean validTaxInfo = this.verifyTaxInfo(cartItem, price);

        if (!validConvFeeTaxInfo || !validTaxInfo) {
            return false;
        }
        return true;
    }

    private boolean verifyConvFeeTaxInfo(CartItem cartItem, float convenienceFee) {
        float convFeeIGST = (convenienceFee * CONVENIENCE_FEE_IGST_PERCENTAGE) / 100;
        float convFeeCGST = (convenienceFee * CONVENIENCE_FEE_CGST_PERCENTAGE) / 100;
        float convFeeSGST = (convenienceFee * CONVENIENCE_FEE_SGST_PERCENTAGE) / 100;
        float convFeeUTGST = (convenienceFee * CONVENIENCE_FEE_UTGST_PERCENTAGE) / 100;

        ConvTaxInfo convTaxInfo = cartItem.getMetaData().getConvTaxInfo();

        if (convFeeIGST != convTaxInfo.getTotalIGST() || convFeeCGST != convTaxInfo.getTotalCGST()
                || convFeeSGST != convTaxInfo.getTotalSGST() || convFeeUTGST != convTaxInfo
                .getTotalUTGST()) {
            return false;
        }
        return true;
    }

    private boolean verifyTaxInfo(CartItem cartItem, float price) {
        float itemIGST = (price * ITEM_IGST_PERCENTAGE) / 100;
        float itemCGST = (price * ITEM_CGST_PERCENTAGE) / 100;
        float itemSGST = (price * ITEM_SGST_PERCENTAGE) / 100;
        float itemUTGST = (price * ITEM_UTGST_PERCENTAGE) / 100;

        TaxInfo taxInfo = cartItem.getMetaData().getTaxInfo();

        if (itemIGST != taxInfo.getTotalIGST() || itemCGST != taxInfo.getTotalCGST()
                || itemSGST != taxInfo.getTotalSGST() || itemUTGST != taxInfo.getTotalUTGST()) {
            return false;
        }
        return true;
    }

    public MerchantNotifyResponse notify(MerchantNotifyRequest request) {
        List<MerchantNotifyCartItem> cartItems = request.getCartItems();

        if (CollectionUtils.isEmpty(cartItems)) {
            throw new BadRequestException(INVALID_CART_ITEMS);
        }

        if (CollectionUtils.isEmpty(request.getMerchantData())) {
            throw new BadRequestException(INVALID_MERCHANT_DATA);
        }

        long merchantId = request.getCartItems().get(0).getMerchantId();
        NotifyMerchantInfo merchantInfo = request.getMerchantData().get(merchantId);

        if (Objects.isNull(merchantInfo)) {
            throw new BadRequestException(INVALID_MERCHANT_DATA);
        }

        MerchantCommitRequest requestBody = merchantCall.getMerchantCommitRequestBody(cartItems,
                request.getUserData());

        MerchantNotifyResponse response = merchantCall.commitMerchantOrder(requestBody,
                merchantInfo);

        double temp = Math.random();

        if (temp <= 0.5) {
            return MerchantNotifyResponse
                    .builder()
                    .status(MerchantNotifyStatus.SUCCESS)
                    .merchantResponse(MerchantOrderData
                            .builder()
                            .merchantReferenceId("abc123")
                            .build())
                    .build();
        } else if (temp <= 0.8) {
            return MerchantNotifyResponse
                    .builder()
                    .status(MerchantNotifyStatus.FAILURE)
                    .failureReason(MerchantNotifyFailureReason.MERCHANT_INFRA_DOWN)
                    .build();
        } else {
            return MerchantNotifyResponse
                    .builder()
                    .status(MerchantNotifyStatus.PENDING)
                    .build();
        }
    }
}
