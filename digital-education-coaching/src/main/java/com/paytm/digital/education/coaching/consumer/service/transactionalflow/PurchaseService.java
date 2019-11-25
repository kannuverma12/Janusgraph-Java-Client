package com.paytm.digital.education.coaching.consumer.service.transactionalflow;

import com.paytm.digital.education.cache.redis.RedisCacheService;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CheckoutCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.ConvTaxInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.MerchantNotifyCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.NotifyMerchantInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.TaxInfo;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantCommitRequest;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantNotifyRequest;
import com.paytm.digital.education.coaching.consumer.model.request.VerifyRequest;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.MerchantNotifyResponse;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.VerifyResponse;
import com.paytm.digital.education.coaching.utils.ComparisonUtils;
import com.paytm.digital.education.database.dao.CoachingCourseDAO;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.PurchaseException;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.CachingConstants.CACHE_KEY_DELIMITER;
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

@Service
@AllArgsConstructor
public class PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    private RedisCacheService redisCacheService;
    private CoachingCourseDAO coachingCourseDAO;
    private MerchantCall      merchantCall;

    public VerifyResponse verify(VerifyRequest request) throws PurchaseException {
        List<CartItem> cartItems = request.getCartItems();

        if (CollectionUtils.isEmpty(cartItems)) {
            log.error("Empty cart items received for verify: {}", request);
            throw PurchaseException
                    .builder()
                    .acknowledged(false)
                    .message("Empty cart items provided")
                    .cause(new BadRequestException(INVALID_CART_ITEMS))
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }

        List<Long> courseIds = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            courseIds.add(cartItem.getMetaData().getCourseId());
        }

        List<CoachingCourseEntity> coachingCourseEntities = coachingCourseDAO.findAllByCourseId(
                courseIds);

        if (CollectionUtils.isEmpty(coachingCourseEntities)) {
            log.error("No coaching courses found for course_ids: {}", courseIds);
            throw PurchaseException
                    .builder()
                    .acknowledged(false)
                    .message("Invalid Cart Items provided")
                    .cause(new BadRequestException(INVALID_CART_ITEMS))
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Map<Long, CoachingCourseEntity> courseIdToEntityMap = new HashMap<>();
        for (CoachingCourseEntity coachingCourseEntity : coachingCourseEntities) {
            courseIdToEntityMap.put(coachingCourseEntity.getCourseId(), coachingCourseEntity);
        }

        for (CartItem cartItem : cartItems) {
            Long courseId = cartItem.getMetaData().getCourseId();
            CoachingCourseEntity coachingCourseEntity = courseIdToEntityMap.get(courseId);

            boolean validCartItem = this.verifyCartItem(courseId, coachingCourseEntity, cartItem);
            if (!validCartItem) {
                throw PurchaseException
                        .builder()
                        .acknowledged(false)
                        .message("Invalid Cart Items provided")
                        .cause(new BadRequestException(INVALID_CART_ITEMS))
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .build();
            }

            boolean validData;
            if (coachingCourseEntity.getIsDynamic()) {
                CheckoutCartItem redisCartItem = this.getCartItemFromRedis(cartItem);
                validData = this.verifyPriceAndTaxes(cartItem, redisCartItem);
            } else {
                validData = this.verifyPriceAndTaxes(cartItem, coachingCourseEntity);
            }

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

    private boolean verifyCartItem(Long courseId, CoachingCourseEntity coachingCourseEntity,
            CartItem cartItem) {
        if (Objects.isNull(coachingCourseEntity)) {
            log.error("No coaching course found for course_id: {}", courseId);
            return false;
        }

        if (!coachingCourseEntity.getPaytmProductId().equals(cartItem.getProductId())) {
            log.error("Invalid Product Id for course_id: {}, DB p_id: {}, request p_id: {}",
                    courseId, coachingCourseEntity.getPaytmProductId(),
                    cartItem.getProductId());
            return false;
        }

        if (!coachingCourseEntity.getMerchantProductId()
                .equals(cartItem.getMetaData().getMerchantProductId())) {
            log.error(
                    "Invalid Merchant Product Id for course_id: {}, DB m_p_id: {}, request m_p_id: {}",
                    courseId, coachingCourseEntity.getMerchantProductId(),
                    cartItem.getMetaData().getMerchantProductId());
            return false;
        }

        if (!StringUtils.isEmpty(cartItem.getMetaData().getCourseType()) && !coachingCourseEntity
                .getCourseType().getText().equals(cartItem.getMetaData().getCourseType())) {
            log.error("Invalid Course type for course_id: {}, DB type: {}, request type: {}",
                    courseId, coachingCourseEntity.getCourseType().getText(),
                    cartItem.getMetaData().getCourseType());
            return false;
        }

        return true;
    }

    private CheckoutCartItem getCartItemFromRedis(CartItem cartItem) {
        String key = cartItem.getReferenceId() + CACHE_KEY_DELIMITER + cartItem.getMetaData()
                .getMerchantProductId() + CACHE_KEY_DELIMITER + cartItem.getProductId();

        String redisValue = redisCacheService.getValueFromCache(key);
        return JsonUtils.fromJson(redisValue, CheckoutCartItem.class);
    }

    private boolean verifyPriceAndTaxes(CartItem cartItem, CheckoutCartItem redisCartItem) {
        if (Objects.isNull(redisCartItem) || Objects.isNull(redisCartItem.getMetaData())) {
            log.error("Cart item not found in redis for cart item: {}", cartItem);
            return false;
        }

        if (cartItem.getQuantity() <= 0) {
            return false;
        }

        if (!cartItem.getProductId().equals(redisCartItem.getProductId())
                || !cartItem.getQuantity().equals(redisCartItem.getQuantity())
                || !ComparisonUtils.thresholdBasedFloatsComparison(cartItem.getBasePrice(),
                redisCartItem.getBasePrice())
                || !ComparisonUtils.thresholdBasedFloatsComparison(cartItem.getConvFee(),
                redisCartItem.getConvFee())
                || !ComparisonUtils.thresholdBasedFloatsComparison(cartItem.getTotalSellingPrice(),
                redisCartItem.getTotalSellingPrice())
                || !ComparisonUtils.thresholdBasedFloatsComparison(cartItem.getUnitSellingPrice(),
                redisCartItem.getUnitSellingPrice())
                || !cartItem.getEducationVertical().equals(redisCartItem.getEducationVertical())) {
            return false;
        }

        if (!StringUtils.isEmpty(cartItem.getCategoryId()) && !cartItem.getCategoryId()
                .equals(redisCartItem.getCategoryId())) {
            return false;
        }

        boolean validConvFeeTaxInfo = this.verifyConvFeeTaxInfo(
                cartItem.getMetaData().getConvTaxInfo(),
                redisCartItem.getMetaData().getConvTaxInfo());
        boolean validTaxInfo = this.verifyTaxInfo(cartItem.getMetaData().getTaxInfo(),
                redisCartItem.getMetaData().getTaxInfo());

        return (validConvFeeTaxInfo && validTaxInfo);
    }

    private boolean verifyConvFeeTaxInfo(ConvTaxInfo cartItemConvFeeTaxInfo,
            ConvTaxInfo redisItemConvFeeTaxInfo) {
        if (Objects.isNull(redisItemConvFeeTaxInfo)) {
            log.error("Conv fee tax info not present in redis for cart item: {}",
                    cartItemConvFeeTaxInfo);
            return false;
        }
        return (ComparisonUtils
                .thresholdBasedFloatsComparison(cartItemConvFeeTaxInfo.getTotalCGST(),
                        redisItemConvFeeTaxInfo.getTotalCGST())
                && ComparisonUtils
                .thresholdBasedFloatsComparison(cartItemConvFeeTaxInfo.getTotalSGST(),
                        redisItemConvFeeTaxInfo.getTotalSGST())
                && ComparisonUtils
                .thresholdBasedFloatsComparison(cartItemConvFeeTaxInfo.getTotalIGST(),
                        redisItemConvFeeTaxInfo.getTotalIGST())
                && ComparisonUtils
                .thresholdBasedFloatsComparison(cartItemConvFeeTaxInfo.getTotalUTGST(),
                        redisItemConvFeeTaxInfo.getTotalUTGST()));
    }

    private boolean verifyTaxInfo(TaxInfo cartItemTaxInfo, TaxInfo redisItemTaxInfo) {
        if (Objects.isNull(redisItemTaxInfo)) {
            log.error("Tax info not present in redis for cart item: {}", cartItemTaxInfo);
            return false;
        }
        return (ComparisonUtils.thresholdBasedFloatsComparison(cartItemTaxInfo.getTotalCGST(),
                redisItemTaxInfo.getTotalCGST())
                && ComparisonUtils.thresholdBasedFloatsComparison(cartItemTaxInfo.getTotalIGST(),
                redisItemTaxInfo.getTotalIGST())
                && ComparisonUtils.thresholdBasedFloatsComparison(cartItemTaxInfo.getTotalSGST(),
                redisItemTaxInfo.getTotalSGST())
                && ComparisonUtils.thresholdBasedFloatsComparison(cartItemTaxInfo.getTotalUTGST(),
                redisItemTaxInfo.getTotalUTGST()));
    }

    private boolean verifyPriceAndTaxes(CartItem cartItem,
            CoachingCourseEntity coachingCourseEntity) {
        if (Objects.isNull(coachingCourseEntity) || cartItem.getQuantity() <= 0) {
            return false;
        }

        //Verify Base Price
        float unitPrice = (float) (double) (coachingCourseEntity.getDiscountedPrice());
        float price = unitPrice * cartItem.getQuantity();
        if (!ComparisonUtils.thresholdBasedFloatsComparison(price, cartItem.getBasePrice())) {
            return false;
        }

        //Verify Conv Fee
        float convenienceFee = (price * CONVENIENCE_FEE_PERCENTAGE) / 100;
        if (!ComparisonUtils.thresholdBasedFloatsComparison(convenienceFee,
                cartItem.getConvFee())) {
            return false;
        }

        //Verify Unit Price
        float unitConvenienceFee = convenienceFee / cartItem.getQuantity();
        float unitSellingPrice = unitPrice + unitConvenienceFee;
        if (!ComparisonUtils
                .thresholdBasedFloatsComparison(unitSellingPrice, cartItem.getUnitSellingPrice())) {
            return false;
        }

        //Verify Selling Price
        float sellingPrice = price + convenienceFee;
        if (!ComparisonUtils.thresholdBasedFloatsComparison(sellingPrice,
                cartItem.getTotalSellingPrice())) {
            return false;
        }

        boolean validConvFeeTaxInfo = this.verifyConvFeeTaxInfo(cartItem, convenienceFee);
        boolean validTaxInfo = this.verifyTaxInfo(cartItem, price);

        return (validConvFeeTaxInfo && validTaxInfo);
    }

    private boolean verifyConvFeeTaxInfo(CartItem cartItem, float convenienceFee) {
        float convFeeIGST = (convenienceFee * CONVENIENCE_FEE_IGST_PERCENTAGE) / 100;
        float convFeeCGST = (convenienceFee * CONVENIENCE_FEE_CGST_PERCENTAGE) / 100;
        float convFeeSGST = (convenienceFee * CONVENIENCE_FEE_SGST_PERCENTAGE) / 100;
        float convFeeUTGST = (convenienceFee * CONVENIENCE_FEE_UTGST_PERCENTAGE) / 100;

        ConvTaxInfo convTaxInfo = cartItem.getMetaData().getConvTaxInfo();

        return (ComparisonUtils
                .thresholdBasedFloatsComparison(convFeeCGST, convTaxInfo.getTotalCGST())
                && ComparisonUtils
                .thresholdBasedFloatsComparison(convFeeIGST, convTaxInfo.getTotalIGST())
                && ComparisonUtils
                .thresholdBasedFloatsComparison(convFeeSGST, convTaxInfo.getTotalSGST())
                && ComparisonUtils
                .thresholdBasedFloatsComparison(convFeeUTGST, convTaxInfo.getTotalUTGST()));
    }

    private boolean verifyTaxInfo(CartItem cartItem, float price) {
        float itemIGST = (price * ITEM_IGST_PERCENTAGE) / 100;
        float itemCGST = (price * ITEM_CGST_PERCENTAGE) / 100;
        float itemSGST = (price * ITEM_SGST_PERCENTAGE) / 100;
        float itemUTGST = (price * ITEM_UTGST_PERCENTAGE) / 100;

        TaxInfo taxInfo = cartItem.getMetaData().getTaxInfo();

        return (ComparisonUtils.thresholdBasedFloatsComparison(itemCGST, taxInfo.getTotalCGST())
                && ComparisonUtils.thresholdBasedFloatsComparison(itemIGST, taxInfo.getTotalIGST())
                && ComparisonUtils.thresholdBasedFloatsComparison(itemSGST, taxInfo.getTotalSGST())
                && ComparisonUtils
                .thresholdBasedFloatsComparison(itemUTGST, taxInfo.getTotalUTGST()));
    }

    public MerchantNotifyResponse notify(MerchantNotifyRequest request) {
        List<MerchantNotifyCartItem> cartItems = request.getCartItems();

        if (CollectionUtils.isEmpty(cartItems)) {
            log.error("Empty cart items received: {}", request);
            throw new BadRequestException(INVALID_CART_ITEMS);
        }

        if (CollectionUtils.isEmpty(request.getMerchantData())) {
            log.error("Empty merchant data received from oms: {}", request);
            throw new BadRequestException(INVALID_MERCHANT_DATA);
        }

        long merchantId = request.getCartItems().get(0).getMerchantId();
        NotifyMerchantInfo merchantInfo = request.getMerchantData().get(merchantId);

        if (Objects.isNull(merchantInfo)) {
            log.error("Data for the merchant not received from oms for merchant_id: {} in: {}",
                    merchantId, request);
            throw new BadRequestException(INVALID_MERCHANT_DATA);
        }

        MerchantCommitRequest requestBody = merchantCall.getMerchantCommitRequestBody(cartItems,
                request.getUserData(), request.getOrderCreatedAt());

        return merchantCall.commitMerchantOrder(requestBody,
                merchantInfo);

    }
}
