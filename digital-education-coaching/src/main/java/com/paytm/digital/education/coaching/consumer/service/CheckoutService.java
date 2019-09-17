package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.dto.CartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.CheckoutCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.CheckoutMetaData;
import com.paytm.digital.education.coaching.consumer.model.dto.MetaData;
import com.paytm.digital.education.coaching.consumer.model.dto.TCS;
import com.paytm.digital.education.coaching.consumer.model.request.CheckoutDataRequest;
import com.paytm.digital.education.coaching.consumer.model.response.CheckoutDataResponse;
import com.paytm.digital.education.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.DPIN;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.SAC;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.SPIN;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_CGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_IGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_SGST_PERCANTAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TransactionConstants.TCS_UTGST_PERCANTAGE;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_CART_ITEMS;

@Slf4j
@Service
public class CheckoutService {

    public CheckoutDataResponse checkoutData(CheckoutDataRequest request) {
        List<CheckoutCartItem> cartItems = request.getCartItems();

        if (CollectionUtils.isEmpty(cartItems)) {
            throw new BadRequestException(INVALID_CART_ITEMS);
        }

        List<CartItem> responseCartItems = new ArrayList<>();
        for (CheckoutCartItem cartItem : cartItems) {
            responseCartItems.add(CartItem
                    .builder()
                    .productId(cartItem.getProductId())
                    .qty(cartItem.getQty())
                    .basePrice(cartItem.getBasePrice())
                    .convFee(cartItem.getConvFee())
                    .sellingPrice(cartItem.getSellingPrice())
                    .categoryId(cartItem.getCategoryId())
                    .educationVertical(cartItem.getEducationVertical())
                    .referenceId(cartItem.getReferenceId())
                    .metaData(this.getMetaDataWithTCS(cartItem))
                    .build()
            );
        }

        return CheckoutDataResponse
                .builder()
                .cartItems(responseCartItems)
                .build();
    }

    private MetaData getMetaDataWithTCS(CheckoutCartItem cartItem) {
        CheckoutMetaData metaData = cartItem.getMetaData();

        if (Objects.isNull(metaData)) {
            throw new BadRequestException(INVALID_CART_ITEMS);
        }

        TCS tcs = TCS
                .builder()
                .basePrice(cartItem.getBasePrice() * 1000)
                .igst(TCS_IGST_PERCANTAGE)
                .cgst(TCS_CGST_PERCANTAGE)
                .sgst(TCS_SGST_PERCANTAGE)
                .utgst(TCS_UTGST_PERCANTAGE)
                .spin(SPIN)
                .dpin(DPIN)
                .sac(SAC)
                .agstin(null)
                .hsn(null)
                .cpin(null)
                .cgstin(null)
                .build();

        return MetaData
                .builder()
                .convTaxInfo(metaData.getConvTaxInfo())
                .taxInfo(metaData.getTaxInfo())
                .tcs(tcs)
                .courseType(metaData.getCourseType())
                .courseId(metaData.getCourseId())
                .userId(metaData.getUserId())
                .build();
    }
}
