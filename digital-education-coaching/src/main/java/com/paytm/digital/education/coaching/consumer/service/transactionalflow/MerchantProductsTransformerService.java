package com.paytm.digital.education.coaching.consumer.service.transactionalflow;

import com.paytm.digital.education.cache.redis.RedisCacheService;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CheckoutCartItem;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CheckoutMetaData;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.ConvTaxInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.TaxInfo;
import com.paytm.digital.education.coaching.consumer.model.request.FetchCartItemsRequestBody;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantData;
import com.paytm.digital.education.coaching.consumer.model.request.MerchantProduct;
import com.paytm.digital.education.coaching.consumer.model.response.transactionalflow.CartDataResponse;
import com.paytm.digital.education.database.dao.CoachingCourseDAO;
import com.paytm.digital.education.database.dao.CoachingInstituteDAO;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_VERTICAL_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.CachingConstants.CACHE_KEY_DELIMITER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.CachingConstants.CACHE_TTL;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_DYNAMIC;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_ENABLED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.MERCHANT_PRODUCT_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.PAYTM_MERCHANT_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_CART_ITEMS;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_MERCHANT_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_MERCHANT_PRODUCTS;

@Service
public class MerchantProductsTransformerService {

    private static final Logger log =
            LoggerFactory.getLogger(MerchantProductsTransformerService.class);

    @Value("${education.vertical.id}")
    private String educationVerticalId;

    @Value("${coaching.category.id}")
    private String coachingCategoryId;

    private static final List<String> INSTITUTE_FIELDS       =
            Arrays.asList("institute_id", "is_enabled");
    private static final List<String> COACHING_COURSE_FIELDS = Arrays.asList("paytm_product_id",
            "merchant_product_id", "is_enabled", "course_id", "course_type");


    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private CoachingInstituteDAO coachingInstituteDAO;

    @Autowired
    private CoachingCourseDAO coachingCourseDAO;

    public CartDataResponse fetchCartDataFromVertical(FetchCartItemsRequestBody request) {
        log.info("Got merchant product data request {}", request);
        if (StringUtils.isEmpty(request.getMerchantData())) {
            log.error("Empty merchant product data received : {}", request);
            throw new BadRequestException(INVALID_MERCHANT_PRODUCTS);
        }
        MerchantData merchantData =
                JsonUtils.fromJson(request.getMerchantData(), MerchantData.class);
        if (Objects.isNull(merchantData)) {
            log.error("Invalid merchant product data received : {}", request);
            throw new BadRequestException(INVALID_MERCHANT_PRODUCTS);
        }
        CoachingInstituteEntity coachingInstituteEntity = coachingInstituteDAO
                .findByPaytmMerchantId(PAYTM_MERCHANT_ID, request.getMerchantId().toString(),
                        INSTITUTE_FIELDS, INSTITUTE_FIELDS);

        if (Objects.isNull(coachingInstituteEntity) || !coachingInstituteEntity.getIsEnabled()) {
            log.error("No coaching institute found with merchant id: {}", request.getMerchantId());
            throw new BadRequestException(INVALID_MERCHANT_ID);
        }

        Map<String, CoachingCourseEntity> merchantProductIdToCourseMap = new HashMap<>();
        List<String> merchantProductIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(Objects.requireNonNull(merchantData).getProductList())) {
            for (MerchantProduct merchantProduct : merchantData.getProductList()) {
                merchantProductIds.add(merchantProduct.getProductId());
            }
        }

        List<CoachingCourseEntity> dynamicCoachingCourses =
                coachingCourseDAO.findByCoachingInstIdAndIsDynAndIsEnabledAndMerchantPIdsIn(
                        COACHING_INSTITUTE_ID, coachingInstituteEntity.getInstituteId(),
                        IS_DYNAMIC, true, IS_ENABLED, true,
                        MERCHANT_PRODUCT_ID, merchantProductIds, COACHING_COURSE_FIELDS,
                        COACHING_COURSE_FIELDS);
        if (CollectionUtils.isEmpty(dynamicCoachingCourses)) {
            log.error("Dynamic courses for merchant_id: {} does not exist",
                    request.getMerchantId());
            throw new BadRequestException(INVALID_MERCHANT_ID);
        }

        for (CoachingCourseEntity dynamicCoachingCourse : dynamicCoachingCourses) {
            merchantProductIdToCourseMap.put(dynamicCoachingCourse.getMerchantProductId(),
                    dynamicCoachingCourse);
        }

        List<CheckoutCartItem> cartItemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(Objects.requireNonNull(merchantData).getProductList())) {
            for (MerchantProduct merchantProduct : merchantData.getProductList()) {
                CoachingCourseEntity dynamicCoachingCourse =
                        merchantProductIdToCourseMap.get(merchantProduct.getProductId());
                Long courseId = dynamicCoachingCourse.getCourseId();
                if (Objects.isNull(courseId)) {
                    log.error("Dynamic coaching course_id is not present for merchantProduct: {}",
                            merchantProduct);
                    throw new BadRequestException(INVALID_CART_ITEMS);
                }
                String referenceId = UUID.randomUUID().toString();
                CheckoutCartItem cartItem = CheckoutCartItem.builder()
                        .totalSellingPrice(merchantProduct.getPrice() * merchantProduct.getQuantity())
                        .unitSellingPrice(merchantProduct.getPrice())
                        .productId(dynamicCoachingCourse.getPaytmProductId())
                        .categoryId(coachingCategoryId)
                        .educationVertical(COACHING_VERTICAL_NAME)
                        .quantity(merchantProduct.getQuantity())
                        .metaData(getMetaData(merchantProduct, request, dynamicCoachingCourse))
                        .referenceId(referenceId)
                        .basePrice(merchantProduct.getPrice())
                        .convFee(0F)
                        .build();
                cartItemList.add(cartItem);
                String cacheKey = getCacheKey(referenceId, dynamicCoachingCourse, merchantProduct);
                String cacheValue = JsonUtils.toJson(cartItem);
                redisCacheService.addKeyToCache(cacheKey, cacheValue, CACHE_TTL);
            }
        }
        return CartDataResponse.builder().cartItems(cartItemList).build();
    }

    private String getCacheKey(String referenceId, CoachingCourseEntity dynamicCoachingCourse,
            MerchantProduct merchantProduct) {
        return referenceId + CACHE_KEY_DELIMITER + merchantProduct.getProductId()
                + CACHE_KEY_DELIMITER + String.valueOf(dynamicCoachingCourse.getPaytmProductId());
    }

    private CheckoutMetaData getMetaData(MerchantProduct merchantProduct,
            FetchCartItemsRequestBody request, CoachingCourseEntity coachingCourseEntity) {

        TaxInfo taxInfo = null;
        if (Objects.nonNull(merchantProduct.getMerchantProductTaxData())) {
            taxInfo = TaxInfo.builder()
                    .totalCGST(Objects.nonNull(
                            merchantProduct.getMerchantProductTaxData().getTotalCGST())
                            ? merchantProduct.getMerchantProductTaxData().getTotalCGST() : 0F)
                    .totalIGST(Objects.nonNull(
                            merchantProduct.getMerchantProductTaxData().getTotalIGST())
                            ? merchantProduct.getMerchantProductTaxData().getTotalIGST() : 0F)
                    .totalSGST(Objects.nonNull(
                            merchantProduct.getMerchantProductTaxData().getTotalSGST())
                            ? merchantProduct.getMerchantProductTaxData().getTotalSGST() : 0F)
                    .totalUTGST(Objects.nonNull(
                            merchantProduct.getMerchantProductTaxData().getTotalUTGST())
                            ? merchantProduct.getMerchantProductTaxData().getTotalUTGST() : 0F)
                    .build();
        } else {
            taxInfo = TaxInfo.builder()
                    .totalCGST(0F)
                    .totalIGST(0F)
                    .totalSGST(0F)
                    .totalUTGST(0F)
                    .build();
        }

        ConvTaxInfo convTaxInfo = ConvTaxInfo.builder()
                .totalCGST(0F)
                .totalIGST(0F)
                .totalSGST(0F)
                .totalUTGST(0F)
                .build();

        return CheckoutMetaData
                .builder()
                .convTaxInfo(convTaxInfo)
                .taxInfo(taxInfo)
                .userId(request.getUserId())
                .merchantProductId(merchantProduct.getProductId())
                .courseId(coachingCourseEntity.getCourseId())
                .courseType(coachingCourseEntity.getCourseType().getText())
                .build();
    }

}
